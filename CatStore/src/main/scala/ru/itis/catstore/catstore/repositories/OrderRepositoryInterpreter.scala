package ru.itis.catstore.catstore.repositories

import cats.Functor
import cats.syntax.functor._
import cats.effect.MonadCancelThrow
import doobie.implicits._
import doobie.util.transactor.Transactor
import ru.itis.catstore.catstore.Order
import ru.itis.catstore.users.User

class OrderRepositoryInterpreter[F[_] : MonadCancelThrow : Functor](val transactor: Transactor[F]) extends OrderRepositoryAlgebra[F] {

  def insert(catId: Long, userId: Long): doobie.Update0 =
    sql"INSERT INTO cat_order (customer, cat_id) VALUES($userId, $catId)".update

  def update(id: Long, payed: Boolean): doobie.Update0 =
    sql"UPDATE cat_order SET payed = $payed WHERE id=$id".update

  def selectById(orderId: Long): doobie.Query0[Order] =
    sql"SELECT * FROM cat_order WHERE id=$orderId".query[Order]

  def selectByOwnerId(ownerId: Long): doobie.Query0[Order] =
    sql"SELECT * FROM cat_order WHERE customer=$ownerId".query[Order]

  def deleteById(orderId: Long): doobie.Update0  =
    sql"DELETE FROM cat_order WHERE id=$orderId".update

  override def save(order: Order, user: User): F[Unit] =
    insert(order.cat_id, user.id).run.transact(transactor).as(())

  override def update(payed: Boolean, orderId: Long): F[Unit] =
    update(orderId, payed).run.transact(transactor).as(())

  override def getById(orderId: Long): F[Option[Order]] =
    selectById(orderId).option.transact(transactor)

  override def delete(orderId: Long): F[Unit] =
    deleteById(orderId).run.transact(transactor).as(())

  override def getUserOrders(userId: Long): F[List[Order]] =
    selectByOwnerId(userId).to[List].transact(transactor)
}

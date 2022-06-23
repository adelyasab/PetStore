package ru.itis.catstore.catstore.repositories

import cats.Functor
import cats.effect.MonadCancelThrow
import cats.syntax.functor._
import doobie.implicits._
import doobie.util.transactor.Transactor
import ru.itis.catstore.catstore.Cat

class CatRepositoryInterpreter[F[_] : MonadCancelThrow : Functor](val transactor: Transactor[F]) extends CatRepositoryAlgebra[F] {

  def insert(name: String, color: String, price: Float): doobie.Update0 =
    sql"INSERT INTO cat(cat_name, color, price) VALUES($name, $color, $price)".update

  def update(id: Long, sold: Boolean): doobie.Update0 =
    sql"UPDATE cat SET sold = $sold WHERE id=$id".update

  def selectById(catId: Long): doobie.Query0[Cat] =
    sql"SELECT * FROM cat WHERE id=$catId".query[Cat]

  def selectByColor(color: String): doobie.Query0[Cat] =
    sql"SELECT * FROM cat WHERE color=$color AND sold=FALSE".query[Cat]

  def deleteById(catId: Long): doobie.Update0  =
    sql"DELETE FROM cat WHERE id=$catId".update

  override def save(cat: Cat): F[Unit] =
    insert(cat.name, cat.color, cat.price).run.transact(transactor).as(())

  override def getById(catId: Long): F[Option[Cat]] =
    selectById(catId).option.transact(transactor)

  override def getByColor(catColor: String): F[List[Cat]] =
    selectByColor(catColor).to[List].transact(transactor)

  override def delete(catId: Long): F[Unit] =
    deleteById(catId).run.transact(transactor).as(())

  override def update(cat: Cat): F[Unit] =
    update(cat.id, cat.sold).run.transact(transactor).as(())
}

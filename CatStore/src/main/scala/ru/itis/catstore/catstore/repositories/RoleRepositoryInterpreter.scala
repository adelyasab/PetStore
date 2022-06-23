package ru.itis.catstore.catstore.repositories

import cats.Functor
import cats.effect.MonadCancelThrow
import cats.syntax.functor._
import doobie.implicits._
import doobie.util.transactor.Transactor
import ru.itis.catstore.catstore.Role
import ru.itis.catstore.catstore.Role.strMapping


class RoleRepositoryInterpreter[F[_] : MonadCancelThrow : Functor](val transactor: Transactor[F]) extends RoleRepositoryAlgebra[F] {

  def insert(userId: Long, role: String): doobie.Update0 =
    sql"INSERT INTO roles (user_id, user_role) VALUES($userId, $role)".update

  def selectById(userId: Long): doobie.Query0[Role] =
    sql"SELECT * FROM roles WHERE user_id=$userId".query[Role]

  def deleteById(userId: Long): doobie.Update0  =
    sql"DELETE FROM roles WHERE user_id=$userId".update


  override def addRole(userId: Long, role: Role): F[Unit] =
    insert(userId, role.user_role).run.transact(transactor).as(())

  override def getRole(userId: Long): F[Option[Role]] =
    selectById(userId).option.transact(transactor)

  override def removeRole(userId: Long): F[Unit] =
    deleteById(userId).run.transact(transactor).as(())
}

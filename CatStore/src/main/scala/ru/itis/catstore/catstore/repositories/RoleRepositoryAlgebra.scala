package ru.itis.catstore.catstore.repositories

import ru.itis.catstore.catstore.Role

trait RoleRepositoryAlgebra[F[_]] {
  def addRole(userId: Long, role: Role): F[Unit]

  def getRole(userId: Long): F[Option[Role]]

  def removeRole(userId: Long): F[Unit]
}
object RoleRepositoryAlgebra {
  def apply[F[_]](implicit rr: RoleRepositoryAlgebra[F]): RoleRepositoryAlgebra[F] = rr
}

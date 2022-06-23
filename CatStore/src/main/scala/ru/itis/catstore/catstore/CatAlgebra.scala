package ru.itis.catstore.catstore

import ru.itis.catstore.users.User

trait CatAlgebra[F[_]] {
  def create(user: User, cat: Cat): F[Unit]

  def edit(user: User, sold: Boolean, catId: Long): F[Unit]

  def delete(user: User, catId: Long): F[Unit]

  def getCatById(user: User, catId: Long): F[Option[Cat]]

  def getCatByColor(user: User, color: String): F[List[Cat]]

}
object CatAlgebra {
  def apply[F[_]](implicit catAlgebra: CatAlgebra[F]): CatAlgebra[F] = catAlgebra
}

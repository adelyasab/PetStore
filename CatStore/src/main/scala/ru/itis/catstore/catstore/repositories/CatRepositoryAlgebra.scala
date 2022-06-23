package ru.itis.catstore.catstore.repositories

import ru.itis.catstore.catstore.Cat

trait CatRepositoryAlgebra[F[_]] {
  def save(cat: Cat): F[Unit]

  def update(cat: Cat): F[Unit]

  def getById(catId: Long): F[Option[Cat]]

  def getByColor(color: String): F[List[Cat]]

  def delete(catId: Long): F[Unit]

}
object CatRepositoryAlgebra {
  def apply[F[_]](implicit cr: CatRepositoryAlgebra[F]): CatRepositoryAlgebra[F] = nr
}

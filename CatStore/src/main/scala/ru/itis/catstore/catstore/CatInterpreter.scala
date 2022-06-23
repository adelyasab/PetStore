package ru.itis.catstore.catstore

import cats.{ApplicativeError, Monad}
import cats.implicits.{catsSyntaxFlatMapOps, toFlatMapOps}
import ru.itis.catstore.catstore.repositories.{CatRepositoryAlgebra, RoleRepositoryAlgebra}
import ru.itis.catstore.users.User

class CatInterpreter
[F[_]
: CatRepositoryAlgebra
: RoleRepositoryAlgebra
: Monad
: ApplicativeError[*[_], Throwable]
] extends CatAlgebra[F] {
  override def create(user: User, cat: Cat): F[Unit] = {
    isAdmin(user.id) >>
    CatRepositoryAlgebra[F].save(cat)
  }

  override def edit(user: User, sold: Boolean, catId: Long): F[Unit] =
    isAdmin(user.id) >>
      CatRepositoryAlgebra[F].update(Cat(catId, "", "", sold, 0.0f))

  override def delete(user: User, catId: Long): F[Unit] =
    isAdmin(user.id) >>
      CatRepositoryAlgebra[F].delete(catId)

  override def getCatById(user: User, catId: Long): F[Option[Cat]] =
    CatRepositoryAlgebra[F].getById(catId)

  override def getCatByColor(user: User, color: String): F[List[Cat]] =
    CatRepositoryAlgebra[F].getByColor(color)

  private def isAdmin(userId: Long): F[Unit] = {
    RoleRepositoryAlgebra[F].getRole(userId).flatMap {
      case Some(value) if value.user_role == Admin().value => Monad[F].pure(())
      case _ => ApplicativeError[F, Throwable].raiseError(new AccessError)
    }
  }
}

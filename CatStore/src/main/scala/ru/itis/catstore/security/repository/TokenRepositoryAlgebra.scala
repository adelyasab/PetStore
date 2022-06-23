package ru.itis.catstore.security.repository

import cats.data.OptionT
import ru.itis.catstore.security.AccessToken
import ru.itis.catstore.users.User

trait TokenRepositoryAlgebra[F[_]] {
  def createToken(user: User): F[AccessToken]

  def getUserByToken(token: AccessToken): OptionT[F, User]
}

object TokenRepositoryAlgebra {
  def apply[F[_]](implicit tr: TokenRepositoryAlgebra[F]): TokenRepositoryAlgebra[F] = tr
}
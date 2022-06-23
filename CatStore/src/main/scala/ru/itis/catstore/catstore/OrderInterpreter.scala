package ru.itis.catstore.catstore

import cats.{Functor, Monad}
import cats.syntax.functor._
import cats.implicits.{catsSyntaxFlatMapOps, toFlatMapOps}
import ru.itis.catstore.catstore.repositories.{CatRepositoryAlgebra, OrderRepositoryAlgebra}
import ru.itis.catstore.users.User

class OrderInterpreter[F[_]: OrderRepositoryAlgebra : CatRepositoryAlgebra : Monad : Functor] extends OrderAlgebra[F] {
  override def create(user: User, order: Order): F[Unit] =
    OrderRepositoryAlgebra[F].save(order, user)

  override def payForOrder(user: User, orderId: Long): F[Unit] =
    OrderRepositoryAlgebra[F].getById(orderId).map(_.get).flatMap { value =>
      OrderRepositoryAlgebra[F].update(payed = true, orderId) >>
        CatRepositoryAlgebra[F].update(Cat(value.cat_id, "", "", sold = true, 0.0f))
    }

  override def getUserOrders(user: User): F[List[Order]] =
    OrderRepositoryAlgebra[F].getUserOrders(user.id)
}

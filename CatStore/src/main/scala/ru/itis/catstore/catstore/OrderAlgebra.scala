package ru.itis.catstore.catstore

import ru.itis.catstore.users.User

trait OrderAlgebra[F[_]] {
  def create(user: User, order: Order): F[Unit]

  def payForOrder(user: User, orderId: Long): F[Unit]

  def getUserOrders(user: User): F[List[Order]]
}
object OrderAlgebra {
  def apply[F[_]](implicit orderAlgebra: OrderAlgebra[F]): OrderAlgebra[F] = orderAlgebra
}


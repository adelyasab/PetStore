package ru.itis.catstore.catstore.repositories

import ru.itis.catstore.catstore.Order
import ru.itis.catstore.users.User

trait OrderRepositoryAlgebra[F[_]] {
  def save(order: Order, user: User): F[Unit]

  def update(payed: Boolean, orderId: Long): F[Unit]

  def getById(orderId: Long): F[Option[Order]]

  def delete(orderId: Long): F[Unit]

  def getUserOrders(userId: Long): F[List[Order]]
}
object OrderRepositoryAlgebra {
  def apply[F[_]](implicit or: OrderRepositoryAlgebra[F]): OrderRepositoryAlgebra[F] = or
}

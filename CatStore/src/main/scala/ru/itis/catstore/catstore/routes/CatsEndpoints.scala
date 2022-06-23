package ru.itis.catstore.catstore.routes

import cats.effect.Concurrent
import cats.implicits.{catsSyntaxFlatMapOps, toFlatMapOps, toFunctorOps}
import cats.{ApplicativeError, Functor, Monad}
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.{AuthedRoutes, EntityDecoder}
import ru.itis.catstore.catstore.{Cat, CatAlgebra, Order, OrderAlgebra}
import ru.itis.catstore.users.User

class CatsEndpoints[F[_]
: Monad
: CatAlgebra
: OrderAlgebra
: Functor
: ApplicativeError[*[_], Throwable]
: Concurrent
] extends Http4sDsl[F] {
  def catsEndpoints(): AuthedRoutes[User, F] = AuthedRoutes.of[User, F] {
    case req@POST -> Root / "addCat" as user =>
      req.req.as[AddCatForm]
      .flatMap {
        value => CatAlgebra[F].create(user, Cat(0, value.name, value.color, false, value.price))
      } >> Ok()

    case GET -> Root / "getCat" / color as user =>
      CatAlgebra[F].getCatByColor(user, color).flatMap(value => Ok(value))

    case POST -> Root / "createOrder" / catId as user =>
      OrderAlgebra[F].create(user, Order(0, user.id, false, catId.toLong))

    case GET -> Root / "pay" / orderId as user =>
      OrderAlgebra[F].payForOrder(user, orderId.toLong) >> Ok()

  }

  implicit val addCatFormDecoder: Decoder[AddCatForm] = deriveDecoder

  implicit val addCatFormEntityEncoder: EntityDecoder[F, AddCatForm] = jsonOf

  case class AddCatForm(name: String, color: String, price: Float)


}

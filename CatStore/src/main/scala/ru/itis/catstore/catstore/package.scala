package ru.itis.catstore

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

package object catstore {
  implicit def catEncoder: Encoder[Cat] = deriveEncoder
  implicit def catListEncoder: Encoder[List[Cat]] = deriveEncoder
  implicit def catEntityEncoder[F[_]]: EntityEncoder[F, Cat] = jsonEncoderOf
  implicit def catListEntityEncoder[F[_]]: EntityEncoder[F, List[Cat]] = jsonEncoderOf

  implicit def orderEncoder: Encoder[Order] = deriveEncoder
  implicit def orderListEncoder: Encoder[List[Order]] = deriveEncoder
  implicit def orderEntityEncoder[F[_]]: EntityEncoder[F, Order] = jsonEncoderOf
  implicit def orderListEntityEncoder[F[_]]: EntityEncoder[F, List[Order]] = jsonEncoderOf
}

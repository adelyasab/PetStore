package ru.itis.catstore.builders

import cats.Functor
import cats.effect._
import cats.implicits.toSemigroupKOps
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.{Router, Server}
import ru.itis.catstore.config.ScalagramConfig
import ru.itis.catstore.catstore.repositories._
import ru.itis.catstore.catstore.routes.CatsEndpoints
import ru.itis.catstore.catstore.{CatAlgebra, CatInterpreter, OrderAlgebra, OrderInterpreter}
import ru.itis.catstore.security.repository.TokenRepositoryInterpreter
import ru.itis.catstore.security.routes.SecurityEndpoints
import ru.itis.catstore.security.{AccessTokenSecurityInterpreter, Middleware, PasswordSecurityInterpreter}
import ru.itis.catstore.users.{UserRepositoryAlgebra, UserRepositoryInterpreter, UserValidatorImpl}

import scala.io.Source

class ServerBuilder[F[_]: Async : Sync : Functor] {

  def buildServer: Resource[F, Server] =
    for {
      conf <- ScalagramConfig.getConfig
      _ <- Resource.eval(DatabaseServicesBuilder.migrateDatabase(conf.db))
      transactor <- DatabaseServicesBuilder.transactor[F](conf.db)
      implicit0(userRepository: UserRepositoryAlgebra[F]) = new UserRepositoryInterpreter[F](transactor)
      implicit0(tokenRepository: TokenRepositoryInterpreter[F]) = new TokenRepositoryInterpreter[F](transactor)
      implicit0(passwordSecurityAlgebra: PasswordSecurityInterpreter[F]) = new PasswordSecurityInterpreter[F]
      implicit0(tokenSecurityAlgebra: AccessTokenSecurityInterpreter[F]) = new AccessTokenSecurityInterpreter[F]
      securityEndpoints = new SecurityEndpoints[F](new UserValidatorImpl)
      implicit0(ora: OrderRepositoryAlgebra[F]) = new OrderRepositoryInterpreter[F](transactor)
      implicit0(cra: CatRepositoryAlgebra[F]) = new CatRepositoryInterpreter[F](transactor)
      implicit0(rra: RoleRepositoryAlgebra[F]) = new RoleRepositoryInterpreter[F](transactor)
      implicit0(oa: OrderAlgebra[F]) = new OrderInterpreter[F]
      implicit0(ca: CatAlgebra[F]) = new CatInterpreter[F]
      authMiddleware = new Middleware[F].authMiddleware
      notesEndpoints = new CatsEndpoints[F]
      httpApp = Router(
        "/" -> (securityEndpoints.getEndpoints <+> authMiddleware(notesEndpoints.notesEndpoints()))
      ).orNotFound
      server <- BlazeServerBuilder[F]
        .bindHttp(port = conf.http.port)
        .withHttpApp(httpApp)
        .resource
    } yield server


}

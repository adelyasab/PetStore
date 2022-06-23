package ru.itis.catstore.builders

import cats.{Functor, Monad}
import cats.effect.{MonadCancelThrow, Resource, Sync}
import cats.effect.kernel.Async
import cats.effect.std.UUIDGen
import org.flywaydb.core.Flyway
import cats.syntax.functor._
import doobie.ExecutionContexts
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import org.flywaydb.core.api.Location
import ru.itis.catstore.config.DatabaseConfig
import ru.itis.catstore.security.repository.TokenRepositoryInterpreter
import ru.itis.catstore.users.UserRepositoryInterpreter

object DatabaseServicesBuilder {

  def migrateDatabase[F[_] : Sync](databaseConfig: DatabaseConfig): F[Unit] = {
    Sync[F].delay {
      Flyway.configure()
        .dataSource(databaseConfig.url, databaseConfig.user, databaseConfig.password)
        .locations(new Location(databaseConfig.migrationsPath))
        .baselineOnMigrate(true)
        .load()
        .migrate()
    }.as {
      ()
    }
  }

  def transactor[F[_] : Async](databaseConfig: DatabaseConfig): Resource[F, Transactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](databaseConfig.poolConnections)
      transactor <- HikariTransactor.newHikariTransactor[F](
        databaseConfig.driverClass,
        databaseConfig.url,
        databaseConfig.user,
        databaseConfig.password,
        ce
      )
    } yield transactor


}

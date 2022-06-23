package ru.itis.catstore

import cats.effect.{ExitCode, IO, IOApp}
import ru.itis.catstore.builders.ServerBuilder


object Server extends IOApp{

  override def run(args: List[String]): IO[ExitCode] = new ServerBuilder[IO].buildServer.use(_ => IO.never).as(ExitCode.Success)
}

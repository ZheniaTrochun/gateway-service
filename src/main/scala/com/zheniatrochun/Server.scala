package com.zheniatrochun

//#quick-start-server
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.zheniatrochun.api.{AdminRoutes, CorsRoutes, HealthRoutes, HttpGatewayRoutes}
import com.zheniatrochun.config.AppConfig
import com.zheniatrochun.directives.CorsDirectives

//#main-class
object Server extends App
  with CorsDirectives
  with SprayJsonSupport
  with HealthRoutes
  with AppConfig {

  // set up ActorSystem and other dependencies here
  //#main-class
  //#server-bootstrapping
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  //#server-bootstrapping

  //#main-class
  // from the UserRoutes trait
  //#main-class
  val gatewayRoutes = new HttpGatewayRoutes()
  val corsRoutes = new CorsRoutes()
  val adminRoutes = new AdminRoutes()

  val routes =
    defaultCORSHeaders {
      options {
        complete(StatusCodes.OK -> None)
      } ~
      adminRoutes.routes ~
      gatewayRoutes.gatewayRoutes ~
      healthRoutes
    }
  //#http-server
  Http().bindAndHandle(logRequestResult("log")(routes), interface, port)

  Await.result(system.whenTerminated, Duration.Inf)
}

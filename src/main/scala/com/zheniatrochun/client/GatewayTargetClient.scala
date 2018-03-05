package com.zheniatrochun.client

import akka.actor.ActorSystem
import akka.http.javadsl.server.directives.HeaderDirectives
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.{ RequestContext, Route, RouteResult }
import akka.stream.Materializer
import akka.stream.scaladsl.{ Sink, Source }
import com.zheniatrochun.directives.CorsDirectives
import com.zheniatrochun.utils.HeaderUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GatewayTargetClient(val host: String)(implicit val system: ActorSystem, implicit val mat: Materializer) extends HeaderUtils with CorsDirectives {

  private val API_PREFIX = "api"
  private val connector = Http().outgoingConnection(host)

  val routes = Route { context =>
    proxyRequest(context)
  }

  private def proxyRequest(context: RequestContext): Future[RouteResult] = {
    val request = context.request
    val originalHeaders = request.headers.toList
    val filteredHeaders =
      (Host(host)
        :: originalHeaders - Host)
        .nonEmptyRequestHeaders

    val proxiedRequest = request.copy(
      uri = createUri(context.request.uri),
      headers = filteredHeaders
    )

    Source.single(proxiedRequest)
      .via(connector)
      .runWith(Sink.head)
      .flatMap(context.complete(_))
  }

  private def createUri(uri: Uri): Uri = {
    val path = uri.path.dropChars(API_PREFIX.length + 1)
    println(s"path = $path")

    val res = uri.withHost(host)
      .withPath(path)
      .withQuery(uri.query())
      .withPort(0)

    println(res.toString)

    res
  }
}

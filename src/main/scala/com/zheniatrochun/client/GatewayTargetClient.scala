package com.zheniatrochun.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import com.zheniatrochun.config.AppConfig
import com.zheniatrochun.directives.CorsDirectives
import com.zheniatrochun.utils.HeaderUtils
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GatewayTargetClient(val host: String)
                         (implicit val system: ActorSystem, implicit val mat: Materializer)
  extends HeaderUtils
    with CorsDirectives
    with AppConfig {

  private val API_PREFIX = config.getString("api.prefix")
  private val connector = Http().outgoingConnection(host)
  private val logger = LoggerFactory.getLogger(this.getClass)

  val routes = Route { context =>
    proxyRequest(context)
  }

  private def proxyRequest(context: RequestContext): Future[RouteResult] = {
    val request = context.request
    val originalHeaders = request.headers.toList

    logger.debug(s"Started proxy operation on request for ${request.uri}")

    val filteredHeaders =
      (Host(host)
        :: originalHeaders - Host)
      .nonEmptyRequestHeaders

    val proxiedRequest = request.copy(
      uri = createUri(context.request.uri),
      headers = filteredHeaders
    )

    logger.debug(s"Redirection url = ${proxiedRequest.uri}")

    Source.single(proxiedRequest)
      .via(connector)
      .runWith(Sink.head)
      .flatMap(context.complete(_))
  }

  private def createUri(uri: Uri): Uri = {
    uri.withHost(host)
      .withPath(uri.path.dropChars(API_PREFIX.length + 1))
      .withQuery(uri.query())
      .withPort(0)
  }
}

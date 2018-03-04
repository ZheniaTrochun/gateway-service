package com.zheniatrochun.api

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.RouteResult
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route}
import akka.stream.scaladsl.{Sink, Source}
import com.zheniatrochun.utils.HeaderUtils

import scala.concurrent.Future

class GatewayTargetClient(val host: String) extends HeaderUtils {

  private val API_PREFIX = "/api"
  private val connector = Http().outgoingConnection(host)

  val routes = Route { context =>
    proxyRequest(context)
  }


  private def proxyRequest(context: RequestContext): Future[RouteResult] = {
    val request = context.request
    val originalHeaders = request.headers.toList
    val filteredHeaders = (Host(host) :: originalHeaders - Host).nonEmptyHeaders

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
    uri.withHost(host)
      .withPath(uri.path.dropChars(API_PREFIX.length + 1))
      .withQuery(uri.query())
      .withPort(0)
  }
}

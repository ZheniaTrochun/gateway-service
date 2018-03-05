package com.zheniatrochun.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{ RequestContext, Route }
import akka.stream.Materializer
import com.zheniatrochun.directives.GatewayTargetDirective

class HttpGatewayRoutes(implicit val system: ActorSystem, implicit val mat: Materializer) extends GatewayTargetDirective {

  val gatewayRoutes: Route = (ctx: RequestContext) => serviceRouteForRequest("api")(_.routes)(ctx)
}

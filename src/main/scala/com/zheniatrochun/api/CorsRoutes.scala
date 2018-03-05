package com.zheniatrochun.api

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives
import com.zheniatrochun.directives.CorsDirectives

class CorsRoutes extends Directives with CorsDirectives with SprayJsonSupport {

  val routes =
    defaultCORSHeaders {
      options {
        complete(StatusCodes.OK -> None)
      }
    }
}


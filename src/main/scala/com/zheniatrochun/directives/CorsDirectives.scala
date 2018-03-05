package com.zheniatrochun.directives

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.HttpMethods._

import scala.concurrent.duration._

trait CorsDirectives {
  val ALLOWED_HEADERS = Seq(
    "Origin",
    "X-Requested-With",
    "Content-Type",
    "Accept",
    "Accept-Encoding",
    "Accept-Language",
    "Host",
    "Referer",
    "User-Agent",
    "Overwrite",
    "Destination",
    "Depth",
    "X-Token",
    "X-File-Size",
    "If-Modified-Since",
    "X-File-Name",
    "Cache-Control",
    "x-api-key",
    "x-api-version",
    "x-cpy-trace-token",

    "Authentication",
    "User",
    "Timeout-Access"
  )

  def defaultCORSHeaders = respondWithHeaders(
    `Access-Control-Allow-Origin`.*,
    `Access-Control-Allow-Methods`(GET, POST, OPTIONS, DELETE,
      CONNECT, DELETE, HEAD, PATCH, PUT, TRACE),
    `Access-Control-Allow-Headers`(ALLOWED_HEADERS.mkString(", ")),
    `Access-Control-Allow-Credentials`(allow = true),
    `Access-Control-Max-Age`(1.hour.toSeconds)
  )
}

package com.zheniatrochun.api

import akka.http.scaladsl.server.Directives._
import com.zheniatrochun.config.AppConfig

class AdminRoutes extends AppConfig {
  val routes =
    pathPrefix("admin") {
      pathPrefix("config") {
        path("create-dummy") {
          config.createDummyConfig()
          complete("Created")
        }
      }
    }
}

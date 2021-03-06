package com.zheniatrochun.utils

import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.ModeledCompanion

trait HeaderUtils {

  implicit class RichHeaders(headers: List[HttpHeader]) {
    def -[T](header: ModeledCompanion[T]): List[HttpHeader] = {
      headers.filterNot(_.is(header.lowercaseName))
    }

    def nonEmptyRequestHeaders = {
      headers.filterNot(header => header.value.isEmpty || header.name == "Timeout-Access")
    }
  }
}

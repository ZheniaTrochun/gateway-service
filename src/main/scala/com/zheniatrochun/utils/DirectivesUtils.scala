package com.zheniatrochun.utils

import akka.http.scaladsl.model.Uri.Path.{Empty, Segment, Slash}
import akka.http.scaladsl.server.PathMatcher1
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.server.PathMatcher.{Matching, Unmatched}
import com.typesafe.config.Config
import akka.http.scaladsl.server.{Directive, Directives}
import com.zheniatrochun.api.GatewayTargetClient

import scala.annotation.tailrec

class DirectivesUtils {

}

trait GatewayTargetDirective extends Directives {
  def serviceRouteForRequest(config: Config, prefix: String): Directive[Tuple1[GatewayTargetClient]] = {
    pathPrefix(GatewayPathMatcher(config, prefix)).flatMap(provide)
  }
}

//TODO finish
case class GatewayPathMatcher(config: Config, prefix: String) extends PathMatcher1[GatewayTargetClient] {
  def apply(path: Path): Matching[Tuple1[GatewayTargetClient]] = matchPathToGatewayTarget(path)

  @tailrec
  private def matchPathToGatewayTarget(path: Path): Matching[Tuple1[GatewayTargetClient]] = path match {
    case Empty => Unmatched
    case Segment(apiPrefix, tail) if apiPrefix == prefix => matchRemainingPathToGatewayTarget(tail)
    case Segment(head, tail) => matchPathToGatewayTarget(tail)
    case Slash(tail) => matchRemainingPathToGatewayTarget(tail)
  }

  @tailrec
  private def matchRemainingPathToGatewayTarget(path: Path): Matching[Tuple1[GatewayTargetClient]] = path match {
    case Slash(tail) => matchRemainingPathToGatewayTarget(tail)
    case s @ Segment(head, _) =>
    case _ => Unmatched
  }

}
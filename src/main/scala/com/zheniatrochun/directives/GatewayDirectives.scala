package com.zheniatrochun.directives

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model.Uri.Path.{Empty, Segment, Slash}
import akka.http.scaladsl.server.PathMatcher.{Matched, Matching, Unmatched}
import akka.http.scaladsl.server.{Directive, Directives, PathMatcher1}
import akka.stream.Materializer
import com.typesafe.config.Config
import com.zheniatrochun.client.GatewayTargetClient
import com.zheniatrochun.config.{AppConfig, Configs}

import scala.annotation.tailrec

trait GatewayTargetDirective extends Directives with AppConfig {

  implicit val system: ActorSystem
  implicit val mat: Materializer

  def serviceRouteForRequest(prefix: String): Directive[Tuple1[GatewayTargetClient]] = {
    pathPrefix(GatewayPathMatcher(config, prefix)).flatMap(provide)
  }
}

case class GatewayPathMatcher(config: Configs, prefix: String)(implicit val system: ActorSystem, implicit val mat: Materializer) extends PathMatcher1[GatewayTargetClient] {
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
      println(head)
      Option(config.getString(head))
        .map(str => Matched(s, Tuple1(new GatewayTargetClient(config.getString(head)))))
        .getOrElse(Unmatched)
    case _ => Unmatched
  }
}
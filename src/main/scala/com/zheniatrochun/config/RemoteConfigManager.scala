package com.zheniatrochun.config

import java.net.URI

import com.typesafe.config.Config

private[config] class RemoteConfigManager(val config: Config) {
  private val redisClient = new RedisClient(new URI(config.getString("redis.url")))

  def getRemoteConfig(): Map[String, String] = {
    redisClient.hgetall1[String, String]("data-service-config")
      .getOrElse(Map.empty[String, String])
  }

  def createDummyConfig(): Unit = {
    val conf = Map("Sertificate" -> "123")
    redisClient.hmset("data-service-config", conf)
  }

  def setConfig(entry: (String, String)): Unit = {
    redisClient.hset("data-service-config", entry._1, entry._2)
  }
}

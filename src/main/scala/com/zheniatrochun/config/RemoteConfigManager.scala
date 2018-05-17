package com.zheniatrochun.config

import java.net.URI

import com.redis.RedisClient
import com.typesafe.config.Config


// simple connector for actor
private[config] class RemoteConfigManager(val config: Config) {

  def getRemoteConfig(): Map[String, String] = {
    withRedis { redisClient =>
      redisClient.hgetall1[String, String]("gateway-service-config")
        .getOrElse(Map.empty[String, String])
    }
  }

  def createDummyConfig(): Unit = {
    withRedis { redisClient =>
      val conf = Map("auth" -> "my-auth-service.herokuapp.com",
        "users" -> "my-data-service.herokuapp.com",
        "bills" -> "my-data-service.herokuapp.com",
        "wallets" -> "my-data-service.herokuapp.com",
        "statistics" -> "statistics-service.herokuapp.com")

      redisClient.hmset("gateway-service-config", conf)
    }
  }

  def setConfig(entry: (String, String)): Unit = {
    withRedis { redisClient =>
      redisClient.hset("gateway-service-config", entry._1, entry._2)
    }
  }

  private def withRedis[T](func: (RedisClient) => T): T = {
    val redisClient = new RedisClient(new URI(config.getString("redis.url")))
    val res = func(redisClient)
    redisClient.disconnect

    res
  }
}

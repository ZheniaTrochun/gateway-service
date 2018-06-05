//package com.zheniatrochun.config
//
//import java.net.URI
//
//import com.redis.RedisClient
//import com.typesafe.config.Config
//
//private[config] class RemoteConfigManager(val config: Config) {
//  private val redisClient = new RedisClient(new URI(config.getString("redis.url")))
//
//  def getRemoteConfig(): Map[String, String] = {
//    redisClient.hgetall1[String, String]("gateway-service-config")
//      .getOrElse(Map.empty[String, String])
//  }
//
//  def createDummyConfig(): Unit = {
//    val conf = Map("auth" -> "my-auth-service.herokuapp.com",
//      "users" -> "my-data-service.herokuapp.com",
//      "bills" -> "my-data-service.herokuapp.com",
//      "wallets" -> "my-data-service.herokuapp.com")
//
//    redisClient.hmset("gateway-service-config", conf)
//  }
//
//  def setConfig(entry: (String, String)): Unit = {
//    redisClient.hset("gateway-service-config", entry._1, entry._2)
//  }
//}

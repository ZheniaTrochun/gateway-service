package com.zheniatrochun.config

import com.typesafe.config.{Config, ConfigFactory}

import scala.language.postfixOps

trait AppConfig {

  val config = new Configs()

  private val http = config.getConfig("http")
  val interface = http.getString("interface")
  val port = http.getInt("port")
}

class Configs {
  private val config = ConfigFactory.load()
  private val remoteConfigManager = new RemoteConfigManager(config)
  private var remoteConfigCache: Map[String, String] = remoteConfigManager.getRemoteConfig()

  def getString(key: String): String = {
    remoteConfigCache.getOrElse(key, config.getString(key))
  }

  def getInt(key: String): Int = {
    getString(key) toInt
  }

  def getConfig(key: String): Config = {
    config.getConfig(key)
  }

  def update(): Unit = {
    remoteConfigCache = remoteConfigManager.getRemoteConfig()
  }

  def createDummyConfig(): Unit = {
    remoteConfigManager.createDummyConfig()
    update()
  }

  def addRemoteConfig(entry: (String, String)): Unit = {
    remoteConfigManager.setConfig(entry)
    update()
  }
}
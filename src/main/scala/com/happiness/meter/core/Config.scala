package com.happiness.meter.core

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf

object Config {
  val sparkConf = new SparkConf()
    .setMaster("local[*]")
    .setAppName("Happiness Meter Spark App")
    .set("spark.logConf", "true")
    .set("spark.driver.port", "7777")
    .set("spark.driver.host", "localhost")
    .set("spark.akka.logLifecycleEvents", "true")

  object Twitter {
    val twitterConfig = ConfigFactory.load("twitter")

    val consumerKey = twitterConfig.getString("consumerKey")
    val consumerSecret = twitterConfig.getString("consumerSecret")
    val accessToken = twitterConfig.getString("accessToken")
    val accessTokenSecret = twitterConfig.getString("accessTokenSecret")
  }
}

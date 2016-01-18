package com.happiness.meter.streaming

import akka.actor.{ActorLogging, Actor}
import com.happiness.meter.core.Config
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

class StreamingActor extends Actor with ActorLogging {

  override def preStart(): Unit = {
    super.preStart()
    System.setProperty("twitter4j.oauth.consumerKey", Config.Twitter.consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", Config.Twitter.consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", Config.Twitter.accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", Config.Twitter.accessTokenSecret)
  }

  val streamingContext = new StreamingContext(Config.sparkConf, Seconds(10))

  def runJob(): Unit = {
    streamingContext.start()
  }

  def receive = {
    case StartJob =>
      runJob()
    case StopJob => streamingContext.stop(stopSparkContext = false)
    case any => log.info(s"unknown message: $any")
  }
}


trait StreamingCommand
object StartJob extends StreamingCommand
object StopJob extends StreamingCommand

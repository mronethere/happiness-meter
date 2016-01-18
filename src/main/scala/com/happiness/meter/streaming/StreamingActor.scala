package com.happiness.meter.streaming

import akka.actor.{ActorLogging, Actor}
import com.happiness.meter.core.Config
import org.apache.spark.SparkContext
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

class StreamingActor(sparkContext: SparkContext) extends Actor with ActorLogging {

  var streamingContext: StreamingContext = null

  override def preStart(): Unit = {
    System.setProperty("twitter4j.oauth.consumerKey", Config.Twitter.consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", Config.Twitter.consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", Config.Twitter.accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", Config.Twitter.accessTokenSecret)
    streamingContext = new StreamingContext(sparkContext, Seconds(8))
  }

  def runJob(): Unit = {
    streamingContext.start()
    val stream = TwitterUtils.createStream(streamingContext, None, List("scala", "java"))
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

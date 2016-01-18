package com.happiness.meter.core

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.happiness.meter.streaming.StreamingActor
import org.apache.spark.SparkContext
import spray.can.Http

import scala.concurrent.duration._

object Application extends App {
  implicit val system = ActorSystem("HappinessMeter")
  implicit val timeout = Timeout(5.seconds)

  val appActor = system.actorOf(Props[ApplicationActor], "app-service")
  IO(Http) ? Http.Bind(appActor, interface = "localhost", port = 9000)

  val sparkContext = new SparkContext(Config.sparkConf)
  val streamingActor = system.actorOf(Props(classOf[StreamingActor], sparkContext), "streaming-actor")

}

package com.happiness.meter.core

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import spray.can.Http

import scala.concurrent.duration._

object Application extends App {
  implicit val system = ActorSystem("HappinessMeter")
  implicit val timeout = Timeout(5.seconds)

  val appActor = system.actorOf(Props[ApplicationActor], "app-service")
  IO(Http) ? Http.Bind(appActor, interface = "localhost", port = 9000)

}

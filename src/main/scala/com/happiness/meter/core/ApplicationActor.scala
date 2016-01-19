package com.happiness.meter.core

import akka.actor.{ActorRef, ActorLogging, Actor}
import spray.routing.{HttpService, HttpServiceActor}
import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller
import spray.json.DefaultJsonProtocol

class ApplicationActor(streamer: ActorRef) extends HttpServiceActor {
  def receive = runRoute {
    pathSingleSlash {
      getFromResource("app/index.html")
    } ~ pathPrefix("app") {
      getFromResourceDirectory("app")
    } ~ pathPrefix("bower_components") {
      getFromResourceDirectory("bower_components")
    } ~ get {
      path("start") {
        complete {
          streamer ! StartJob
          "started"
        }
      } ~ path("end") {
        complete {
          streamer ! StopJob
          "finished"
        }
      }
    }
  }
}



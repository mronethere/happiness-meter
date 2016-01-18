package com.happiness.meter.core

import spray.routing.{HttpService, HttpServiceActor}
import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller
import spray.json.DefaultJsonProtocol

class ApplicationActor extends HttpServiceActor with ApplicationAPI {
  def receive = runRoute(routes)
}

trait ApplicationAPI extends HttpService with DefaultJsonProtocol {

  val statics =
    pathSingleSlash {
      getFromResource("app/index.html")
    } ~ pathPrefix("app") {
      getFromResourceDirectory("app")
    } ~ pathPrefix("bower_components") {
      getFromResourceDirectory("bower_components")
    }

  val routes = statics
}

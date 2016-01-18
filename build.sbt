name := """happiness-meter"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaV = "2.4.1"
  val sprayV = "1.3.3"
  val sprakV = "1.6.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "io.spray" %%  "spray-can" % sprayV,
    "io.spray" %%  "spray-routing" % sprayV,
    "io.spray" %%  "spray-client" % sprayV,
    "io.spray" %%  "spray-json" % "1.3.2",
    "org.apache.spark" %% "spark-core" % sprakV,
    "org.apache.spark" %% "spark-streaming" % sprakV,
    "org.apache.spark" %% "spark-streaming-twitter" % sprakV,
    "org.scalatest" %% "scalatest" % "2.2.6" % "test")
}

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
scalacOptions in Test ++= Seq("-Yrangepos")

resolvers += "spray repo" at "http://repo.spray.io"

fork in run := true
fork in test := true

Revolver.settings
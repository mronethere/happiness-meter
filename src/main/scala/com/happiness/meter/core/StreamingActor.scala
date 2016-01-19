package com.happiness.meter.core

import akka.actor.{Actor, ActorLogging}
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.{LogisticRegressionModel, LogisticRegressionWithSGD}
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

class StreamingActor(sparkContext: SparkContext) extends Actor with ActorLogging {

  var streamingContext: StreamingContext = null
  val scala = "scala"
  val java = "scala"

  override def preStart(): Unit = {
    System.setProperty("twitter4j.oauth.consumerKey", Config.Twitter.consumerKey)
    System.setProperty("twitter4j.oauth.consumerSecret", Config.Twitter.consumerSecret)
    System.setProperty("twitter4j.oauth.accessToken", Config.Twitter.accessToken)
    System.setProperty("twitter4j.oauth.accessTokenSecret", Config.Twitter.accessTokenSecret)
    System.setProperty("hadoop.home.dir", "c:\\winutil\\")
    streamingContext = new StreamingContext(sparkContext, Seconds(4))
  }

  def runJob(): Unit = {
    log.info("running the job...")
    log.info("loading data from txt files...")
    val happy = sparkContext.textFile("happy.txt")
    val unhappy = sparkContext.textFile("unhappy.txt")
    log.info("data from txt files is loaded...")

    log.info("creating execution plan for training data...")
    val tf = new HashingTF(numFeatures = 10000)
    val happyFeatures = happy.map(text => tf.transform(text.split(" ")))
    val unhappyFeatures = unhappy.map(text => tf.transform(text.split(" ")))
    val happyExamples = happyFeatures.map(features => LabeledPoint(1, features))
    val unhappyExamples = unhappyFeatures.map(features => LabeledPoint(0, features))
    val trainingData = happyExamples.union(unhappyExamples)
    log.info("execution plan is crated...")

    log.info("starting caching training data...")
    trainingData.cache()
    log.info("training data is cached...")

    log.info("creating logistic regression model...")
    val model = new LogisticRegressionWithSGD().run(trainingData)
    log.info("logistic regression model is created...")

    log.info("setting up the twitter stream...")
    val stream = TwitterUtils
      .createStream(streamingContext, None, List(scala, java))
      .map(status => (classifyText(status.getText), status.getText))
      .filter(classifierAndStatus => classifierAndStatus._1.nonEmpty)

    stream.foreachRDD { rdd =>
      rdd.foreach { case (classifier, text) =>
        val test = tf.transform(text.split(" "))
        println(s"<PREDICTION> for $classifier: ${model.predict(test)} \n *text is:'$text' \n</PREDICTION>")
      }
    }
    streamingContext.start()
  }



  def receive = {
    case StartJob =>
      runJob()
    case StopJob => streamingContext.stop(stopSparkContext = false)
    case any => log.info(s"unknown message: $any")
  }

  /* this could be re-written for general purpose */
  def classifyText(text: String): String = {
    val textLC = text.toLowerCase
    val containsScala = textLC.contains(scala)
    val containsJava = textLC.contains(java)
    if ((containsScala && containsJava) || !(containsScala || containsScala)) ""
    else if (containsScala) scala
    else java
  }
}


trait StreamingCommand
object StartJob extends StreamingCommand
object StopJob extends StreamingCommand

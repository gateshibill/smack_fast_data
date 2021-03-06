package com.smack.actor

import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import akka.actor.SupervisorStrategy._
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.io.IO
import akka.routing.ConsistentHashingPool
import com.smack.actor.collection._
import com.smack.actor.http.DataService
import com.smack.actor.router.route.{InputWorker, KafkaConsumeWorker}
import com.typesafe.config.Config
import spray.can.Http

class DataInputActor(config: Config) extends Actor with ActorLogging {

  val workerRouter = context.actorOf(
    ClusterRouterPool(ConsistentHashingPool(0), ClusterRouterPoolSettings(
      totalInstances = 40, maxInstancesPerNode = 1,
      allowLocalRoutees = true, useRole = None)).props(Props(new InputWorker(config))),
    name = "workerInRouter")

  val handler = context.actorOf(Props(new DataService(config, workerRouter)), name = "handler")

  implicit val system = context.system
  IO(Http) ! Http.Bind(handler, interface = config.getString("http.interface"), port = config.getInt("http.port"))

  val logtype = config.getString("log.type")
	log.info("========="+logtype)
  logtype match {
	  
    case "file" => {
      context.actorOf(Props(new FileActor(config, workerRouter)), name = "fileActor")
      log.info("start file log collection")
    }
    case "kafka" => {
      val kafkaWorkerRouter = context.actorOf(
        ClusterRouterPool(ConsistentHashingPool(0), ClusterRouterPoolSettings(
          totalInstances = 40, maxInstancesPerNode = 1,
          allowLocalRoutees = true, useRole = None)).props(Props(new KafkaConsumeWorker(config, workerRouter))),
        name = "kafkaWorkerRouter")
      val topics = config.getString("kafka_consumer.topic")
      topics.split(",").foreach { topic =>
        context.actorOf(Props(new KakfaActor(config, topic, kafkaWorkerRouter)), name = "kafkaActor_" + topic)
        log.info("start kafka log collection at topic=" + topic)
      }
    }
    case "zeromq" => {
      context.actorOf(Props(new ZeroMqActor(config, workerRouter)), name = "zeromqActor")
      log.info("start zeromq log collection")
    }
  }

  override val supervisorStrategy =
    OneForOneStrategy() {
      case e: NullPointerException =>
        log.error("[{}] create NullPointerException [{}]", self, e.getLocalizedMessage()); Restart
      case e: Exception =>
        log.error("[{}] create NullPointerException [{}]", self, e.getLocalizedMessage()); Restart
    }
  def receive = {
    case x => log.info("x=" + x)
  }
}
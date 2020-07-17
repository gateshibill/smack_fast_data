package com.smack.actor

import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props}
import akka.actor.SupervisorStrategy._
import akka.cluster.routing.{ClusterRouterPool, ClusterRouterPoolSettings}
import akka.io.IO
import akka.routing.ConsistentHashingPool
import com.smack.actor.http.DataService
import com.smack.actor.router.route.OutputWorker
import com.typesafe.config.Config
import spray.can.Http

class DataOutputActor(config: Config) extends Actor with ActorLogging {

  val workerRouter = context.actorOf(
    ClusterRouterPool(ConsistentHashingPool(0), ClusterRouterPoolSettings(
      totalInstances = 40, maxInstancesPerNode = 8,
      allowLocalRoutees = true, useRole = None)).props(Props(new OutputWorker(config))),
    name = "workerOutRouter")

  val handler = context.actorOf(Props(new DataService(config, workerRouter)), name = "handler")
  implicit val system = context.system
  IO(Http) ! Http.Bind(handler, interface = config.getString("http.interface"), port = config.getInt("http.port"))

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
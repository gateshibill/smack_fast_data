package com.smack.spark.rpc.actor

import akka.actor.{Actor, ActorLogging}
import com.smack.model.CmdMessageByPeriod
import com.smack.spark.rpc.service.RankDataService
import com.smack.util.JsonUtil

class RpcActor extends Actor with ActorLogging {
	val rankDataDealService = new RankDataService()
	def receive = {
		case cmdMsgeGroup: CmdMessageByPeriod => {
			cmdMsgeGroup.cmd match {
				case "avgtime" =>
					sender ! rankDataDealService.handlerAvgTime(cmdMsgeGroup)
				case "pv" =>
					sender ! rankDataDealService.handlerPv(cmdMsgeGroup)
				case "uv" =>
					sender ! rankDataDealService.handlerUv(cmdMsgeGroup)
				case _ =>
					sender ! JsonUtil.toJson(Map("message" -> "not found cmd"))
			}
		}
		case x => log.info("xxxx" + x)
	}
}
package com.smack.cassandra

import scala.concurrent.Future

trait StatusRealStaticsService extends ProductionDatabase {

	def getListByserverName(namespace: String, servername: String, num: Integer): Future[List[StatusRealStatics]] = {
		database.statusRealStaticsModel.getListByCondition(namespace, servername, num)
	}
}

object StatusRealStaticsService extends StatusRealStaticsService with ProductionDatabase

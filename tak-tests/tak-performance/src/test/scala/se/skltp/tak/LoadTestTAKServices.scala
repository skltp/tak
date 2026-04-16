package se.skltp.tak

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._
import se.skltp.tak.utils.Conf
import se.skltp.tak.utils.Headers

class LoadTestTAKServices extends Simulation {

    setUp(
		Scenarios.scn_PingOkHttp.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf),
		
		Scenarios.scn_GetLogicalAddressessByServiceContractHttp.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf),
		//Scenarios.scn_GetSupportedServiceContractsHttp_v1.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf),
		Scenarios.scn_GetSupportedServiceContractsHttp_v2.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf),
	
		Scenarios.scn_HamtaAllaAnropsBehorigheter.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf),		
		Scenarios.scn_HamtaAllaVirtualiseringar.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf),
		Scenarios.scn_HamtaAllaTjanstekontrakt.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf),
		Scenarios.scn_HamtaAllaTjanstekomponenter.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf)
	)
}
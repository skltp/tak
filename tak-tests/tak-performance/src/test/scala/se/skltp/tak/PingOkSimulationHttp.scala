package se.skltp.tak

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._
import se.skltp.tak.utils.Conf
import se.skltp.tak.utils.Headers

class PingOkSimulationHttp extends Simulation {

  //NOTE!
  //
  //HTTP needs correct http headers x-vp-sender-id and x-vp-instance-id, and your
  //ip adress must be in VP config (vp-config-override.properties) whitelist.
  
  setUp(
	  Scenarios.scn_PingOkHttp.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf)
  )
}
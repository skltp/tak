/**
 * Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
 * 							<http://cehis.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.tak

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import scala.concurrent.duration._
import se.skltp.tak.utils.Conf
import se.skltp.tak.utils.Headers

class LoadTestTAK extends Simulation {

    setUp(	
		Scenarios.scn_PingOkHttp.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf),
		
		Scenarios.scn_GetLogicalAddressessByServiceContractHttp.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf),
		Scenarios.scn_GetSupportedServiceContractsHttp.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf),
	
		Scenarios.scn_HamtaAllaAnropsBehorigheter.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf),		
		Scenarios.scn_HamtaAllaVirtualiseringar.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf)
			
		/*
		Scenarios.scn_HamtaAllaTjanstekontrakt.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf),
		Scenarios.scn_HamtaAllaTjanstekomponenter.inject(rampUsers(Conf.noOfUsers) over (Scenarios.rampUpTimeSecs seconds)).protocols(Conf.httpConf)*/
		
	)
}
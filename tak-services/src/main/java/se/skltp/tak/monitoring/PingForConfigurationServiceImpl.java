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
package se.skltp.tak.monitoring;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.itintegration.monitoring.rivtabp21.v1.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.v1.ConfigurationType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.v1.PingForConfigurationType;
import se.skltp.tak.core.facade.TakSyncService;
import se.skltp.tak.core.facade.VirtualiseringInfo;

@WebService(
		serviceName = "PingForConfigurationResponderService",  
		portName = "PingForConfigurationResponderPort", 
		targetNamespace = "urn:riv:itintegration:monitoring:PingForConfiguration:1:rivtabp21")
public class PingForConfigurationServiceImpl implements PingForConfigurationResponderInterface {

	private static final Logger log = LoggerFactory.getLogger(PingForConfigurationServiceImpl.class);
	
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
	
	final String applicationName = "tk-admin-services";

	private TakSyncService takSyncService;
	
	public void setTakSyncService(TakSyncService takSyncService) {
		this.takSyncService = takSyncService;
	}
	
	@Override
	public PingForConfigurationResponseType pingForConfiguration(String logicalAddress,
			PingForConfigurationType parameters) {
		
		log.info("PingForConfiguration requested for {}", applicationName);
		
		try {
			log.debug("Check if TK admin services can access database?");
			takSyncService.getVagvalByTjanstekontrakt(parameters.getServiceContractNamespace());
			log.debug("Yes, no problem in using database");
		} catch (Exception e) {
			log.error("Severe error in TK admin service, error trying to use database", e);
			throw new RuntimeException("Severe error in TK admin service database access, message: " + e.getMessage());
		}
		
		PingForConfigurationResponseType response = new PingForConfigurationResponseType();
		response.setPingDateTime(dateFormat.format(new Date()));
		response.getConfiguration().add(createConfigurationInfo("Applikation", applicationName));
		
		log.info("PingForConfiguration response returned for {}", applicationName);
		
		return response;
	}

	private ConfigurationType createConfigurationInfo(String name, String value) {
		log.debug("PingForConfiguration config added [{}: {}]", name, value);
		
		ConfigurationType configurationInfo = new ConfigurationType();
		configurationInfo.setName(name);
		configurationInfo.setValue(value);
		return configurationInfo;
	}
	
	

}

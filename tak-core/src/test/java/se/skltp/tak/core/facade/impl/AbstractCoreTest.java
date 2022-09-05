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
package se.skltp.tak.core.facade.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.sql.DataSource;

import  com.fasterxml.jackson.databind.ObjectMapper;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;

@ContextConfiguration("classpath*:tak-core-EMBED.xml")
public abstract class AbstractCoreTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	DataSource dataSource;
	
	private String initialData = "<?xml version='1.0' encoding='UTF-8'?> "
			+ "<dataset>"

			+ "<rivTaProfil id='1' namn='RIVTABP20' version='0' beskrivning='RIV TA BP 2.0' pubVersion='1'/>"
			+ "<rivTaProfil id='2' namn='RIVTABP21' version='0' beskrivning='RIV TA BP 2.1' pubVersion='1'/>"
			+ "<rivTaProfil id='3' namn='RIVTABPXX' version='0' beskrivning='RIV TA BP X.X' deleted='FALSE' updatedBy='admin' updatedTime='2015-12-11 12:13:14'/>"

			+ "<tjanstekontrakt id='10' namnrymd='urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1' version='0' beskrivning='Tidbokning - GetSubjectOfCareSchedule' majorVersion='1' minorVersion='0' pubVersion='1'/>"
			+ "<tjanstekontrakt id='11' namnrymd='urn:riv:itinfra:tp:PingResponder:1' version='0' beskrivning='Ping' majorVersion='1' minorVersion='0' pubVersion='1'/>"
			+ "<tjanstekontrakt id='12' namnrymd='urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContractResponder:1' version='0' beskrivning='Stödtjänst VP' majorVersion='1' minorVersion='0' pubVersion='1'/>"
			+ "<tjanstekontrakt id='13' namnrymd='urn:riv:itintegration:registry:GetSupportedServiceContractsResponder:1' version='0' beskrivning='Stödtjänst VP' majorVersion='1' minorVersion='0' pubVersion='1'/>"
			+ "<tjanstekontrakt id='14' namnrymd='urn:riv:itintegration:engagementindex:FindContentResponder:1' version='0' beskrivning='Engagemangsindex - Findcontent' majorVersion='1' minorVersion='0' pubVersion='1'/>"
			+ "<tjanstekontrakt id='15' namnrymd='urn:riv:itintegration:engagementindex:ProcessNotificationResponder:1' version='0' beskrivning='Engagemangsindex - ProcessNotification' majorVersion='1' minorVersion='0' pubVersion='1'/>"
			+ "<tjanstekontrakt id='16' namnrymd='urn:riv:itintegration:engagementindex:UpdateResponder:1' version='0' beskrivning='Engagemangsindex - Update' majorVersion='1' minorVersion='0' pubVersion='1'/>"
			+ "<tjanstekontrakt id='17' namnrymd='urn:riv:itintegration:registry:GetValidAffressResponder:1' version='0' beskrivning='Stödtjänst VP ny - GetValidAddress' majorVersion='1' minorVersion='0' deleted='FALSE' updatedBy='admin' updatedTime='2015-12-11 12:13:14'/>"
						
			+ "<logiskadress id='1' hsaId='HSA-VKK123' version='0' beskrivning='Demo adressat tidbok, vardcentralen kusten, Karna' pubVersion='1'/>"		
			+ "<logiskadress id='2' hsaId='HSA-VKM345' version='0' beskrivning='Demo adressat tidbok, vardcentralen kusten, Marstrand' pubVersion='1'/>"
			+ "<logiskadress id='3' hsaId='HSA-VKY567' version='0' beskrivning='Demo adressat tidbok, vardcentralen kusten, Ytterby' pubVersion='1'/>"		
			+ "<logiskadress id='4' hsaId='PING' version='0' beskrivning='VPs egna ping-tjanst' pubVersion='1'/>"		
			+ "<logiskadress id='5' hsaId='5565594230' version='0' beskrivning='Organisation: Inera' pubVersion='1'/>"		
			+ "<logiskadress id='6' hsaId='HSA-NYA-Test-123' version='0' beskrivning='Organisation: XXXX' deleted='FALSE' updatedBy='admin' updatedTime='2015-12-11 12:13:14'/>"		
			
			+ "<tjanstekomponent id='1' hsaId='Schedulr' beskrivning='Demo tidbok' version='0' pubVersion='1'/>"
			+ "<tjanstekomponent id='2' hsaId='tp' beskrivning='tp test client' version='0' pubVersion='1'/>"
			+ "<tjanstekomponent id='3' hsaId='PING-HSAID' beskrivning='VP intern ping tjänst' version='0' pubVersion='1'/>"
			+ "<tjanstekomponent id='4' hsaId='EI-HSAID' beskrivning='Engagemangsidex' version='0' pubVersion='1'/>"
			+ "<tjanstekomponent id='5' hsaId='VP-Cachad-GetLogicalAddresseesByServiceContract' beskrivning='VP-Cachad-GetLogicalAddresseesByServiceContract' version='0' pubVersion='1'/>"
			+ "<tjanstekomponent id='6' hsaId='5565594230' beskrivning='Inera som konsument, tex EI' version='0' pubVersion='1'/>"
			+ "<tjanstekomponent id='7' hsaId='AGT-Tidbok' beskrivning='Producent: GetAggregatedSubjectOfCareSchedule' version='0' pubVersion='1'/>"
			+ "<tjanstekomponent id='8' hsaId='HSA-NYA-Test-123' beskrivning='Nya producent: GetAggregatedSubjectOfCareSchedule' version='0' deleted='FALSE' updatedBy='admin' updatedTime='2015-12-11 12:13:14'/>"
			
			+ "<anropsAdress id='1' adress='http://33.33.33.33:8080/Schedulr-0.1/ws/GetSubjectOfCareSchedule/1' version='0' rivTaProfil_id='2' tjanstekomponent_id='1' pubVersion='1'/>"
			+ "<anropsAdress id='2' adress='http://localhost:10000/test/Ping_Service' version='0' rivTaProfil_id='1' tjanstekomponent_id='2' pubVersion='1'/>"
			+ "<anropsAdress id='3' adress='http://localhost:8081/skltp-ei/update-service/v1' version='0' rivTaProfil_id='2' tjanstekomponent_id='4' pubVersion='1'/>"
			+ "<anropsAdress id='4' adress='http://localhost:8082/skltp-ei/find-content-service/v1' version='0' rivTaProfil_id='2' tjanstekomponent_id='4' pubVersion='1'/>"
			+ "<anropsAdress id='5' adress='http://localhost:8081/skltp-ei/notification-service/v1' version='0' rivTaProfil_id='2' tjanstekomponent_id='4' pubVersion='1'/>"
			+ "<anropsAdress id='6' adress='https://localhost:23001/vp/GetLogicalAddresseesByServiceContract/1/rivtabp21' version='0' rivTaProfil_id='2' tjanstekomponent_id='5' pubVersion='1'/>"
			+ "<anropsAdress id='7' adress='http://localhost:8083/GetAggregatedSubjectOfCareSchedule/service/v1' version='0' rivTaProfil_id='2' tjanstekomponent_id='7' pubVersion='1'/>"
			+ "<anropsAdress id='8' adress='http://localhost:8083/NyaServiceURL/service/v1' version='0' rivTaProfil_id='3' tjanstekomponent_id='8' deleted='FALSE' updatedBy='admin' updatedTime='2015-12-11 12:13:14'/>"
			
			+ "<vagval id='1' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' logiskadress_id='1' tjanstekontrakt_id='10' anropsAdress_id='1' version='0' pubVersion='1'/>"
			+ "<vagval id='2' fromTidpunkt='2010-12-24' tomTidpunkt='2113-12-24' logiskadress_id='2' tjanstekontrakt_id='10' anropsAdress_id='1' version='0' pubVersion='1'/>"
			+ "<vagval id='3' fromTidpunkt='2010-12-24' tomTidpunkt='2113-12-24' logiskadress_id='3' tjanstekontrakt_id='10' anropsAdress_id='1' version='0' pubVersion='1'/>"
			+ "<vagval id='4' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' logiskadress_id='4' tjanstekontrakt_id='11' anropsAdress_id='2' version='0' pubVersion='1'/>"
			+ "<vagval id='5' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' logiskadress_id='5' tjanstekontrakt_id='12' anropsAdress_id='6' version='0' pubVersion='1'/>"
			+ "<vagval id='6' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' logiskadress_id='5' tjanstekontrakt_id='13' anropsAdress_id='4' version='0' pubVersion='1'/>"
			+ "<vagval id='7' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' logiskadress_id='5' tjanstekontrakt_id='14' anropsAdress_id='5' version='0' pubVersion='1'/>"
			+ "<vagval id='8' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' logiskadress_id='5' tjanstekontrakt_id='15' anropsAdress_id='3' version='0' pubVersion='1'/>"
			+ "<vagval id='9' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' logiskadress_id='5' tjanstekontrakt_id='16' anropsAdress_id='7' version='0' pubVersion='1'/>"
			+ "<vagval id='10' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' logiskadress_id='6' tjanstekontrakt_id='17' anropsAdress_id='8' version='0' deleted='FALSE' updatedBy='admin' updatedTime='2015-12-11 12:13:14'/>"
			
			+ "<anropsBehorighet id='1' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' integrationsavtal='I1' tjanstekonsument_id='2' logiskadress_id='1' tjanstekontrakt_id='10' version='0' pubVersion='1'/>"
			+ "<anropsBehorighet id='2' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' integrationsavtal='I1' tjanstekonsument_id='2' logiskadress_id='2' tjanstekontrakt_id='10' version='0' pubVersion='1'/>"
			+ "<anropsBehorighet id='3' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' integrationsavtal='I1' tjanstekonsument_id='2' logiskadress_id='3' tjanstekontrakt_id='10' version='0' pubVersion='1'/>"
			+ "<anropsBehorighet id='4' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' integrationsavtal='I2' tjanstekonsument_id='2' logiskadress_id='4' tjanstekontrakt_id='11' version='0' pubVersion='1'/>"
			+ "<anropsBehorighet id='5' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' integrationsavtal='EI' tjanstekonsument_id='2' logiskadress_id='5' tjanstekontrakt_id='12' version='0' pubVersion='1'/>"
			+ "<anropsBehorighet id='6' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' integrationsavtal='I3' tjanstekonsument_id='2' logiskadress_id='5' tjanstekontrakt_id='11' version='0' pubVersion='1'/>"
			+ "<anropsBehorighet id='7' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' integrationsavtal='EI' tjanstekonsument_id='2' logiskadress_id='5' tjanstekontrakt_id='13' version='0' pubVersion='1'/>"
			+ "<anropsBehorighet id='8' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' integrationsavtal='I4' tjanstekonsument_id='2' logiskadress_id='5' tjanstekontrakt_id='14' version='0' pubVersion='1'/>"
			+ "<anropsBehorighet id='9' fromTidpunkt='2009-03-10' tomTidpunkt='2113-12-24' integrationsavtal='Nya_I' tjanstekonsument_id='8' logiskadress_id='6' tjanstekontrakt_id='17' version='0' deleted='FALSE' updatedBy='admin' updatedTime='2015-12-11 12:13:14'/>"

			+ "<filter id='1' servicedomain='urn:riv:itintegration:registry:GetItems' anropsbehorighet_id='5' version='0' pubVersion='1'/>"
			+ "<filter id='2' servicedomain='urn:riv:itintegration:registry:GetItems' anropsbehorighet_id='7' version='0' pubVersion='1'/>"
			+ "<filter id='3' servicedomain='urn:riv:itintegration:registry:GetItems' anropsbehorighet_id='8' version='0' pubVersion='1'/>"
			+ "<filter id='4' servicedomain='urn:riv:itintegration:registry:GetMoreItems' anropsbehorighet_id='8' version='0' pubVersion='1'/>"
			+ "<filter id='5' servicedomain='urn:riv:itintegration:registry:GetItems' anropsbehorighet_id='9' version='0' deleted='FALSE' updatedBy='admin' updatedTime='2015-12-11 12:13:14'/>"

			+ "<filtercategorization id='1' category='Category c1' filter_id='2' version='0' pubVersion='1'/>"
			+ "<filtercategorization id='2' category='Category c1' filter_id='2' version='0' pubVersion='1'/>"
			+ "<filtercategorization id='3' category='Category c2' filter_id='3' version='0' pubVersion='1'/>"
			+ "<filtercategorization id='4' category='Category c2' filter_id='4' version='0' deleted='FALSE' updatedBy='admin' updatedTime='2015-12-11 12:13:14'/>"
			
			+ "<pubVersion id='1' formatVersion='1' time='2015-12-10 12:01:09' utforare='admin' kommentar='default version' version='0' storlek='2' data='./src/test/resources/export.gzip'/>"
			+ "<pubVersion id='2' formatVersion='1' time='2015-12-11 12:01:09' utforare='admin' kommentar='ändrat beskrivning' version='0' storlek='2' data='./src/test/resources/export.gzip'/>"
			+ "<pubVersion id='3' formatVersion='2' time='2015-12-12 12:01:09' utforare='admin' kommentar='uppdaterad format' version='0' storlek='2' data='./src/test/resources/export.gzip'/>"

			+ "</dataset>";

	@BeforeTransaction
	public void onSetUpInTransaction() throws Exception {
		InputStream fs = new ByteArrayInputStream(initialData.getBytes("UTF-8"));
		IDataSet dataSet = new FlatXmlDataSetBuilder().build(fs);
	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
	    DatabaseOperation.DELETE_ALL.CLEAN_INSERT.execute(connection, dataSet);
	}
	
	 public boolean compareJson(String input1, String input2) {
        ObjectMapper om = new ObjectMapper();
        Map<String, Object> m1 = null;
        Map<String, Object> m2 = null;
        try {
            m1 = (Map<String, Object>)(om.readValue(input1, Map.class));
            m2 = (Map<String, Object>)(om.readValue(input2, Map.class));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m1.size()==m2.size();
    }
}

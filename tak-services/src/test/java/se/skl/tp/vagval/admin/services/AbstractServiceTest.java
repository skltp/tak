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
package se.skl.tp.vagval.admin.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.jpa.AbstractJpaTests;

public abstract class AbstractServiceTest extends AbstractJpaTests {

	@Autowired
	DataSource dataSource;
 
	private String initialData = "<?xml version='1.0' encoding='UTF-8'?> "
		+ "<dataset>"
		+ "<tjanstekontrakt id='1' namnrymd='XXX' version='1'/>"
		+ "<tjanstekontrakt id='2' namnrymd='YYY' version='1'/>"
		+ "<tjanstekontrakt id='3' namnrymd='ZZZ' version='1'/>"
		+ "<tjanstekontrakt id='4' namnrymd='AAA' version='1'/>"
		+ "<tjanstekontrakt id='5' namnrymd='BBB' version='1'/>"
		+ "<logiskadressat id='3' hsaId='1' version='1'/>"			
		+ "<rivVersion id='4' namn='Riv1' version='1'/>"
		+ "<rivVersion id='5' namn='Riv2' version='1'/>"
		+ "<tjanstekomponent  id='7' hsaId='hsa7' adress='http://xxx.yyy' version='1'/>"
		+ "<tjanstekomponent  id='8' hsaId='hsa8' adress='http://xxx.yyy' version='1'/>"
		+ "<logiskadressat id='22' hsaId='test-hsa' version='1'/>"
		+ "<logiskadress id='11' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' logiskadressat_id='22' tjanstekontrakt_id='1' rivVersion_id='4' tjansteproducent_id='7' version='1'/>"
		+ "<logiskadress id='12' fromTidpunkt='2010-12-24' tomTidpunkt='2011-12-24' logiskadressat_id='22' tjanstekontrakt_id='1' rivVersion_id='5' tjansteproducent_id='7' version='1'/>"
		+ "<logiskadress id='13' fromTidpunkt='2010-12-24' tomTidpunkt='2011-12-24' logiskadressat_id='22' tjanstekontrakt_id='2' rivVersion_id='5' tjansteproducent_id='7' version='1'/>"
		+ "<tjanstekomponent id='2' hsaId='hsa2' adress='' version='1'/>"
		+ "<tjanstekomponent id='3' hsaId='hsa3' adress='' version='1'/>"
		+ "<tjanstekomponent id='4' hsaId='hsa4' adress='' version='1'/>"
		+ "<anropsBehorighet id='21' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' tjanstekonsument_id='2' logiskadressat_id='22' tjanstekontrakt_id='1' version='1' />"
		+ "<anropsBehorighet id='22' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' tjanstekonsument_id='3' logiskadressat_id='22' tjanstekontrakt_id='1' version='1' />"
		+ "<anropsBehorighet id='23' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' tjanstekonsument_id='3' logiskadressat_id='22' tjanstekontrakt_id='2' version='1' />"
		+ "<anropsBehorighet id='24' fromTidpunkt='2009-03-10' tomTidpunkt='2020-12-24' tjanstekonsument_id='3' logiskadressat_id='22' tjanstekontrakt_id='3' version='1' />"
		+ "<anropsBehorighet id='25' fromTidpunkt='2009-03-10' tomTidpunkt='2020-12-24' tjanstekonsument_id='4' logiskadressat_id='22' tjanstekontrakt_id='4' version='1' />"
		+ "<anropsBehorighet id='26' fromTidpunkt='2009-03-10' tomTidpunkt='2020-12-24' tjanstekonsument_id='4' logiskadressat_id='22' tjanstekontrakt_id='5' version='1' />"
		+ "<filter id='1' servicedomain='a_servicedomain' anropsbehorighet_id='24' version='1'/>"
		+ "<filtercategorization id='1' category='Booking' filter_id='1' version='1'/>"
		+ "<filtercategorization id='2' category='Invitation' filter_id='1' version='1'/>"
		+ "<filter id='2' servicedomain='Scheduling' anropsbehorighet_id='25' version='1'/>"
		+ "<filtercategorization id='3' category='Booking' filter_id='2' version='1'/>"
		+ "<filtercategorization id='4' category='Invitation' filter_id='2' version='1'/>"
		+ "<filter id='3' servicedomain='Servicedomain_FOO' anropsbehorighet_id='25' version='1'/>"
		+ "<filtercategorization id='5' category='Foo' filter_id='3' version='1'/>"
		+ "<filtercategorization id='6' category='Bar' filter_id='3' version='1'/>"
		+ "<filter id='4' servicedomain='Scheduling' anropsbehorighet_id='26' version='1'/>"
		+ "</dataset>";
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] { "classpath*:tp-vagval-admin-core-EMBED.xml", "classpath*:tp-vagval-admin-services-test.xml" };
	}

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();

		AbstractServiceTest.cleanInsert(dataSource, initialData);
	}
	
	private static void cleanInsert(DataSource dataSource, String dbUnitXML) throws Exception {
		InputStream fs = new ByteArrayInputStream(dbUnitXML.getBytes());
	    IDataSet dataSet = new FlatXmlDataSet(fs);
	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
	    DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
	}
}

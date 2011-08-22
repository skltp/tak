/**
 * Copyright 2009 Sjukvardsradgivningen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public

 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,

 *   Boston, MA 02111-1307  USA
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

import se.skl.tp.vagvalsinfo.wsdl.v1.HamtaAllaAnropsBehorigheterResponseType;
import se.skl.tp.vagvalsinfo.wsdl.v1.HamtaAllaVirtualiseringarResponseType;

public class SokVagvalsInfoImplTest extends AbstractJpaTests {

	@Autowired
	SokVagvalsInfoImpl sokVagvalsInfoImpl;

	@Autowired
	DataSource dataSource;
 
	private String initialData = "<?xml version='1.0' encoding='UTF-8'?> "
		+ "<dataset>"
		+ "<tjanstekontrakt id='1' namnrymd='XXX' version='1'/>"
		+ "<tjanstekontrakt id='2' namnrymd='YYY' version='1'/>"
		+ "<logiskadressat id='3' hsaId='1' version='1'/>"			
		+ "<rivVersion id='4' namn='Riv1' version='1'/>"
		+ "<rivVersion id='5' namn='Riv2' version='1'/>"
		+ "<tjanstekomponent  id='7' hsaId='hsa7' adress='http://xxx.yyy' version='1'/>"
		+ "<tjanstekomponent  id='8' hsaId='hsa8' adress='http://xxx.yyy' version='1'/>"
		+ "<logiskadressat id='22' version='1'/>"
		+ "<logiskadress id='11' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' logiskadressat_id='22' tjanstekontrakt_id='1' rivVersion_id='4' tjansteproducent_id='7' version='1'/>"
		+ "<logiskadress id='12' fromTidpunkt='2010-12-24' tomTidpunkt='2011-12-24' logiskadressat_id='22' tjanstekontrakt_id='1' rivVersion_id='5' tjansteproducent_id='7' version='1'/>"
		+ "<logiskadress id='13' fromTidpunkt='2010-12-24' tomTidpunkt='2011-12-24' logiskadressat_id='22' tjanstekontrakt_id='2' rivVersion_id='5' tjansteproducent_id='7' version='1'/>"
		+ "<tjanstekomponent id='2' hsaId='hsa2' adress='' version='1'/>"
		+ "<tjanstekomponent id='3' hsaId='hsa3' adress='' version='1'/>"
		+ "<anropsBehorighet id='21' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' tjanstekonsument_id='2' logiskadressat_id='22' tjanstekontrakt_id='1' version='1' />"
		+ "<anropsBehorighet id='22' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' tjanstekonsument_id='3' logiskadressat_id='22' tjanstekontrakt_id='1' version='1' />"
		+ "<anropsBehorighet id='23' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' tjanstekonsument_id='3' logiskadressat_id='22' tjanstekontrakt_id='2' version='1' />"
		+ "</dataset>";
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] { "classpath*:tp-vagval-admin-core-EMBED.xml", "classpath*:tp-vagval-admin-services-test.xml" };
	}

	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();

		SokVagvalsInfoImplTest.cleanInsert(dataSource, initialData);

	}
	
	private static void cleanInsert(DataSource dataSource, String dbUnitXML) throws Exception {
		InputStream fs = new ByteArrayInputStream(dbUnitXML.getBytes());
	    IDataSet dataSet = new FlatXmlDataSet(fs);
	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
	    DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);

	}

	public void testHamtaAllaVirtualiseringar() {

		HamtaAllaVirtualiseringarResponseType result = sokVagvalsInfoImpl
				.hamtaAllaVirtualiseringar(null);
		assertEquals(3, result.getVirtualiseringsInfo().size());

		result = sokVagvalsInfoImpl.hamtaAllaVirtualiseringar("XXX");
		assertEquals(2, result.getVirtualiseringsInfo().size());

	}

	public void testhamtaAllaAnropsBehorigheter() {

		HamtaAllaAnropsBehorigheterResponseType result = sokVagvalsInfoImpl
				.hamtaAllaAnropsBehorigheter(null);
		assertEquals(3, result.getAnropsBehorighetsInfo().size());

		result = sokVagvalsInfoImpl.hamtaAllaAnropsBehorigheter("XXX");
		assertEquals(2, result.getAnropsBehorighetsInfo().size());

	}

}

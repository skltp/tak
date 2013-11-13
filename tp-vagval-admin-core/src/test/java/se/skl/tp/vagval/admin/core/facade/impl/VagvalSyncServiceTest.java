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
package se.skl.tp.vagval.admin.core.facade.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.jpa.AbstractJpaTests;

import se.skl.tp.vagval.admin.core.facade.AnropsbehorighetInfo;
import se.skl.tp.vagval.admin.core.facade.VagvalSyncService;
import se.skl.tp.vagval.admin.core.facade.VirtualiseringInfo;

public class VagvalSyncServiceTest extends AbstractJpaTests {

	@Autowired
	VagvalSyncService vagvalSyncService;

	@Autowired
	DataSource dataSource;
	
	@Override
	protected String[] getConfigLocations() {
		return new String[] { "classpath:tp-vagval-admin-core-EMBED.xml" };
	}

	private String initialData = "<?xml version='1.0' encoding='UTF-8'?> "
			+ "<dataset>"
			+ "<tjanstekontrakt id='1' namnrymd='XXX' version='1'/>"
			+ "<tjanstekontrakt id='2' namnrymd='YYY' version='1'/>"
			+ "<tjanstekontrakt id='3' namnrymd='ZZZ' version='1'/>"
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
			+ "<anropsBehorighet id='24' fromTidpunkt='2009-03-10' tomTidpunkt='2010-12-24' tjanstekonsument_id='3' logiskadressat_id='22' tjanstekontrakt_id='3' version='1' />"
			+ "<filter id='1' servicedomain='a_servicedomain' anropsbehorighet_id='24' version='1'/>"
			+ "</dataset>";

	@Override
	protected void onSetUpInTransaction() throws Exception {
		VagvalSyncServiceTest.cleanInsert(dataSource, initialData);
	}
	
	private static void cleanInsert(DataSource dataSource, String dbUnitXML) throws Exception {
		InputStream fs = new ByteArrayInputStream(dbUnitXML.getBytes());
	    IDataSet dataSet = new FlatXmlDataSet(fs);
	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
	    DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);

	}

	public void testGetAllVirtualisering() throws Exception {

		List<VirtualiseringInfo> result = vagvalSyncService.getAllVirtualisering();
		assertEquals(3, result.size());

	}
	public void testGetVirtualiseringByTjanstekontrakt() throws Exception {

		List<VirtualiseringInfo> result = vagvalSyncService.getVirtualiseringByTjanstekontrakt("XXX");
		assertEquals(2, result.size());

		result = vagvalSyncService.getVirtualiseringByTjanstekontrakt("YYY");
		assertEquals(1, result.size());

	}
	public void testGetAllAnropsbehorighet() throws Exception {

		List<AnropsbehorighetInfo> result = vagvalSyncService.getAllAnropsbehorighet();
		assertEquals(4, result.size());

	}
	
	public void testGetAnropsbehorighetByTjanstekontrakt() throws Exception {

		List<AnropsbehorighetInfo> result = vagvalSyncService.getAnropsbehorighetByTjanstekontrakt("XXX");
		assertEquals(2, result.size());

		result = vagvalSyncService.getAnropsbehorighetByTjanstekontrakt("YYY");
		assertEquals(1, result.size());

	}

	public void testLogicalAddressesAndFiltersByTjanstekontrakt() throws Exception {
		List<AnropsbehorighetInfo> result = vagvalSyncService.getLogicalAddresseesAndFiltersByServiceContract("ZZZ", "hsa3");
		assertNotNull(result.get(0).getFilters());
		assertEquals(1, result.get(0).getFilters().size());
	}
}

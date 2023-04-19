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
package se.skltp.tak.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.sql.DataSource;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;

@ContextConfiguration({ "classpath*:tak-core-EMBED.xml", "classpath*:tak-services-test.xml" })
public abstract class AbstractServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	DataSource dataSource;
	
	private String initialData = "<?xml version='1.0' encoding='UTF-8'?> "
			+ "<dataset>"
			+ "<pubVersion id='1' formatVersion='1' time='2009-03-10 12:01:09' utforare='Kalle' kommentar='Kommentar' version='1' storlek='2' data='./src/test/resources/export.gzip'/>"			
			+ "</dataset>";

	@BeforeTransaction
	public void onSetUpInTransaction() throws Exception {
		AbstractServiceTest.cleanInsert(dataSource, initialData);
	}
	
	private static void cleanInsert(DataSource dataSource, String dbUnitXML) throws Exception {
		InputStream fs = new ByteArrayInputStream(dbUnitXML.getBytes());
	    IDataSet dataSet = new FlatXmlDataSetBuilder().build(fs);
	    IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());
	    DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
	}
}

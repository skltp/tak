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
package se.skltp.tak.core.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ContextConfiguration;

import se.skltp.tak.core.dao.PublishDao;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.core.memdb.PublishedVersionCache;

/**
 * @author muqkha
 *
 */
@ContextConfiguration(locations="classpath:/tak-core-config.xml,classpath:/tak-core-DEV.xml")
public class InitializeDB {
	
	public static void main(String[] args) throws IOException, SerialException, SQLException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath:/tak-core-config.xml",
	              "classpath:/tak-core-DEV.xml" });
		
		PublishDao publishDao = (PublishDao) context.getBean("publishDao", PublishDao.class);
		JpaTransactionManager transactionManager = (JpaTransactionManager) context.getBean("transactionManager", JpaTransactionManager.class);
		EntityManager em  = transactionManager.getEntityManagerFactory().createEntityManager();
		
		InitializeDB initDB = new InitializeDB();
		
		PubVersion pubVersion = initDB.createCoreVersion();
		byte[] jsonCompressed = initDB.generateCoreVersion(pubVersion, publishDao);
		System.out.println("Core version generated is done! byte size:" + jsonCompressed.length);
		initDB.writeFileToDisk(jsonCompressed);
		System.out.println("Core version saved on disk");
		
		initDB.savePublishVersion0(em, pubVersion, jsonCompressed);
		System.out.println("Finally saved in DB. End of process!");
	}
	
	private PubVersion createCoreVersion() {
		PubVersion pubVersion = new PubVersion();
		pubVersion.setFormatVersion(1L);
		pubVersion.setKommentar("Basic version/Core Data");
 		pubVersion.setTime(new java.sql.Date(System.currentTimeMillis()));
		pubVersion.setUtforare("admin");
		pubVersion.setVersion(0L);
		
		return pubVersion;
	}
	
	private byte[] generateCoreVersion(PubVersion pubVersion, PublishDao publishDao) throws IOException {
		// Read DB and create a PV
		PublishedVersionCache newPVFromDataRows = Util.getPublishedVersionCache( pubVersion,
				publishDao.getRivTaProfil(), 
				publishDao.getTjanstekontrakt(),
				publishDao.getTjanstekomponent(),
				publishDao.getLogiskAdress(),
				publishDao.getAnropsadress(),
				publishDao.getVagval(),
				publishDao.getAnropsbehorighet(),
				publishDao.getFilter(),
				publishDao.getFiltercategorization() );

		
		// Save a new PV as gzipped JSON to DB
		String newPVFromDataRowsJSON = Util.fromPublishedVersionToJSON(newPVFromDataRows);
		return Util.compress(newPVFromDataRowsJSON);
	}
	
	private void writeFileToDisk(byte[] jsonCompressed) throws IOException {
		Path path = Paths.get("./src/test/resources/export_new.gzip");	    
		java.nio.file.Files.write(path, jsonCompressed);
	}
	
	private void savePublishVersion0(EntityManager em, PubVersion pubVersion, byte[] jsonCompressed) throws SerialException, SQLException {
		Blob blob = new SerialBlob(jsonCompressed);
		pubVersion.setData(blob);
		
		em.getTransaction().begin();
		em.persist(pubVersion);
		em.getTransaction().commit();
	}
}

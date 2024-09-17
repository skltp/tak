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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Scanner;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.skltp.tak.core.dao.PublishDao;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.core.memdb.PublishedVersionCache;

/**
 * @author muqkha
 *
 */
public class InitializeDB {

	@PersistenceContext
	private EntityManager entityManager;



	public static void main(String[] args) throws IOException, SerialException, SQLException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		
		Scanner in = new Scanner(System.in);
		try {
			InitializeDB initDB = new InitializeDB();
			ApplicationContext context = new ClassPathXmlApplicationContext(initDB.validateConfigurationFiles(args));
			
			PublishDao publishDao = context.getBean("publishDao", PublishDao.class);

			PubVersion pubVersion = initDB.createCoreVersion();
			byte[] jsonCompressed = initDB.generateCoreVersion(pubVersion, publishDao);
			System.out.println("Core version generated! byte size:" + jsonCompressed.length);
			initDB.writeFileToDisk(jsonCompressed);
			
			System.out.println("Do you want to update DB with blob(true/false): ");
			boolean saveToDb = in.nextBoolean();
			
			if (saveToDb) {
				initDB.savePublishVersion0(initDB.entityManager, pubVersion, jsonCompressed);
				System.out.println("Finally saved in DB. End of process!");
			} else {
				System.out.println("Skipping updating database. End of process!");
			}
		} catch (BeansException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	private String[] validateConfigurationFiles(String[] configurationFiles) {
		
		if (configurationFiles.length == 0) {
			throw new IllegalArgumentException("Missing configuration file as input, for example file:./tak-example.xml which has db configuration");
		}
		
		for (String configFile : configurationFiles) {
			File f = new File(configFile);
			System.out.println("Configuration file : " + f.getAbsolutePath() + ", has been found by file API(true/false): " + f.exists());
		}
		
		return (String[]) configurationFiles;
	}
	
	private PubVersion createCoreVersion() {
		PubVersion pubVersion = new PubVersion();
		pubVersion.setFormatVersion(1L);
		pubVersion.setKommentar("Basic version/Core Data");
 		pubVersion.setTime(new java.sql.Date(System.currentTimeMillis()));
		pubVersion.setUtforare("admin");
		
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
		String fileLocation = System.getProperty("java.io.tmpdir") + "tak-core-version-0.gzip";
		Path path = Paths.get(fileLocation);
		java.nio.file.Files.write(path, jsonCompressed);
		System.out.println("Core version saved on disk at location: " + fileLocation);
	}
	
	private void savePublishVersion0(EntityManager em, PubVersion pubVersion, byte[] jsonCompressed) throws SerialException, SQLException {
		Blob blob = new SerialBlob(jsonCompressed);
		pubVersion.setData(blob);
		pubVersion.setStorlek(jsonCompressed.length);
		
		em.getTransaction().begin();
		em.persist(pubVersion);
		em.getTransaction().commit();
	}
}

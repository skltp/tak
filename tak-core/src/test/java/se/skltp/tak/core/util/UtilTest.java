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

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.skltp.tak.core.memdb.PublishedVersionCache;

public class UtilTest {

//	@Before
//	public void before() {
//	}
	
	@Test
	public void checkFileContentAfterJSONSerialzation() throws Exception {
		String originalJson = new String(readAllBytes(get("./src/test/resources/export.json")));
		PublishedVersionCache cache = new PublishedVersionCache(new String(readAllBytes(get("./src/test/resources/export.json"))));

		String serializedJson = Util.fromPublishedVersionToJSON(cache);
		assertEquals(serializedJson, originalJson);
	}		
}
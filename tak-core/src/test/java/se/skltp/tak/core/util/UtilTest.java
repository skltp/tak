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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class UtilTest {
	
	@Test
	public void checkFileContentAfterDecompress() throws Exception {
		String originalJson = new String(readAllBytes(get("./src/test/resources/export.json")), "utf-8");
		String unCompressedJson = new String(Util.decompress(readAllBytes(get("./src/test/resources/export.gzip"))));
		
		ObjectMapper om = new ObjectMapper();
        Map<String, Object> m1 = (Map<String, Object>)(om.readValue(originalJson, Map.class));
        Map<String, Object> m2 = (Map<String, Object>)(om.readValue(unCompressedJson, Map.class));
            
        assertEquals(m1, m2);
	}

	/**
	 * Main class for easy creation of zipped file
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		makeGzipFile();
	}
	
	/**
	 * Creates a zipped file from an unzipped json file
	 */
	public static void makeGzipFile() {
		// Read JSON uncompressed
		byte[] jsonUncompressed;
		try {
			jsonUncompressed = readAllBytes(get("./src/test/resources/export.json"));
			byte[] jsonCompressed = Util.compress(new String(jsonUncompressed));
			Path path = Paths.get("./src/test/resources/export_new.gzip");	    
			java.nio.file.Files.write(path, jsonCompressed);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}
}
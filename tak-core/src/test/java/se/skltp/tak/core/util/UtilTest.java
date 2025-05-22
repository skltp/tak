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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import  com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class UtilTest {

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
	public static void makeGzipFile() throws IOException {
		Path source = Paths.get("./src/test/resources/export.json");
		Path target = Paths.get("./src/test/resources/export_new.gzip");

		// try-with-resources stänger allt i rätt ordning
		try (InputStream in  = Files.newInputStream(source);
			 OutputStream out = Files.newOutputStream(
					 target,
					 StandardOpenOption.CREATE,
					 StandardOpenOption.TRUNCATE_EXISTING);
			 GZIPOutputStream gz = new GZIPOutputStream(out)) { // 32 kB buffert

			// sedan Java 9 – kopierar strömmen chunk-vis
			in.transferTo(gz);
		}
	}
}
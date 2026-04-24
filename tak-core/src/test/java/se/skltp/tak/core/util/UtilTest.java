/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPOutputStream;

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
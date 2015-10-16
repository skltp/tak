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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.core.entity.Filtercategorization;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.core.entity.Vagval;
import se.skltp.tak.core.memdb.PublishedVersionCache;

public class Util {

	public static byte[] compress(String json) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStream out = new GZIPOutputStream(baos);
			out.write(json.getBytes("UTF-8"));
			out.close();
			return baos.toByteArray();
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public static String decompress(byte[] bytes) {
		InputStream in;
		try {
			in = new GZIPInputStream(new ByteArrayInputStream(bytes));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int len;
			while ((len = in.read(buffer)) > 0)
				baos.write(buffer, 0, len);
			return new String(baos.toByteArray(), "UTF-8");
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}

	public static String fromPublishedVersionToJSON(PublishedVersionCache cache) throws IOException {
		Map<String,Object> pubversion = new LinkedHashMap<String,Object>();
		pubversion.put("formatVersion", "1");
		pubversion.put("version", "1");
		pubversion.put("tidpunkt", "2009-03-10T00:00:00+0100");
		pubversion.put("utforare", "Kalle");
		pubversion.put("kommentar", "kommentar");		
		Map<String,Object> data = new LinkedHashMap<String,Object>();
		pubversion.put("data", data);

		List<LinkedHashMap<String, Object>> rivtaprofiler = new ArrayList<LinkedHashMap<String,Object>>();
		data.put("rivtaprofil", rivtaprofiler);
		for(Entry<Integer, RivTaProfil> entry : cache.rivTaProfil.entrySet()) {
			LinkedHashMap<String, Object> entries = new LinkedHashMap<String,Object>();
			entries.put("id", entry.getValue().getId());
			entries.put("namn", entry.getValue().getNamn());
			entries.put("beskrivning", entry.getValue().getBeskrivning());
			entries.put("pubversion", entry.getValue().getPubVersion());
			rivtaprofiler.add(entries);
		}

		List<LinkedHashMap<String, Object>> tjanstekontrakt = new ArrayList<LinkedHashMap<String,Object>>();
		data.put("tjanstekontrakt", tjanstekontrakt);
		for(Entry<Integer, Tjanstekontrakt> entry : cache.tjanstekontrakt.entrySet()) {
			LinkedHashMap<String, Object> entries = new LinkedHashMap<String,Object>();
			entries.put("id", entry.getValue().getId());
			entries.put("namnrymd", entry.getValue().getNamnrymd());
			entries.put("beskrivning", entry.getValue().getBeskrivning());
			entries.put("majorVersion", entry.getValue().getMajorVersion());
			entries.put("minorVersion", entry.getValue().getMinorVersion());
			entries.put("pubversion", entry.getValue().getPubVersion());
			tjanstekontrakt.add(entries);
		}

		List<LinkedHashMap<String, Object>> logiskAdress = new ArrayList<LinkedHashMap<String,Object>>();
		data.put("logiskadress", logiskAdress);
		for(Entry<Integer, LogiskAdress> entry : cache.logiskAdress.entrySet()) {
			LinkedHashMap<String, Object> entries = new LinkedHashMap<String,Object>();
			entries.put("id", entry.getValue().getId());
			entries.put("hsaId", entry.getValue().getHsaId());
			entries.put("beskrivning", entry.getValue().getBeskrivning());
			entries.put("pubversion", entry.getValue().getPubVersion());
			logiskAdress.add(entries);
		}

		List<LinkedHashMap<String, Object>> tjanstekomponent = new ArrayList<LinkedHashMap<String,Object>>();
		data.put("tjanstekomponent", tjanstekomponent);
		for(Entry<Integer, Tjanstekomponent> entry : cache.tjanstekomponent.entrySet()) {
			LinkedHashMap<String, Object> entries = new LinkedHashMap<String,Object>();
			entries.put("id", entry.getValue().getId());
			entries.put("hsaId", entry.getValue().getHsaId());
			entries.put("beskrivning", entry.getValue().getBeskrivning());
			entries.put("pubversion", entry.getValue().getPubVersion());
			tjanstekomponent.add(entries);
		}

		List<LinkedHashMap<String, Object>> anropsAdress = new ArrayList<LinkedHashMap<String,Object>>();
		data.put("anropsadress", anropsAdress);
		for(Entry<Integer, AnropsAdress> entry : cache.anropsAdress.entrySet()) {
			LinkedHashMap<String, Object> entries = new LinkedHashMap<String,Object>();
			anropsAdress.add(entries);
			entries.put("id", entry.getValue().getId());
			entries.put("adress", entry.getValue().getAdress());
			entries.put("pubversion", entry.getValue().getPubVersion());

			LinkedHashMap<String, Object> relationship = new LinkedHashMap<String, Object>();
			entries.put("relationships", relationship);
			relationship.put("rivtaprofil", entry.getValue().getRivTaProfil().getId());
			relationship.put("tjanstekomponent", entry.getValue().getTjanstekomponent().getId());
		}

		List<LinkedHashMap<String, Object>> anropsbehorighet = new ArrayList<LinkedHashMap<String,Object>>();
		data.put("anropsbehorighet", anropsbehorighet);
		for(Entry<Integer, Anropsbehorighet> entry : cache.anropsbehorighet.entrySet()) {
			LinkedHashMap<String, Object> entries = new LinkedHashMap<String,Object>();
			anropsbehorighet.add(entries);
			entries.put("id", entry.getValue().getId());
			entries.put("integrationsavtal", entry.getValue().getIntegrationsavtal());
			entries.put("fromTidpunkt", PublishedVersionCache.df.format(new Date(entry.getValue().getFromTidpunkt().getTime())));
			entries.put("tomTidpunkt", PublishedVersionCache.df.format(new Date(entry.getValue().getTomTidpunkt().getTime())));
			entries.put("pubversion", entry.getValue().getPubVersion());

			LinkedHashMap<String, Object> relationship= new LinkedHashMap<String,Object>();
			entries.put("relationships", relationship);
			relationship.put("logiskAdress", entry.getValue().getLogiskAdress().getId());
			relationship.put("tjanstekomponent", entry.getValue().getTjanstekonsument().getId());
			relationship.put("tjanstekontrakt", entry.getValue().getTjanstekontrakt().getId());
		}

		List<LinkedHashMap<String, Object>> vagval = new ArrayList<LinkedHashMap<String,Object>>();
		data.put("vagval", vagval);
		for(Entry<Integer, Vagval> entry : cache.vagval.entrySet()) {
			LinkedHashMap<String, Object> entries = new LinkedHashMap<String,Object>();
			vagval.add(entries);
			entries.put("id", entry.getValue().getId());
			entries.put("fromTidpunkt", PublishedVersionCache.df.format(new Date(entry.getValue().getFromTidpunkt().getTime())));
			entries.put("tomTidpunkt", PublishedVersionCache.df.format(new Date(entry.getValue().getTomTidpunkt().getTime())));
			entries.put("pubversion", entry.getValue().getPubVersion());

			LinkedHashMap<String, Object> relationship= new LinkedHashMap<String,Object>();
			entries.put("relationships", relationship);				 
			relationship.put("anropsadress", entry.getValue().getAnropsAdress().getId());
			relationship.put("logiskadress", entry.getValue().getLogiskAdress().getId());
			relationship.put("tjanstekontrakt", entry.getValue().getTjanstekontrakt().getId());
		}

		List<LinkedHashMap<String, Object>> filter = new ArrayList<LinkedHashMap<String,Object>>();
		data.put("filter", filter);
		for(Entry<Integer, Filter> entry : cache.filter.entrySet()) {
			LinkedHashMap<String, Object> entries = new LinkedHashMap<String,Object>();
			filter.add(entries);
			entries.put("id", entry.getValue().getId());
			entries.put("servicedomain", entry.getValue().getServicedomain());
			entries.put("pubversion", entry.getValue().getPubVersion());
			
			LinkedHashMap<String, Object> relationship= new LinkedHashMap<String,Object>();
			entries.put("relationships", relationship);
			relationship.put("anropsbehorighet", entry.getValue().getAnropsbehorighet().getId());
		}

		List<LinkedHashMap<String, Object>> filtercategorization = new ArrayList<LinkedHashMap<String,Object>>();
		data.put("filtercategorization", filtercategorization);
		for(Entry<Integer, Filtercategorization> entry : cache.filtercategorization.entrySet()) {
			LinkedHashMap<String, Object> entries = new LinkedHashMap<String,Object>();
			filtercategorization.add(entries);
			entries.put("id", entry.getValue().getId());
			entries.put("category", entry.getValue().getCategory());
			entries.put("pubversion", entry.getValue().getPubVersion());

			LinkedHashMap<String, Object> relationship= new LinkedHashMap<String,Object>();
			entries.put("relationships", relationship);
			relationship.put("filter", entry.getValue().getFilter().getId());
		}

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.defaultPrettyPrintingWriter().writeValueAsString(pubversion);
		return jsonString;				
	}
}

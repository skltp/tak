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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.core.entity.Filtercategorization;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.core.entity.Vagval;
import se.skltp.tak.core.memdb.PublishedVersionCache;

public class Util {
	
	private static final Logger log = LoggerFactory.getLogger(Util.class);

	public static byte[] compress(String json) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStream out = new GZIPOutputStream(baos);
			out.write(json.getBytes("UTF-8"));
			out.flush();
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
			while ((len = in.read(buffer)) > 0) {
				baos.write(buffer, 0, len);
			}
//			in.close();
			return new String(baos.toByteArray(), "UTF-8");
		} catch (IOException e) {
			throw new AssertionError(e);
		}
	}
	
	public static PublishedVersionCache getPublishedVersionCacheInstance(PubVersion oldPVInstance) {
		PublishedVersionCache pvCache = null;
		
		if (oldPVInstance != null) {
			Blob dataBlob = oldPVInstance.getData();
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				byte[] buffer = new byte[1];
				InputStream is = dataBlob.getBinaryStream();
				while (is.read(buffer) > 0) {
					baos.write(buffer);
				}
			
				String decompressedData = Util.decompress(baos.toByteArray());
				pvCache  = new PublishedVersionCache(decompressedData);
			} catch (Exception e) {
				String errMsg = "Error during process READ BLOB - FETCH STREAM - DECOMPRESS JSON DATA - READ JSON DATA. Cause: " + e.getMessage();
				log.error(errMsg);
				throw new IllegalStateException(errMsg);
			}
		} else {
			pvCache = new PublishedVersionCache();
			log.info("Argument pubVersion is null so creating a new object with empty cache");
		}
		
		return pvCache;
	}

	public static String fromPublishedVersionToJSON(PublishedVersionCache cache) throws IOException {
		Map<String,Object> pubversion = new LinkedHashMap<String,Object>();
		pubversion.put("formatVersion", Long.toString(cache.getFormatVersion()));
		pubversion.put("version", Long.toString(cache.getVersion()));
		pubversion.put("tidpunkt", PublishedVersionCache.df.format(cache.getTime()));
		pubversion.put("utforare", cache.getUtforare());
		pubversion.put("kommentar", cache.getKommentar());		
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
	
	
	public static PublishedVersionCache getPublishedVersionCache( PubVersion pubVersion,
																  List<RivTaProfil> listRTP, 
																  List<Tjanstekontrakt> listTK,
																  List<Tjanstekomponent> listTKomp,
																  List<LogiskAdress> listLA,
																  List<AnropsAdress> listAA,
																  List<Vagval> listVV, 
																  List<Anropsbehorighet> listAB, 
																  List<Filter> listF, 
																  List<Filtercategorization> listFC) {

		PublishedVersionCache newPVC = new PublishedVersionCache();
		
		// PubVersion
		newPVC.setFormatVersion(pubVersion.getFormatVersion());
		newPVC.setKommentar(pubVersion.getKommentar());
		newPVC.setTime(pubVersion.getTime());
		newPVC.setUtforare(pubVersion.getUtforare());
		newPVC.setVersion(pubVersion.getVersion());

		// RivTaProfil
		for (RivTaProfil rtp : listRTP) {
			if (!rtp.getDeleted()) {
				newPVC.rivTaProfil.put((int) rtp.getId(), rtp);
			}			
		}

		// Tjanstekontrakt
		for (Tjanstekontrakt tk : listTK) {
			if (!tk.getDeleted()) {
				newPVC.tjanstekontrakt.put((int) tk.getId(), tk);
			}
		}
		
		// Tjanstekomponent
		for (Tjanstekomponent tk : listTKomp) {
			if (!tk.getDeleted()) {
				newPVC.tjanstekomponent.put((int) tk.getId(), tk);
			}
		}

		// LogiskAdress
		for (LogiskAdress la : listLA) {
			if (!la.getDeleted()) {
				newPVC.logiskAdress.put((int) la.getId(), la);
			}
		}
		
		// AnropsAdress
		for (AnropsAdress aa : listAA) {
			if (!aa.getDeleted()) {
				newPVC.anropsAdress.put((int) aa.getId(), aa);
			}
		}

		// Vagval
		for (Vagval vv : listVV) {
			if (!vv.getDeleted()) {
				newPVC.vagval.put((int) vv.getId(), vv);
			}
		}
		
		// Anropsbehorighet
		for (Anropsbehorighet ab : listAB) {
			if (!ab.getDeleted()) {
				newPVC.anropsbehorighet.put((int) ab.getId(), ab);
			}
		}

		// Filter
		for (Filter f : listF) {
			if (!f.getDeleted()) {
				newPVC.filter.put((int) f.getId(), f);
			}
		}
		
		// FilterCategorization
		for (Filtercategorization fc : listFC) {
			if (!fc.getDeleted()) {
				newPVC.filtercategorization.put((int) fc.getId(), fc);
			}
		}

		return newPVC;
	}
}

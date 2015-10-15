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
package se.skltp.tak.core.memdb;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.codehaus.jackson.map.ObjectMapper;

import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.Filtercategorization;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.core.entity.Vagval;
import se.skltp.tak.core.entity.Filter;


public class PublishedVersionCache {
	private int publishVersion;
	HashMap<Integer, RivTaProfil> 			rivTaProfil = new HashMap<Integer, RivTaProfil>();
	HashMap<Integer, Tjanstekontrakt> 		tjanstekontrakt = new HashMap<Integer, Tjanstekontrakt>();
	HashMap<Integer, LogiskAdress> 			logiskAdress = new HashMap<Integer, LogiskAdress>();
	HashMap<Integer, Tjanstekomponent>		tjanstekomponent = new HashMap<Integer, Tjanstekomponent>();
	
	HashMap<Integer, AnropsAdress> 			anropsAdress = new HashMap<Integer, AnropsAdress>();
	HashMap<Integer, Anropsbehorighet> 		anropsbehorighet = new HashMap<Integer, Anropsbehorighet>();
	HashMap<Integer, Vagval> 				vagval = new HashMap<Integer, Vagval>();
	HashMap<Integer, Filter> 				filter = new HashMap<Integer, Filter>();
	HashMap<Integer, Filtercategorization> 	filtercategorization = new HashMap<Integer, Filtercategorization>();
		
	public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public PublishedVersionCache(String json) {
		
		ObjectMapper mapper = new ObjectMapper();

		try {
			// Read JSON string and map it to a LinkedHashMap structure
			LinkedHashMap jsonMap = (LinkedHashMap) mapper.readValue(json, Object.class);
			
			// Read version of published version and save
			publishVersion = Integer.parseInt((String)jsonMap.get("version"), 10);
			
			initHashMaps((LinkedHashMap) jsonMap.get("data"));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	private void initHashMaps(LinkedHashMap data) {
		rivTaProfil = fillRivTaProfile((ArrayList) data.get("rivtaprofil"));
		tjanstekontrakt = fillTjanstekontrakt((ArrayList) data.get("tjanstekontrakt"));
		logiskAdress = fillLogiskadress((ArrayList) data.get("logiskadress"));
		tjanstekomponent = fillTjanstekomponent((ArrayList) data.get("tjanstekomponent"));

		anropsAdress = fillAnropsadress((ArrayList) data.get("anropsadress"));
		anropsbehorighet = fillAnropsbehorighet((ArrayList) data.get("anropsbehorighet"));
		vagval = fillVagval((ArrayList) data.get("vagval"));
		filter = fillFilter((ArrayList) data.get("filter"));
		filtercategorization = fillFiltercategorization((ArrayList) data.get("filtercategorization"));
	}

	public int getPublishVersion() {
		return publishVersion;
	}
	
	
	
	
	
	
	private  HashMap fillRivTaProfile(ArrayList json_rivtaprofil) {
		HashMap<Integer, RivTaProfil> rivTaProfil_Map = new HashMap<Integer, RivTaProfil>();
		Iterator iterRivTaProfil = json_rivtaprofil.iterator();
		while (iterRivTaProfil.hasNext()) {
			LinkedHashMap val = (LinkedHashMap) iterRivTaProfil.next();
			RivTaProfil rtp = new RivTaProfil();
			rtp.setId((Integer) val.get("id"));
			rtp.setNamn((String) val.get("namn"));
			rtp.setBeskrivning((String) val.get("beskrivning"));
			rivTaProfil_Map.put((Integer) val.get("id"), rtp);
		}
		return rivTaProfil_Map;
	}

	private  HashMap fillTjanstekontrakt(ArrayList json_tjanstekontrakt) {
		HashMap<Integer, Tjanstekontrakt> tjanstekontrakt_Map = new HashMap<Integer, Tjanstekontrakt>();
		Iterator iterTjanstekontrakt = json_tjanstekontrakt.iterator();
		while (iterTjanstekontrakt.hasNext()) {
			LinkedHashMap val = (LinkedHashMap) iterTjanstekontrakt.next();
			Tjanstekontrakt tk = new Tjanstekontrakt();
			tk.setId((Integer) val.get("id"));
			tk.setNamnrymd((String) val.get("namnrymd"));
			tk.setBeskrivning((String) val.get("beskrivning"));
			tk.setMajorVersion((Integer) val.get("majorVersion"));
			tk.setMinorVersion((Integer) val.get("minorVersion"));
			tjanstekontrakt_Map.put((Integer) val.get("id"), tk);
		}
		return tjanstekontrakt_Map;
	}

	private  HashMap fillLogiskadress(ArrayList json_logiskadress) {
		HashMap<Integer, LogiskAdress> logiskadress_Map = new HashMap<Integer, LogiskAdress>();
		Iterator iterLogiskAdress = json_logiskadress.iterator();
		while (iterLogiskAdress.hasNext()) {
			LinkedHashMap val = (LinkedHashMap) iterLogiskAdress.next();
			LogiskAdress la = new LogiskAdress();
			la.setId((Integer) val.get("id"));
			la.setHsaId((String) val.get("hsaId"));
			la.setBeskrivning((String) val.get("beskrivning"));
			logiskadress_Map.put((Integer) val.get("id"), la);
		}
		return logiskadress_Map;
	}

	private  HashMap fillTjanstekomponent(ArrayList json_tjanstekomponent) {
		HashMap<Integer, Tjanstekomponent> tjanstekomponent_Map = new HashMap<Integer, Tjanstekomponent>();
		Iterator iterTjanstekomponent = json_tjanstekomponent.iterator();
		while (iterTjanstekomponent.hasNext()) {
			LinkedHashMap val = (LinkedHashMap) iterTjanstekomponent.next();
			Tjanstekomponent tk = new Tjanstekomponent();
			tk.setId((Integer) val.get("id"));
			tk.setHsaId((String) val.get("hsaId"));
			tk.setBeskrivning((String) val.get("beskrivning"));
			tjanstekomponent_Map.put((Integer) val.get("id"), tk);
		}
		return tjanstekomponent_Map;
	}

	private  HashMap fillAnropsadress(ArrayList json_anropsadress) {
		HashMap<Integer, AnropsAdress> anropsadress_Map = new HashMap<Integer, AnropsAdress>();
		Iterator iterAnropsadress = json_anropsadress.iterator();
		while (iterAnropsadress.hasNext()) {
			LinkedHashMap val = (LinkedHashMap) iterAnropsadress.next();
			AnropsAdress aa = new AnropsAdress();
			aa.setId((Integer) val.get("id"));
			aa.setAdress((String) val.get("adress"));
			LinkedHashMap relationships = (LinkedHashMap) val.get("relationships");
			
			int index_rivtaprofil = (Integer) relationships.get("rivtaprofil");
			aa.setRivTaProfil((RivTaProfil) rivTaProfil.get(index_rivtaprofil));

			// Add this anropsadress to list in rivtaprofiler
			rivTaProfil.get(index_rivtaprofil).getAnropsAdresser().add(aa);

			int index_tjanstekomponent = (Integer) relationships.get("tjanstekomponent");
			aa.setTjanstekomponent((Tjanstekomponent) tjanstekomponent.get(index_tjanstekomponent));

			// Add this anropsadress to list in tjanstekomponenter
			tjanstekomponent.get(index_tjanstekomponent).getAnropsAdresser().add(aa);

			anropsadress_Map.put((Integer) val.get("id"), aa);
		}
		return anropsadress_Map;
	}

	private  HashMap fillAnropsbehorighet(ArrayList json_anropsbehorighet) {
		HashMap<Integer, Anropsbehorighet> anropsbehorighet_Map = new HashMap<Integer, Anropsbehorighet>();
		Iterator iterAnropsadress = json_anropsbehorighet.iterator();
		while (iterAnropsadress.hasNext()) {
			LinkedHashMap val = (LinkedHashMap) iterAnropsadress.next();
			Anropsbehorighet ab = new Anropsbehorighet();
			ab.setId((Integer) val.get("id"));
			ab.setIntegrationsavtal((String) val.get("integrationsavtal"));
			try {
				ab.setFromTidpunkt(new java.sql.Date(df.parse((String) val.get("fromTidpunkt")).getTime()));
				ab.setTomTidpunkt(new java.sql.Date(df.parse((String) val.get("tomTidpunkt")).getTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			LinkedHashMap relationships = (LinkedHashMap) val.get("relationships");

			int index_logiskadress = (Integer) relationships.get("logiskAdress");
			ab.setLogiskAdress((LogiskAdress) logiskAdress.get(index_logiskadress));

			// Add this anropsbehorighet to list in logiska adresser
			logiskAdress.get(index_logiskadress).getAnropsbehorigheter().add(ab);

			int index_tjanstekomponent = (Integer) relationships.get("tjanstekomponent");
			ab.setTjanstekonsument((Tjanstekomponent) tjanstekomponent.get(index_tjanstekomponent));

			// Add this anropsbehorighet to list in tjanstekomponenter
			tjanstekomponent.get(index_tjanstekomponent).getAnropsbehorigheter().add(ab);

			int index_tjanstekontrakt = (Integer) relationships.get("tjanstekontrakt");
			ab.setTjanstekontrakt((Tjanstekontrakt) tjanstekontrakt.get(index_tjanstekontrakt));

			// Add this anropsbehorighet to list in tjanstekontrakt
			tjanstekontrakt.get(index_tjanstekontrakt).getAnropsbehorigheter().add(ab);

			anropsbehorighet_Map.put((Integer) val.get("id"), ab);
		}
		return anropsbehorighet_Map;
	}

	private  HashMap fillVagval(ArrayList json_vagval) {
		HashMap<Integer, Vagval> vagval_Map = new HashMap<Integer, Vagval>();
		Iterator iterVagval = json_vagval.iterator();
		while (iterVagval.hasNext()) {
			LinkedHashMap val = (LinkedHashMap) iterVagval.next();
			Vagval vv = new Vagval();
			vv.setId((Integer) val.get("id"));
			try {
				vv.setFromTidpunkt(new java.sql.Date(df.parse((String) val.get("fromTidpunkt")).getTime()));
				vv.setTomTidpunkt(new java.sql.Date(df.parse((String) val.get("tomTidpunkt")).getTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			LinkedHashMap relationships = (LinkedHashMap) val.get("relationships");

			int index_anropsadress = (Integer) relationships.get("anropsadress");
			vv.setAnropsAdress((AnropsAdress) anropsAdress.get(index_anropsadress));

			// Add this vagval to list in anropsadresser
			anropsAdress.get(index_anropsadress).getVagVal().add(vv);
			
			int index_logiskadress = (Integer) relationships.get("logiskadress");
			vv.setLogiskAdress((LogiskAdress) logiskAdress.get(index_logiskadress));

			// Add this vagval to list in logiska adresser
			logiskAdress.get(index_logiskadress).getVagval().add(vv);

			int index_tjanstekontrakt = (Integer) relationships.get("tjanstekontrakt");
			vv.setTjanstekontrakt((Tjanstekontrakt) tjanstekontrakt.get(index_tjanstekontrakt));

			// Add this vagval to list in tjanstekontrakt
			tjanstekontrakt.get(index_tjanstekontrakt).getVagval().add(vv);

			vagval_Map.put((Integer) val.get("id"), vv);
		}
		return vagval_Map;
	}

	private  HashMap fillFilter(ArrayList json_filter) {
		HashMap<Integer, Filter> filter_Map = new HashMap<Integer, Filter>();
		Iterator iterFilter = json_filter.iterator();
		while (iterFilter.hasNext()) {
			LinkedHashMap val = (LinkedHashMap) iterFilter.next();
			Filter f = new Filter();
			f.setId((Integer) val.get("id"));
			f.setServicedomain((String) val.get("servicedomain"));
			LinkedHashMap relationships = (LinkedHashMap) val.get("relationships");

			int index_anropsbehorighet = (Integer) relationships.get("anropsbehorighet");
			f.setAnropsbehorighet((Anropsbehorighet) anropsbehorighet.get(index_anropsbehorighet));

			// Add this filter to list in anropsbehorigheter
			anropsbehorighet.get(index_anropsbehorighet).getFilter().add(f);
			
			filter_Map.put((Integer) val.get("id"), f);
		}
		return filter_Map;
	}

	private  HashMap fillFiltercategorization(ArrayList json_filtercategorization) {
		HashMap<Integer, Filtercategorization> filterategorization_Map = new HashMap<Integer, Filtercategorization>();
		Iterator iterFilterategorization = json_filtercategorization.iterator();
		while (iterFilterategorization.hasNext()) {
			LinkedHashMap val = (LinkedHashMap) iterFilterategorization.next();
			Filtercategorization fc = new Filtercategorization();
			fc.setId((Integer) val.get("id"));
			fc.setCategory((String) val.get("category"));
			LinkedHashMap relationships = (LinkedHashMap) val.get("relationships");

			int index_filter = (Integer) relationships.get("filter");
			fc.setFilter((Filter) filter.get(index_filter));
			
			// add this filtercategory to list in filter
			filter.get(index_filter).getCategorization().add(fc);
			
			filterategorization_Map.put((Integer) val.get("id"), fc);
		}
		return filterategorization_Map;
	}


}

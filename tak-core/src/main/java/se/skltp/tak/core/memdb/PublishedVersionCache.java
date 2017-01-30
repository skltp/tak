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

import java.util.Date;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.core.entity.Filtercategorization;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.core.entity.Vagval;


public class PublishedVersionCache {
	private long formatVersion;
	private Date time;
	private String utforare;
	private String kommentar;
	private long version;
	
	public HashMap<Integer, RivTaProfil> 			rivTaProfil = new HashMap<Integer, RivTaProfil>();
	public HashMap<Integer, Tjanstekontrakt> 		tjanstekontrakt = new HashMap<Integer, Tjanstekontrakt>();
	public HashMap<Integer, LogiskAdress> 			logiskAdress = new HashMap<Integer, LogiskAdress>();
	public HashMap<Integer, Tjanstekomponent>		tjanstekomponent = new HashMap<Integer, Tjanstekomponent>();
	
	public HashMap<Integer, AnropsAdress> 			anropsAdress = new HashMap<Integer, AnropsAdress>();
	public HashMap<Integer, Anropsbehorighet> 		anropsbehorighet = new HashMap<Integer, Anropsbehorighet>();
	public HashMap<Integer, Vagval> 				vagval = new HashMap<Integer, Vagval>();
	public HashMap<Integer, Filter> 				filter = new HashMap<Integer, Filter>();
	public HashMap<Integer, Filtercategorization> 	filtercategorization = new HashMap<Integer, Filtercategorization>();
		
	public static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	private static final Logger log = LoggerFactory.getLogger(PublishedVersionCache.class);

	public PublishedVersionCache() {
		
	}
	
	public PublishedVersionCache(String json) {
		initCache(json);
	}
	
	private void initCache(String json) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			log.info("Reading json data into #initCache(json) length: " + json.length()); 
			// Read JSON string and map it to a LinkedHashMap structure
			LinkedHashMap<?, ?> jsonMap = (LinkedHashMap<?, ?>) mapper.readValue(json, Object.class);
			
			// Read version of published version and save
			formatVersion = Integer.parseInt((String)jsonMap.get("formatVersion"), 10);
			time = new java.sql.Date(df.parse((String) jsonMap.get("tidpunkt")).getTime());
			utforare = (String)jsonMap.get("utforare");
			kommentar = (String)jsonMap.get("kommentar");
			version = Integer.parseInt((String)jsonMap.get("version"), 10);
			log.info("json data: published version by : " + utforare + ", kommentar: " + kommentar + ", time: " + time); 
			
			initHashMaps((LinkedHashMap<?, List<LinkedHashMap<?, ?>>>) jsonMap.get("data"));

		} catch (ParseException ex) {
			log.error(ex.getMessage());
			throw new RuntimeException(ex);				
		} catch (IOException ex) {
			log.error(ex.getMessage());
			throw new RuntimeException(ex);
		}		
	}
	
	private void initHashMaps(LinkedHashMap<?, List<LinkedHashMap<?, ?>>> data) {
		rivTaProfil = fillRivTaProfile(data.get("rivtaprofil"));
		tjanstekontrakt = fillTjanstekontrakt(data.get("tjanstekontrakt"));
		logiskAdress = fillLogiskadress(data.get("logiskadress"));
		tjanstekomponent = fillTjanstekomponent(data.get("tjanstekomponent"));

		anropsAdress = fillAnropsadress(data.get("anropsadress"));
		anropsbehorighet = fillAnropsbehorighet(data.get("anropsbehorighet"));
		vagval = fillVagval(data.get("vagval"));
		filter = fillFilter(data.get("filter"));
		filtercategorization = fillFiltercategorization(data.get("filtercategorization"));
		
		log.info("Finished reading json data into HashMaps");
	}
	
	public long getFormatVersion() {
		return formatVersion;
	}

	public void setFormatVersion(long formatVersion) {
		this.formatVersion = formatVersion;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getUtforare() {
		return utforare;
	}

	public void setUtforare(String utforare) {
		this.utforare = utforare;
	}

	public String getKommentar() {
		return kommentar;
	}

	public void setKommentar(String kommentar) {
		this.kommentar = kommentar;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	private  HashMap<Integer, RivTaProfil> fillRivTaProfile(List<LinkedHashMap<?, ?>> json_rivtaprofil) {
		HashMap<Integer, RivTaProfil> rivTaProfil_Map = new HashMap<Integer, RivTaProfil>();
		Iterator<LinkedHashMap<?, ?>> iterRivTaProfil = json_rivtaprofil.iterator();
		while (iterRivTaProfil.hasNext()) {
			Map<?, ?> val = iterRivTaProfil.next();
			RivTaProfil rtp = new RivTaProfil();
			rtp.setId((Integer) val.get("id"));
			rtp.setNamn((String) val.get("namn"));
			rtp.setBeskrivning((String) val.get("beskrivning"));
			rtp.setPubVersion((String) val.get("pubversion"));
			rivTaProfil_Map.put((Integer) val.get("id"), rtp);
		}
		
		log.info("Finished reading RivTaProfil: HashMap size: " + rivTaProfil_Map.size());
		return rivTaProfil_Map;
	}

	private  HashMap<Integer, Tjanstekontrakt> fillTjanstekontrakt(List<LinkedHashMap<?, ?>> json_tjanstekontrakt) {
		HashMap<Integer, Tjanstekontrakt> tjanstekontrakt_Map = new HashMap<Integer, Tjanstekontrakt>();
		Iterator<LinkedHashMap<?, ?>> iterTjanstekontrakt = json_tjanstekontrakt.iterator();
		while (iterTjanstekontrakt.hasNext()) {
			Map<?, ?> val =  iterTjanstekontrakt.next();
			Tjanstekontrakt tk = new Tjanstekontrakt();
			tk.setId((Integer) val.get("id"));
			tk.setNamnrymd((String) val.get("namnrymd"));
			tk.setBeskrivning((String) val.get("beskrivning"));
			tk.setMajorVersion((Integer) val.get("majorVersion"));
			tk.setMinorVersion((Integer) val.get("minorVersion"));
			tk.setPubVersion((String) val.get("pubversion"));
			tjanstekontrakt_Map.put((Integer) val.get("id"), tk);
		}
		
		log.info("Finished reading Tjanstekontrakt: HashMap size: " + tjanstekontrakt_Map.size());
		return tjanstekontrakt_Map;
	}

	private  HashMap<Integer, LogiskAdress> fillLogiskadress(List<LinkedHashMap<?, ?>> json_logiskadress) {
		HashMap<Integer, LogiskAdress> logiskadress_Map = new HashMap<Integer, LogiskAdress>();
		Iterator<LinkedHashMap<?, ?>> iterLogiskAdress = json_logiskadress.iterator();
		while (iterLogiskAdress.hasNext()) {
			Map<?, ?> val = iterLogiskAdress.next();
			LogiskAdress la = new LogiskAdress();
			la.setId((Integer) val.get("id"));
			la.setHsaId((String) val.get("hsaId"));
			la.setBeskrivning((String) val.get("beskrivning"));
			la.setPubVersion((String) val.get("pubversion"));
			logiskadress_Map.put((Integer) val.get("id"), la);
		}
		
		log.info("Finished reading LogiskAdress: HashMap size: " + logiskadress_Map.size());
		return logiskadress_Map;
	}

	private  HashMap<Integer, Tjanstekomponent> fillTjanstekomponent(List<LinkedHashMap<?, ?>> json_tjanstekomponent) {
		HashMap<Integer, Tjanstekomponent> tjanstekomponent_Map = new HashMap<Integer, Tjanstekomponent>();
		Iterator<LinkedHashMap<?, ?>> iterTjanstekomponent = json_tjanstekomponent.iterator();
		while (iterTjanstekomponent.hasNext()) {
			Map<?, ?> val = iterTjanstekomponent.next();
			Tjanstekomponent tk = new Tjanstekomponent();
			tk.setId((Integer) val.get("id"));
			tk.setHsaId((String) val.get("hsaId"));
			tk.setBeskrivning((String) val.get("beskrivning"));
			tk.setPubVersion((String) val.get("pubversion"));
			tjanstekomponent_Map.put((Integer) val.get("id"), tk);
		}
		
		log.info("Finished reading Tjanstekomponent: HashMap size: " + tjanstekomponent_Map.size());
		return tjanstekomponent_Map;
	}

	private  HashMap<Integer, AnropsAdress> fillAnropsadress(List<LinkedHashMap<?, ?>> json_anropsadress) {
		HashMap<Integer, AnropsAdress> anropsadress_Map = new HashMap<Integer, AnropsAdress>();
		Iterator<LinkedHashMap<?, ?>> iterAnropsadress = json_anropsadress.iterator();
		while (iterAnropsadress.hasNext()) {
			Map<?, ?> val = iterAnropsadress.next();
			AnropsAdress aa = new AnropsAdress();
			aa.setId((Integer) val.get("id"));
			aa.setAdress((String) val.get("adress"));
			aa.setPubVersion((String) val.get("pubversion"));
			Map<?, ?> relationships =  (Map<?, ?>) val.get("relationships");
			
			int index_rivtaprofil = (Integer) relationships.get("rivtaprofil");
			logErrorMsgIfMissingRelation(rivTaProfil, index_rivtaprofil, RivTaProfil.class);
			aa.setRivTaProfil((RivTaProfil) rivTaProfil.get(index_rivtaprofil));

			// Add this anropsadress to list in rivtaprofiler
			rivTaProfil.get(index_rivtaprofil).getAnropsAdresser().add(aa);

			int index_tjanstekomponent = (Integer) relationships.get("tjanstekomponent");
			logErrorMsgIfMissingRelation(tjanstekomponent, index_tjanstekomponent, Tjanstekomponent.class);
			aa.setTjanstekomponent((Tjanstekomponent) tjanstekomponent.get(index_tjanstekomponent));

			// Add this anropsadress to list in tjanstekomponenter
			tjanstekomponent.get(index_tjanstekomponent).getAnropsAdresser().add(aa);

			anropsadress_Map.put((Integer) val.get("id"), aa);
		}
		
		log.info("Finished reading AnropsAdress: HashMap size: " + anropsadress_Map.size());
		return anropsadress_Map;
	}

	private  HashMap<Integer, Anropsbehorighet> fillAnropsbehorighet(List<LinkedHashMap<?, ?>> json_anropsbehorighet) {
		HashMap<Integer, Anropsbehorighet> anropsbehorighet_Map = new HashMap<Integer, Anropsbehorighet>();
		Iterator<LinkedHashMap<?, ?>> iterAnropsadress = json_anropsbehorighet.iterator();
		while (iterAnropsadress.hasNext()) {
			Map<?, ?> val = iterAnropsadress.next();
			Anropsbehorighet ab = new Anropsbehorighet();
			ab.setId((Integer) val.get("id"));
			ab.setIntegrationsavtal((String) val.get("integrationsavtal"));
			try {
				ab.setFromTidpunkt(new java.sql.Date(df.parse((String) val.get("fromTidpunkt")).getTime()));
				ab.setTomTidpunkt(new java.sql.Date(df.parse((String) val.get("tomTidpunkt")).getTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			ab.setPubVersion((String) val.get("pubversion"));

			Map<?, ?> relationships =  (Map<?, ?>) val.get("relationships");

			int index_logiskadress = (Integer) relationships.get("logiskAdress");
			logErrorMsgIfMissingRelation(logiskAdress, index_logiskadress, LogiskAdress.class);
			
			ab.setLogiskAdress((LogiskAdress) logiskAdress.get(index_logiskadress));

			// Add this anropsbehorighet to list in logiska adresser
			logiskAdress.get(index_logiskadress).getAnropsbehorigheter().add(ab);

			int index_tjanstekomponent = (Integer) relationships.get("tjanstekomponent");
			logErrorMsgIfMissingRelation(tjanstekomponent, index_tjanstekomponent, Tjanstekomponent.class);
			ab.setTjanstekonsument((Tjanstekomponent) tjanstekomponent.get(index_tjanstekomponent));

			// Add this anropsbehorighet to list in tjanstekomponenter
			tjanstekomponent.get(index_tjanstekomponent).getAnropsbehorigheter().add(ab);

			int index_tjanstekontrakt = (Integer) relationships.get("tjanstekontrakt");
			logErrorMsgIfMissingRelation(tjanstekontrakt, index_tjanstekontrakt, Tjanstekontrakt.class);
			ab.setTjanstekontrakt((Tjanstekontrakt) tjanstekontrakt.get(index_tjanstekontrakt));

			// Add this anropsbehorighet to list in tjanstekontrakt
			tjanstekontrakt.get(index_tjanstekontrakt).getAnropsbehorigheter().add(ab);

			anropsbehorighet_Map.put((Integer) val.get("id"), ab);
		}
		
		log.info("Finished reading Anropsbehorighet: HashMap size: " + anropsbehorighet_Map.size());
		return anropsbehorighet_Map;
	}

	private  HashMap<Integer, Vagval> fillVagval(List<LinkedHashMap<?, ?>> json_vagval) {
		HashMap<Integer, Vagval> vagval_Map = new HashMap<Integer, Vagval>();
		Iterator<LinkedHashMap<?, ?>> iterVagval = json_vagval.iterator();
		while (iterVagval.hasNext()) {
			Map<?, ?> val = iterVagval.next();
			Vagval vv = new Vagval();
			vv.setId((Integer) val.get("id"));
			try {
				vv.setFromTidpunkt(new java.sql.Date(df.parse((String) val.get("fromTidpunkt")).getTime()));
				vv.setTomTidpunkt(new java.sql.Date(df.parse((String) val.get("tomTidpunkt")).getTime()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			vv.setPubVersion((String) val.get("pubversion"));

			Map<?, ?> relationships =  (Map<?, ?>) val.get("relationships");

			int index_anropsadress = (Integer) relationships.get("anropsadress");
			logErrorMsgIfMissingRelation(anropsAdress, index_anropsadress, AnropsAdress.class);
			vv.setAnropsAdress((AnropsAdress) anropsAdress.get(index_anropsadress));

			// Add this vagval to list in anropsadresser
			anropsAdress.get(index_anropsadress).getVagVal().add(vv);
			
			int index_logiskadress = (Integer) relationships.get("logiskadress");
			logErrorMsgIfMissingRelation(logiskAdress, index_logiskadress, LogiskAdress.class);
			vv.setLogiskAdress((LogiskAdress) logiskAdress.get(index_logiskadress));

			// Add this vagval to list in logiska adresser
			logiskAdress.get(index_logiskadress).getVagval().add(vv);

			int index_tjanstekontrakt = (Integer) relationships.get("tjanstekontrakt");
			logErrorMsgIfMissingRelation(tjanstekontrakt, index_tjanstekontrakt, Tjanstekontrakt.class);
			vv.setTjanstekontrakt((Tjanstekontrakt) tjanstekontrakt.get(index_tjanstekontrakt));

			// Add this vagval to list in tjanstekontrakt
			tjanstekontrakt.get(index_tjanstekontrakt).getVagval().add(vv);

			vagval_Map.put((Integer) val.get("id"), vv);
		}
		return vagval_Map;
	}

	private  HashMap<Integer, Filter> fillFilter(List<LinkedHashMap<?, ?>> json_filter) {
		HashMap<Integer, Filter> filter_Map = new HashMap<Integer, Filter>();
		Iterator<LinkedHashMap<?, ?>> iterFilter = json_filter.iterator();
		while (iterFilter.hasNext()) {
			Map<?, ?> val = iterFilter.next();
			Filter f = new Filter();
			f.setId((Integer) val.get("id"));
			f.setServicedomain((String) val.get("servicedomain"));
			f.setPubVersion((String) val.get("pubversion"));
			
			Map<?, ?> relationships =  (Map<?, ?>) val.get("relationships");

			int index_anropsbehorighet = (Integer) relationships.get("anropsbehorighet");
			logErrorMsgIfMissingRelation(anropsbehorighet, index_anropsbehorighet, Anropsbehorighet.class);
			f.setAnropsbehorighet((Anropsbehorighet) anropsbehorighet.get(index_anropsbehorighet));

			// Add this filter to list in anropsbehorigheter
			anropsbehorighet.get(index_anropsbehorighet).getFilter().add(f);
			
			filter_Map.put((Integer) val.get("id"), f);
		}
		
		log.info("Finished reading Filter: HashMap size: " + filter_Map.size());
		return filter_Map;
	}

	private  HashMap<Integer, Filtercategorization> fillFiltercategorization(List<LinkedHashMap<?, ?>> json_filtercategorization) {
		HashMap<Integer, Filtercategorization> filterategorization_Map = new HashMap<Integer, Filtercategorization>();
		Iterator<LinkedHashMap<?, ?>> iterFilterategorization = json_filtercategorization.iterator();
		while (iterFilterategorization.hasNext()) {
			Map<?, ?> val = iterFilterategorization.next();
			Filtercategorization fc = new Filtercategorization();
			fc.setId((Integer) val.get("id"));
			fc.setCategory((String) val.get("category"));
			fc.setPubVersion((String) val.get("pubversion"));
			
			Map<?, ?> relationships =  (Map<?, ?>) val.get("relationships");

			int index_filter = (Integer) relationships.get("filter");
			logErrorMsgIfMissingRelation(filter, index_filter, Filter.class);
			fc.setFilter((Filter) filter.get(index_filter));
			
			// add this filtercategory to list in filter
			filter.get(index_filter).getCategorization().add(fc);
			
			filterategorization_Map.put((Integer) val.get("id"), fc);
		}
		
		log.info("Finished reading Filtercategorization: HashMap size: " + filterategorization_Map.size());
		return filterategorization_Map;
	}
	
	private AbstractVersionInfo logErrorMsgIfMissingRelation(Map<Integer, ? extends AbstractVersionInfo> map, int index, Class<?> c) {
		AbstractVersionInfo versionInfo = map.get(index); 
		
		if (versionInfo == null) {
			String msg = "Missing relationship in jsonfile in version " + getVersion() +", for Entity: " + c + ", with database index: " + index;
			throw new IllegalStateException(msg);
		}
		return versionInfo;
	}

}

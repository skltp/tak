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
package se.skltp.tak.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import se.skltp.tak.core.dao.PubVersionDao;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.core.memdb.PublishedVersionCache;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;


@Path("/")
public class ExportTakData {
	// Json keys
	private static final String ID = "id";
	private static final String ADRESS = "adress";
	private static final String PUBVERSION = "pubversion";
	private static final String RELATIONSHIPS = "relationships";
	private static final String RIVTAPROFIL = "rivtaprofil";
	private static final String TJANSTEKOMPONENT = "tjanstekomponent";
	private static final String NAMN = "namn";
	private static final String BESKRIVNING = "beskrivning";
	private static final String TJANSTEKONTRAKT = "tjanstekontrakt";
	private static final String NAMNRYMD = "namnrymd";
	private static final String MAJOR_VERSION = "majorVersion";
	private static final String MINOR_VERSION = "minorVersion";
	private static final String LOGISKADRESS = "logiskadress";
	private static final String HSA_ID = "hsaId";
	private static final String VAGVAL = "vagval";
	private static final String FROM_TIDPUNKT = "fromTidpunkt";
	private static final String TOM_TIDPUNKT = "tomTidpunkt";
	private static final String LOGISK_ADRESS = "logiskAdress";
	private static final String ANROPSADRESS = "anropsadress";
	private static final String FILTER = "filter";
	private static final String SERVICEDOMAIN = "servicedomain";
	private static final String ANROPSBEHORIGHET = "anropsbehorighet";
	private static final String FILTERCATEGORIZATION = "filtercategorization";
	private static final String CATEGORY = "category";
	private static final String INTEGRATIONSAVTAL = "integrationsavtal";
	private static final String TJANSTEKONSUMENT = "tjanstekonsument";
	private static final String DATA = "data";
	private static final String STATUS = "Status";
	private static final String MESSAGE = "message";

	private PubVersionDao pubVersionDao;

	public ExportTakData(PubVersionDao pubVersionDao) {
		this.pubVersionDao = pubVersionDao;
	}

	private StdSerializer<Date> createDateSerializer() {
		return new StdSerializer<Date>(Date.class) {
			@Override
			public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
				String formattedDate = PublishedVersionCache.df.format(value);
				gen.writeString(formattedDate);
			}
		};
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response export(@QueryParam("version") Integer version,
						   @QueryParam("pretty") boolean prettyPrint) throws JsonProcessingException {

		PublishedVersionCache cachePv;
		if (version == null) {
			cachePv = pubVersionDao.getLatestPublishedVersionCache();
		} else {
			try {
				cachePv = pubVersionDao.getPublishedVersionCacheOnId(version);
			} catch (IllegalStateException e) {
				return mapToResponse(Map.of(
						STATUS, "ERROR",
						MESSAGE, e.getMessage()
						), prettyPrint);
			}
		}

		return mapToResponse(createResponse(cachePv), prettyPrint);
	}

	private Response mapToResponse(Map<String , Object> respMap, boolean pretty) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new SimpleModule().addSerializer(Date.class, createDateSerializer()));
		if (pretty) {
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
		}
		return Response.ok(mapper.writeValueAsString(respMap)).build();
	}

	private HashMap<String, Object> createResponse(PublishedVersionCache cachePv) {

		HashMap<String, Object> response = new HashMap<>();

		response.put("formatVersion", cachePv.getFormatVersion());
		response.put("tidpunkt", cachePv.getTime());
		response.put("utforare", cachePv.getUtforare());
		response.put("kommentar", cachePv.getKommentar());
		response.put("version", cachePv.getVersion());

		HashMap<Object, Object> respData = new HashMap<>();
		respData.put(ANROPSADRESS, mapEntities(cachePv.anropsAdress, this::entityToMap));
		respData.put(RIVTAPROFIL, mapEntities(cachePv.rivTaProfil, this::entityToMap));
		respData.put(TJANSTEKONTRAKT, mapEntities(cachePv.tjanstekontrakt, this::entityToMap));
		respData.put(LOGISKADRESS, mapEntities(cachePv.logiskAdress, this::entityToMap));
		respData.put(TJANSTEKOMPONENT, mapEntities(cachePv.tjanstekomponent, this::entityToMap));
		respData.put(VAGVAL, mapEntities(cachePv.vagval, this::entityToMap));
		respData.put(FILTER, mapEntities(cachePv.filter, this::entityToMap));
		respData.put(FILTERCATEGORIZATION, mapEntities(cachePv.filtercategorization, this::entityToMap));
		respData.put(ANROPSBEHORIGHET, mapEntities(cachePv.anropsbehorighet, this::entityToMap));

		response.put(DATA, respData);
		return response;
	}

	private <T> List<Map<String, Object>> mapEntities(Map<Integer, T> entities, Function<T, Map<String, Object>> mapper) {
		return entities.values().stream().map(mapper).toList();
	}

	private Map<String, Object> entityToMap(Anropsbehorighet row) {
		return Map.of(
				ID, row.getId(),
				INTEGRATIONSAVTAL, row.getIntegrationsavtal(),
				FROM_TIDPUNKT, row.getFromTidpunkt(),
				TOM_TIDPUNKT, row.getTomTidpunkt(),
				PUBVERSION, row.getPubVersion(),
				RELATIONSHIPS, Map.of(
						LOGISK_ADRESS, row.getLogiskAdress().getHsaId(),
						TJANSTEKONSUMENT, row.getTjanstekonsument().getHsaId(),
						TJANSTEKONTRAKT, row.getTjanstekontrakt().getNamnrymd()
				));
	}

	private Map<String, Object> entityToMap(Filtercategorization row) {
		return Map.of(
				ID, row.getId(),
				PUBVERSION, row.getPubVersion(),
				CATEGORY, row.getCategory(),
				RELATIONSHIPS, Map.of(
						FILTER, row.getFilter().getId()
				));
	}

	private Map<String, Object> entityToMap(Filter row) {
		return Map.of(
				ID, row.getId(),
				SERVICEDOMAIN, row.getServicedomain(),
				PUBVERSION, row.getPubVersion(),
				RELATIONSHIPS, Map.of(
						ANROPSBEHORIGHET, row.getAnropsbehorighet().getId()
				));
	}

	private Map<String, Object> entityToMap(Vagval row) {
		return Map.of(
				ID, row.getId(),
				FROM_TIDPUNKT, row.getFromTidpunkt(),
				TOM_TIDPUNKT, row.getTomTidpunkt(),
				PUBVERSION, row.getPubVersion(),
				RELATIONSHIPS, Map.of(
						LOGISK_ADRESS, row.getLogiskAdress().getHsaId(),
						ANROPSADRESS, row.getAnropsAdress().getAdress(),
						TJANSTEKONTRAKT, row.getTjanstekontrakt().getNamnrymd()
				));
	}

	private Map<String, Object> entityToMap(Tjanstekomponent row) {
		return Map.of(
				ID, row.getId(),
				HSA_ID, row.getHsaId(),
				BESKRIVNING, null2Empty(row.getBeskrivning()),
				PUBVERSION, row.getPubVersion()
		);
	}

	private Map<String, Object> entityToMap(LogiskAdress row) {
		return Map.of(
				ID, row.getId(),
				HSA_ID, row.getHsaId(),
				BESKRIVNING, null2Empty(row.getBeskrivning()),
				PUBVERSION, row.getPubVersion()
		);
	}

	private Map<String, Object> entityToMap(Tjanstekontrakt row) {
		return Map.of(
				ID, row.getId(),
				NAMNRYMD, row.getNamnrymd(),
				BESKRIVNING, null2Empty(row.getBeskrivning()),
				MAJOR_VERSION, row.getMajorVersion(),
				MINOR_VERSION, row.getMinorVersion(),
				PUBVERSION, row.getPubVersion()
		);
	}
	private Map<String, Object> entityToMap(AnropsAdress row) {
		return Map.of(
				ID, row.getId(),
				ADRESS, row.getAdress(),
				PUBVERSION, row.getPubVersion(),
				RELATIONSHIPS, Map.of(
						RIVTAPROFIL, row.getRivTaProfil().getNamn(),
						TJANSTEKOMPONENT, row.getTjanstekomponent().getHsaId()
				)
		);
	}
	private Map<String, Object> entityToMap(RivTaProfil row) {
		return Map.of(
				ID, row.getId(),
				NAMN, row.getNamn(),
				BESKRIVNING, null2Empty(row.getBeskrivning()),
				PUBVERSION, row.getPubVersion()
		);
	}

	private String null2Empty(String notNull) {
		if (notNull == null) {
			return "";
		}
		return notNull;
	}
}

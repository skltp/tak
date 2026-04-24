/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.memdb.LatestPublishedVersion;

@Service()
public class TjanstekomponentDao {
	@Autowired
	private LatestPublishedVersion lpv;

	public List<Tjanstekomponent> getAllTjanstekomponentAndAnropsAdresserAndAnropsbehorigheter() {
		List<Tjanstekomponent> list = new ArrayList<Tjanstekomponent>(lpv.getPvc().tjanstekomponent.values());
		return list;
	}
	
	public int size() {
		return lpv.getPvc().tjanstekomponent.values().size();
	}
}

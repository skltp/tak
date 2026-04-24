/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.core.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.skltp.tak.core.entity.Vagval;
import se.skltp.tak.core.memdb.LatestPublishedVersion;

@Service()
public class LogiskAdressDao {
	@Autowired
	private LatestPublishedVersion lpv;

	private Collection<Vagval> getCache() {
		return lpv.getPvc().vagval.values();
	}
	
	public List<Vagval> getAllVagVal() {
		List<Vagval> list = new ArrayList<Vagval>(getCache());
		return list;
	}

	public int size() {
		return getCache().size();
	}

	public List<Vagval> getByTjanstekontrakt(String namnrymd) {
		List<Vagval> list = new ArrayList<Vagval>(getCache());
		// Remove entries where namnrymd doesn't match
		Iterator<Vagval> iter = list.iterator();
		while(iter.hasNext()) {
			Vagval v = iter.next();
			if (!v.getTjanstekontrakt().getNamnrymd().equals(namnrymd)) {
				iter.remove();
			}
		}
		return list;
	}

}

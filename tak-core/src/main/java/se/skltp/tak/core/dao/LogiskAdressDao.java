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

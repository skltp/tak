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
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.memdb.LatestPublishedVersion;

@Service()
public class AnropsbehorighetDao {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private LatestPublishedVersion lpv;

	public List<Anropsbehorighet> getAllAnropsbehorighet() {
		List<Anropsbehorighet> list = new ArrayList<Anropsbehorighet>(lpv.getPvc().anropsbehorighet.values());
		return list;
	}

	public List<Anropsbehorighet> getAllAnropsbehorighetAndFilter() {
		return getAllAnropsbehorighet();
	}

	public List<Anropsbehorighet> getAnropsbehorighetByTjanstekontrakt(String namnrymd) {
		List<Anropsbehorighet> list = new ArrayList<Anropsbehorighet>(lpv.getPvc().anropsbehorighet.values());
		// Remove entries where namnrymd doesn't match
		Iterator<Anropsbehorighet> iter = list.iterator();
		while(iter.hasNext()) {
			Anropsbehorighet v = iter.next();
			if (!v.getTjanstekontrakt().getNamnrymd().equals(namnrymd)) {
				iter.remove();
			}
		}
		return list;
	}

	public List<Anropsbehorighet> getAnropsbehorighetAndFilterByTjanstekontrakt(String namnrymd) {
		return getAnropsbehorighetByTjanstekontrakt(namnrymd);
	}
}

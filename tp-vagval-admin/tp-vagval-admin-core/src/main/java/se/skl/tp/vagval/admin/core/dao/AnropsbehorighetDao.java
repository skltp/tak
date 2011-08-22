/**
 * Copyright 2009 Sjukvardsradgivningen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public

 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the

 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,

 *   Boston, MA 02111-1307  USA
 */
package se.skl.tp.vagval.admin.core.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import se.skl.tp.vagval.admin.core.entity.Anropsbehorighet;

@Service()
public class AnropsbehorighetDao {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<Anropsbehorighet> getAllAnropsbehorighet() {
		List<Anropsbehorighet> list = em.createQuery("Select a from Anropsbehorighet a").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Anropsbehorighet> getAnropsbehorighetByTjanstekontrakt(String namnrymd) {
		Query query = em.createQuery("Select a from Anropsbehorighet a "
				+ "where a.tjanstekontrakt.namnrymd = :namnrymd");
		query.setParameter("namnrymd", namnrymd);
		return query.getResultList();
	}

}

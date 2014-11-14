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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

import se.skltp.tak.core.entity.Vagval;

@Service()
public class LogiskAdressDao {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<Vagval> getAllVagVal() {
		List<Vagval> list = em.createQuery("Select v from Vagval v ").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Vagval> getByTjanstekontrakt(String namnrymd) {
		Query query = em.createQuery("Select v from Vagval v "
				+ "where v.tjanstekontrakt.namnrymd = :namnrymd");
		query.setParameter("namnrymd", namnrymd);
		return query.getResultList();
	}

}

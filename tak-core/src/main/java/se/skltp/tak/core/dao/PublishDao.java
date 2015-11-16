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

import org.springframework.stereotype.Service;

import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.core.entity.Filtercategorization;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.core.entity.Vagval;

@Service()
public class PublishDao {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public List<AnropsAdress> getAnropsadressForPublish() {
		List<AnropsAdress> list = em.createQuery("Select aa from AnropsAdress aa where updatedTime != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Anropsbehorighet> getAnropsbehorighetForPublish() {
		List<Anropsbehorighet> list = em.createQuery("Select ab from Anropsbehorighet ab where updatedTime != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Filter> getFilterForPublish() {
		List<Filter> list = em.createQuery("Select f from Filter f where updatedTime != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Filtercategorization> getFiltercategorizationForPublish() {
		List<Filtercategorization> list = em.createQuery("Select fc from Filtercategorization fc where updatedTime != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<LogiskAdress> getLogiskAdressForPublish() {
		List<LogiskAdress> list = em.createQuery("Select la from LogiskAdress la where updatedTime != null").getResultList();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<RivTaProfil> getRivTaProfilForPublish() {
		List<RivTaProfil> list = em.createQuery("Select rtp from RivTaProfil rtp where updatedTime != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Tjanstekomponent> getTjanstekomponentForPublish() {
		List<Tjanstekomponent> list = em.createQuery("Select tk from Tjanstekomponent tk where updatedTime != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Tjanstekontrakt> getTjanstekontraktForPublish() {
		List<Tjanstekontrakt> list = em.createQuery("Select tk from Tjanstekontrakt tk where updatedTime != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Vagval> getVagvalForPublish() {
		List<Vagval> list = em.createQuery("Select vv from Vagval vv where updatedTime != null").getResultList();
		return list;
	}
}

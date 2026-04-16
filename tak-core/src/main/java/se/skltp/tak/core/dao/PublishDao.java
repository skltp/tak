package se.skltp.tak.core.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
	public List<AnropsAdress> getAnropsadress() {
		List<AnropsAdress> list = em.createQuery("Select aa from AnropsAdress aa where deleted = FALSE and pubVersion != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Anropsbehorighet> getAnropsbehorighet() {
		List<Anropsbehorighet> list = em.createQuery("Select ab from Anropsbehorighet ab where deleted = FALSE and pubVersion != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Filter> getFilter() {
		List<Filter> list = em.createQuery("Select f from Filter f where deleted = FALSE and pubVersion != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Filtercategorization> getFiltercategorization() {
		List<Filtercategorization> list = em.createQuery("Select fc from Filtercategorization fc where deleted = FALSE and pubVersion != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<LogiskAdress> getLogiskAdress() {
		List<LogiskAdress> list = em.createQuery("Select la from LogiskAdress la where deleted = FALSE and pubVersion != null").getResultList();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<RivTaProfil> getRivTaProfil() {
		List<RivTaProfil> list = em.createQuery("Select rtp from RivTaProfil rtp where deleted = FALSE and pubVersion != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Tjanstekomponent> getTjanstekomponent() {
		List<Tjanstekomponent> list = em.createQuery("Select tk from Tjanstekomponent tk where deleted = FALSE and pubVersion != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Tjanstekontrakt> getTjanstekontrakt() {
		List<Tjanstekontrakt> list = em.createQuery("Select tk from Tjanstekontrakt tk where deleted = FALSE and pubVersion != null").getResultList();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<Vagval> getVagval() {
		List<Vagval> list = em.createQuery("Select vv from Vagval vv where deleted = FALSE and pubVersion != null").getResultList();
		return list;
	}
}

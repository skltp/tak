package se.skltp.tak.core.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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

	public int size() {
		return lpv.getPvc().anropsbehorighet.values().size();
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

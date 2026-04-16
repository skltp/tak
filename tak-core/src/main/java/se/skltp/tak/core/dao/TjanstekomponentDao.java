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

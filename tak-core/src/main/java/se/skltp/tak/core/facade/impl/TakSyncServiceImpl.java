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
package se.skltp.tak.core.facade.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.skltp.tak.core.dao.AnropsbehorighetDao;
import se.skltp.tak.core.dao.LogiskAdressDao;
import se.skltp.tak.core.dao.TjanstekomponentDao;
import se.skltp.tak.core.dao.TjanstekontraktDao;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.core.facade.*;

@Service("takSyncService")
public class TakSyncServiceImpl implements TakSyncService {

    @Autowired
    TjanstekontraktDao tjanstekontraktDao;

    @Autowired
    LogiskAdressDao logiskAdressDao;

	@Autowired
	AnropsbehorighetDao anropsbehorighetDao;

	@Autowired
	TjanstekomponentDao tjanstekomponentDao;

	@Override
	// need to keep transaction (JPA session) open since we are lazy-loading parts
	// of an object-graph here
	@Transactional(readOnly = true) 
	public List<TjanstekomponentInfo> getAllTjanstekomponent() {
		List<Tjanstekomponent> tks = tjanstekomponentDao.getAllTjanstekomponentAndAnropsAdresserAndAnropsbehorigheter();
		
		List<TjanstekomponentInfo> tkis = new ArrayList<TjanstekomponentInfo>();
		for (Tjanstekomponent tk : tks) {
			TjanstekomponentInfo tki = new TjanstekomponentInfo();
			tki.setHsaId(tk.getHsaId());
			tki.setBeskrivning(tk.getBeskrivning());
			tkis.add(tki);
			
			// service producers
			for (AnropsAdress aa : tk.getAnropsAdresser()) {
				AnropsAdressInfo aai = new AnropsAdressInfo();				
				aai.setAdress(aa.getAdress());								
				aai.setRivtaProfilNamn(aa.getRivTaProfil().getNamn());
				tki.getAnropsAdressInfos().add(aai);
				
				for (Vagval vv : aa.getVagVal()) {
					VagvalsInfo vvi = new VagvalsInfo();
					vvi.setFromTidpunkt(vv.getFromTidpunkt());
					vvi.setTomTidpunkt(vv.getTomTidpunkt());
					vvi.setLogiskAdressBeskrivning(vv.getLogiskAdress().getBeskrivning());
					vvi.setLogiskAdressHsaId(vv.getLogiskAdress().getHsaId());
					vvi.setTjanstekontraktNamnrymd(vv.getTjanstekontrakt().getNamnrymd());
					aai.getVagvalsInfos().add(vvi);
				}
			}
			
			// service consumers
			for (Anropsbehorighet ab : tk.getAnropsbehorigheter()) {
				AnropsbehorighetInfo abi = new AnropsbehorighetInfo();
				abi.setFromTidpunkt(ab.getFromTidpunkt());
				abi.setTomTidpunkt(ab.getTomTidpunkt());
				abi.setIntegrationsavtal(ab.getIntegrationsavtal());
				abi.setTjanstekontraktNamnrymd(ab.getTjanstekontrakt().getNamnrymd());
				abi.setLogiskAdressHsaId(ab.getLogiskAdress().getHsaId());
				abi.setLogiskAdressBeskrivning(ab.getLogiskAdress().getBeskrivning());
				tki.getAnropsbehorighetInfos().add(abi);
			}
		}
		
		return tkis;
	}

    @Override
    public List<TjanstekontraktInfo> getAllTjanstekontrakt() {
        List<Tjanstekontrakt> list = tjanstekontraktDao.getAllTjanstekontrakt();

        List<TjanstekontraktInfo> infos = new ArrayList<TjanstekontraktInfo>();
        for (Tjanstekontrakt tk : list) {
            TjanstekontraktInfo i = new TjanstekontraktInfo();
            i.setMajorVersion(String.valueOf(tk.getMajorVersion()));
            i.setMinorVersion(String.valueOf(tk.getMinorVersion()));
            i.setNamnrymd(tk.getNamnrymd());
            i.setBeskrivning(tk.getBeskrivning());
            infos.add(i);
        }

        return infos;
    }

	public List<AnropsbehorighetInfo> getAllAnropsbehorighet() {

		List<Anropsbehorighet> list = anropsbehorighetDao.getAllAnropsbehorighet();

		List<AnropsbehorighetInfo> infos = transformToAnropsbehorighetInfoList(list);

		return infos;
	}

	public List<AnropsbehorighetInfo> getAllAnropsbehorighetAndFilter() {

		List<Anropsbehorighet> list = anropsbehorighetDao.getAllAnropsbehorighetAndFilter();

		List<AnropsbehorighetInfo> infos = transformToAnropsbehorighetInfoListWithFilterInfoList(list);

		return infos;
	}
	
	public List<AnropsbehorighetInfo> getAnropsbehorighetByTjanstekontrakt(String namnrymd) {
		List<Anropsbehorighet> list = anropsbehorighetDao
				.getAnropsbehorighetByTjanstekontrakt(namnrymd);

		List<AnropsbehorighetInfo> infos = transformToAnropsbehorighetInfoList(list);

		return infos;
	}
	
	@Override
	public List<AnropsbehorighetInfo> getAnropsbehorighetAndFilterByTjanstekontrakt(
			String namnrymd) {
		List<Anropsbehorighet> list = anropsbehorighetDao
				.getAnropsbehorighetAndFilterByTjanstekontrakt(namnrymd);

		List<AnropsbehorighetInfo> infos = transformToAnropsbehorighetInfoListWithFilterInfoList(list);

		return infos;
	}

	private List<AnropsbehorighetInfo> transformToAnropsbehorighetInfoList(
			List<Anropsbehorighet> list) {
		List<AnropsbehorighetInfo> infos = new ArrayList<AnropsbehorighetInfo>();
		for (Anropsbehorighet ab : list) {
			AnropsbehorighetInfo info = transformToAnropsbehorighetInfo(ab);
			infos.add(info);
		}
		return infos;
	}
	
	private List<AnropsbehorighetInfo> transformToAnropsbehorighetInfoListWithFilterInfoList(
			List<Anropsbehorighet> list) {
		List<AnropsbehorighetInfo> infos = new ArrayList<AnropsbehorighetInfo>();
		for (Anropsbehorighet ab : list) {
			AnropsbehorighetInfo info = transformToAnropsbehorighetInfo(ab);
			info.setFilterInfos(transformToFilterInfosList(ab.getFilter()));
			infos.add(info);
		}
		return infos;
	}
	
	private AnropsbehorighetInfo transformToAnropsbehorighetInfo(Anropsbehorighet behorighet) {
		AnropsbehorighetInfo info = new AnropsbehorighetInfo();
		info.setFromTidpunkt(behorighet.getFromTidpunkt());
		info.setIdAnropsbehorighet(behorighet.getId());
		info.setTomTidpunkt(behorighet.getTomTidpunkt());
		info.setTjanstekontraktNamnrymd(behorighet.getTjanstekontrakt().getNamnrymd());
		info.setLogiskAdressHsaId(behorighet.getLogiskAdress().getHsaId());
		info.setHsaIdTjanstekomponent(behorighet.getTjanstekonsument().getHsaId());
		
		return info;
	}
	private List<FilterInfo> transformToFilterInfosList(List<Filter> list) {
		List<FilterInfo> infos = new ArrayList<FilterInfo>();
		for (Filter filter : list) {
			FilterInfo info = new FilterInfo();
			infos.add(info);
			info.setServicedomain(filter.getServicedomain());
			
			List<String> categories = new ArrayList<String>();
			for(Filtercategorization category : filter.getCategorization()) {
				categories.add(category.getCategory());
			}
			if(!categories.isEmpty()) {
				info.setFilterCategorizations(categories);
			}
		}
		return infos;
	}

    public List<VirtualiseringInfo> getAllVagval() {

		List<Vagval> list = logiskAdressDao.getAllVagVal();

		List<VirtualiseringInfo> infos = transformToVirtualiseringInfoList(list);
		return infos;
	}

	public List<VirtualiseringInfo> getVagvalByTjanstekontrakt(
			String namnrymd) {
		List<Vagval> list = logiskAdressDao.getByTjanstekontrakt(namnrymd);

		List<VirtualiseringInfo> infos = transformToVirtualiseringInfoList(list);

		return infos;
	}

	private List<VirtualiseringInfo> transformToVirtualiseringInfoList(List<Vagval> list) {

		List<VirtualiseringInfo> infos = new ArrayList<VirtualiseringInfo>();
		for (Vagval vt : list) {
			VirtualiseringInfo info = new VirtualiseringInfo();
			infos.add(info);
			info.setAdress(vt.getAnropsAdress().getAdress());
			info.setFromTidpunkt(vt.getFromTidpunkt());
			info.setIdLogiskAdress(vt.getId());
			info.setTomTidpunkt(vt.getTomTidpunkt());
			info.setNamnRiv(vt.getAnropsAdress().getRivTaProfil().getNamn());
			info.setNamnrymd(vt.getTjanstekontrakt().getNamnrymd());
			info.setHsaIdLogiskAddresat(vt.getLogiskAdress().getHsaId());
			info.setHsaIdTjanstekomponent(vt.getAnropsAdress().getTjanstekomponent().getHsaId());
		}
		return infos;
	}

	
	public Set<String> getAllSupportedNamespacesByLogicalAddress(
			String logicalAddress, String consumerHsaId) {
		final List<AnropsbehorighetInfo> perms = this.getAllAnropsbehorighet();
		final Set<String> namespaces = new HashSet<String>();
		
		for (final AnropsbehorighetInfo ai : perms) {
			if(consumerHsaId != null) {
				if (ai.getLogiskAdressHsaId().equalsIgnoreCase(logicalAddress) && ai.getHsaIdTjanstekomponent().equalsIgnoreCase(consumerHsaId)) {
					namespaces.add(ai.getTjanstekontraktNamnrymd());
				}
			} else {
				if (ai.getLogiskAdressHsaId().equalsIgnoreCase(logicalAddress)) {
					namespaces.add(ai.getTjanstekontraktNamnrymd());
				}
			}
		}
		
		return namespaces;
	}

	public Set<String> getLogicalAddresseesByServiceContract(
			String serviceContractNamespace,  String consumerHsaId) {
		final List<AnropsbehorighetInfo> perms = this.getAllAnropsbehorighet();
		final Set<String> logicalAdressees = new HashSet<String>();
		
		for (final AnropsbehorighetInfo ai : perms) {
			if (ai.getTjanstekontraktNamnrymd().equalsIgnoreCase(serviceContractNamespace) && ai.getHsaIdTjanstekomponent().equalsIgnoreCase(consumerHsaId)) {
				logicalAdressees.add(ai.getLogiskAdressHsaId());
			}
		}
		
		return logicalAdressees;
	}

	@Override
	public List<AnropsbehorighetInfo> getLogicalAddresseesAndFiltersByServiceContract(
			String serviceContractNamespace, String consumerHsaId) {
		final List<AnropsbehorighetInfo> perms = this.getAnropsbehorighetAndFilterByTjanstekontrakt(serviceContractNamespace);
		List<AnropsbehorighetInfo> logicalAddressesAndFilters = new ArrayList<AnropsbehorighetInfo>();
		
		for (final AnropsbehorighetInfo ai : perms) {
			if (ai.getHsaIdTjanstekomponent().equalsIgnoreCase(consumerHsaId)) {
				logicalAddressesAndFilters.add(ai);
			}
		}
		
		return logicalAddressesAndFilters;
	}
}

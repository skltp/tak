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
package se.skl.tp.vagval.admin.core.facade.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.skl.tp.vagval.admin.core.dao.AnropsbehorighetDao;
import se.skl.tp.vagval.admin.core.dao.LogiskAdressDao;
import se.skl.tp.vagval.admin.core.entity.Anropsbehorighet;
import se.skl.tp.vagval.admin.core.entity.Filter;
import se.skl.tp.vagval.admin.core.entity.LogiskAdress;
import se.skl.tp.vagval.admin.core.facade.AnropsbehorighetInfo;
import se.skl.tp.vagval.admin.core.facade.FilterInfo;
import se.skl.tp.vagval.admin.core.facade.VagvalSyncService;
import se.skl.tp.vagval.admin.core.facade.VirtualiseringInfo;

@Service("vagvalSyncService")
public class VagvalSyncServiceImpl implements VagvalSyncService {

	@Autowired
	LogiskAdressDao logiskAdressDao;

	@Autowired
	AnropsbehorighetDao anropsbehorighetDao;

	public List<AnropsbehorighetInfo> getAllAnropsbehorighet() {

		List<Anropsbehorighet> list = anropsbehorighetDao.getAllAnropsbehorighet();

		List<AnropsbehorighetInfo> infos = transformToAnropsbehorighetInfoList(list);

		return infos;
	}

	public List<AnropsbehorighetInfo> getAnropsbehorighetByTjanstekontrakt(String namnrymd) {
		List<Anropsbehorighet> list = anropsbehorighetDao
				.getAnropsbehorighetByTjanstekontrakt(namnrymd);

		List<AnropsbehorighetInfo> infos = transformToAnropsbehorighetInfoList(list);

		return infos;
	}

	private List<AnropsbehorighetInfo> transformToAnropsbehorighetInfoList(
			List<Anropsbehorighet> list) {
		List<AnropsbehorighetInfo> infos = new ArrayList<AnropsbehorighetInfo>();
		for (Anropsbehorighet ab : list) {
			AnropsbehorighetInfo info = new AnropsbehorighetInfo();
			infos.add(info);
			info.setFromTidpunkt(ab.getFromTidpunkt());
			info.setIdAnropsbehorighet(ab.getId());
			info.setTomTidpunkt(ab.getTomTidpunkt());
			info.setNamnrymd(ab.getTjanstekontrakt().getNamnrymd());
			info.setHsaIdLogiskAddresat(ab.getLogiskAddresat().getHsaId());
			info.setHsaIdTjanstekomponent(ab.getTjanstekonsument().getHsaId());
			info.setFilters(transformToFilterInfosList(ab.getFilter()));
		}
		return infos;
	}
	
	private List<FilterInfo> transformToFilterInfosList(List<Filter> list) {
		List<FilterInfo> infos = new ArrayList<FilterInfo>();
		for (Filter filter : list) {
			FilterInfo info = new FilterInfo();
			infos.add(info);
		}
		return infos;
	}

	public List<VirtualiseringInfo> getAllVirtualisering() {

		List<LogiskAdress> list = logiskAdressDao.getAllLogiskAdress();

		List<VirtualiseringInfo> infos = transformToVirtualiseringInfoList(list);
		return infos;
	}

	public List<VirtualiseringInfo> getVirtualiseringByTjanstekontrakt(
			String namnrymd) {
		List<LogiskAdress> list = logiskAdressDao.getByTjanstekontrakt(namnrymd);

		List<VirtualiseringInfo> infos = transformToVirtualiseringInfoList(list);

		return infos;
	}

	private List<VirtualiseringInfo> transformToVirtualiseringInfoList(List<LogiskAdress> list) {

		List<VirtualiseringInfo> infos = new ArrayList<VirtualiseringInfo>();
		for (LogiskAdress vt : list) {
			VirtualiseringInfo info = new VirtualiseringInfo();
			infos.add(info);
			info.setAdress(vt.getTjansteproducent().getAdress());
			info.setFromTidpunkt(vt.getFromTidpunkt());
			info.setIdLogiskAdress(vt.getId());
			info.setTomTidpunkt(vt.getTomTidpunkt());
			info.setNamnRiv(vt.getRivVersion().getNamn());
			info.setNamnrymd(vt.getTjanstekontrakt().getNamnrymd());
			info.setHsaIdLogiskAddresat(vt.getLogiskAddresat().getHsaId());
			info.setHsaIdTjanstekomponent(vt.getTjansteproducent().getHsaId());
		}
		return infos;
	}

	
	public Set<String> getAllSupportedNamespacesByLogicalAddress(
			String logicalAddress, String consumerHsaId) {
		final List<AnropsbehorighetInfo> perms = this.getAllAnropsbehorighet();
		final Set<String> namespaces = new HashSet<String>();
		
		for (final AnropsbehorighetInfo ai : perms) {
			
			if (ai.getHsaIdLogiskAddresat().equalsIgnoreCase(logicalAddress) && ai.getHsaIdTjanstekomponent().equalsIgnoreCase(consumerHsaId)) {
				namespaces.add(ai.getNamnrymd());
			}
		}
		
		return namespaces;
	}

	public Set<String> getLogicalAddresseesByServiceContract(
			String serviceContractNamespace,  String consumerHsaId) {
		final List<AnropsbehorighetInfo> perms = this.getAllAnropsbehorighet();
		final Set<String> logicalAdressees = new HashSet<String>();
		
		for (final AnropsbehorighetInfo ai : perms) {
			
			if (ai.getNamnrymd().equalsIgnoreCase(serviceContractNamespace) && ai.getHsaIdTjanstekomponent().equalsIgnoreCase(consumerHsaId)) {
				logicalAdressees.add(ai.getHsaIdLogiskAddresat());
			}
		}
		
		return logicalAdressees;
	}

	@Override
	public List<AnropsbehorighetInfo> getLogicalAddresseesAndFiltersByServiceContract(
			String serviceContractNamespace, String consumerHsaId) {
		final List<AnropsbehorighetInfo> perms = this.getAllAnropsbehorighet();
		List<AnropsbehorighetInfo> logicalAddressesAndFilters = new ArrayList<AnropsbehorighetInfo>();
		for (final AnropsbehorighetInfo ai : perms) {
			if (ai.getNamnrymd().equalsIgnoreCase(serviceContractNamespace) && ai.getHsaIdTjanstekomponent().equalsIgnoreCase(consumerHsaId)) {
				logicalAddressesAndFilters.add(ai);
			}
		}
		
		return logicalAddressesAndFilters;
	}

	
}

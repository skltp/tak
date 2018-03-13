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
package se.skltp.tak.core.facade;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface TakSyncService {

	List<TjanstekomponentInfo> getAllTjanstekomponent();
    
	int getAllTjanstekomponentSize();
    
	List<TjanstekontraktInfo> getAllTjanstekontrakt();

	int getAllTjanstekontraktSize();

    List<VirtualiseringInfo> getAllVagval();

	List<AnropsbehorighetInfo> getAllAnropsbehorighet();
	
	int getAllAnropsbehorighetAndFilterSize();

	List<AnropsbehorighetInfo> getAllAnropsbehorighetAndFilter();

	List<VirtualiseringInfo> getVagvalByTjanstekontrakt(String namnrymd);
	
	List<AnropsbehorighetInfo> getAnropsbehorighetByTjanstekontrakt(String namnrymd);
	
	List<AnropsbehorighetInfo> getAnropsbehorighetAndFilterByTjanstekontrakt(String namnrymd);
		
	Set<String> getAllSupportedNamespacesByLogicalAddress(final String logicalAddress, final String consumerHsaId);

	Set<String> getAllSupportedNamespacesByLogicalAddressAndDate(final String logicalAddress, final String consumerHsaId, final Date date);

	Set<String> getLogicalAddresseesByServiceContract(final String serviceContractNamespace, final String consumerHsaId);
	
	List<AnropsbehorighetInfo> getLogicalAddresseesAndFiltersByServiceContract(final String serviceContractNamespace, final String consumerHsaId);

	int getAllVagvalSize();
//	List<PubVersion> getAllPubVersions();

}

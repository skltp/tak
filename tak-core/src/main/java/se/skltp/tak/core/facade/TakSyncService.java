/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
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

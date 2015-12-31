<%--

    Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
    					<http://cehis.se/>

    This file is part of SKLTP.

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

--%>
<div id="publish-deleted-list" class="content scaffold-list" role="main">
	<h1>
		<g:message code="pubVersion.deleted.list" />
	</h1>
	<table>
		<thead>
			<tr>
				<th width="2%" />
				<th width="15%" />
				<th width="80%"/>
				<th width="3%"/>
			</tr>
		</thead>
		<tbody>
			<g:each in="${rivTaProfilList}" status="i" var="rivTaProfilInstance">
				<g:if test="${rivTaProfilInstance.isDeleted()}">
					<tr class="even">
						<td><g:checkBox value="${rivTaProfilInstance.getUpdatedBy().equalsIgnoreCase(currentUser)}" disabled="true" /></td>
						<td><g:message code="default.rivTaProfil.label" /></td>
						<td><g:link controller="rivTaProfil" action="show" id="${rivTaProfilInstance.id}">${rivTaProfilInstance.getPublishInfo()}</g:link></td>
						<td>${rivTaProfilInstance.getUpdatedBy()}</td>
					</tr>
				</g:if>
			</g:each>

			<g:each in="${logiskAdressList}" status="i" var="logiskAdressInstance">
				<g:if test="${logiskAdressInstance.isDeleted()}">
					<tr class="odd">
						<td><g:checkBox value="${logiskAdressInstance.getUpdatedBy().equalsIgnoreCase(currentUser)}" disabled="true" /></td>
						<td><g:message code="default.logiskAdress.label" /></td>
						<td><g:link controller="logiskAdress" action="show"	id="${logiskAdressInstance.id}">${logiskAdressInstance.getPublishInfo()}</g:link></td>
						<td>${logiskAdressInstance.getUpdatedBy()}</td>
					</tr>
				</g:if>
			</g:each>
			
			<g:each in="${tjanstekontraktList}" status="i" var="tjanstekontraktInstance">
				<g:if test="${tjanstekontraktInstance.isDeleted()}">
					<tr class="even">
						<td><g:checkBox value="${tjanstekontraktInstance.getUpdatedBy().equalsIgnoreCase(currentUser)}" disabled="true" /></td>
						<td><g:message code="default.tjanstekontrakt.label" /></td>
						<td><g:link controller="tjanstekontrakt" action="show" id="${tjanstekontraktInstance.id}">${tjanstekontraktInstance.getPublishInfo()}</g:link></td>
						<td>${tjanstekontraktInstance.getUpdatedBy()}</td>
					</tr>
				</g:if>
			</g:each>
			
			<g:each in="${tjanstekomponentList}" status="i" var="tjanstekomponentInstance">
				<g:if test="${tjanstekomponentInstance.isDeleted()}">
					<tr class="odd">
						<td><g:checkBox value="${tjanstekomponentInstance.getUpdatedBy().equalsIgnoreCase(currentUser)}" disabled="true" /></td>
						<td><g:message code="default.tjanstekomponent.label" /></td>
						<td><g:link controller="tjanstekomponent" action="show" id="${tjanstekomponentInstance.id}">${tjanstekomponentInstance.getPublishInfo()}</g:link></td>
						<td>${tjanstekomponentInstance.getUpdatedBy()}</td>
					</tr>
				</g:if>
			</g:each>

			<g:each in="${filterList}" status="i" var="filterInstance">
				<g:if test="${filterInstance.isDeleted()}">
					<tr class="even">
						<td><g:checkBox value="${filterInstance.getUpdatedBy().equalsIgnoreCase(currentUser)}" disabled="true" /></td>
						<td><g:message code="default.filter.label" /></td>
						<td><g:link controller="filter" action="show" id="${filterInstance.id}">${filterInstance.getPublishInfo()}</g:link></td>
						<td>${filterInstance.getUpdatedBy()}</td>
					</tr>
				</g:if>
			</g:each>
			
			<g:each in="${filtercategorizationList}" status="i" var="filtercategorizationInstance">
				<g:if test="${filtercategorizationInstance.isDeleted()}">
					<tr class="odd">
						<td><g:checkBox value="${filtercategorizationInstance.getUpdatedBy().equalsIgnoreCase(currentUser)}" disabled="true" /></td>
						<td><g:message code="default.filtercategorization.label" /></td>
						<td><g:link controller="filtercategorization" action="show"	id="${filtercategorizationInstance.id}">${filtercategorizationInstance.getPublishInfo()}</g:link></td>
						<td>${filtercategorizationInstance.getUpdatedBy()}</td>
					</tr>
				</g:if>
			</g:each>
			
			<g:each in="${anropsAdressList}" status="i" var="anropsAdressInstance">
				<g:if test="${anropsAdressInstance.isDeleted()}">
					<tr class="even">
						<td><g:checkBox value="${anropsAdressInstance.getUpdatedBy().equalsIgnoreCase(currentUser)}" disabled="true" /></td>
						<td><g:message code="default.anropsadress.label" /></td>
						<td><g:link controller="anropsAdress" action="show" id="${anropsAdressInstance.id}">${anropsAdressInstance.getPublishInfo()}</g:link></td>
						<td>${anropsAdressInstance.getUpdatedBy()}</td>
					</tr>
				</g:if>
			</g:each>
			
			<g:each in="${anropsbehorighetList}" status="i" var="anropsbehorighetInstance">
				<g:if test="${anropsbehorighetInstance.isDeleted()}">
					<tr class="odd">
						<td><g:checkBox value="${anropsbehorighetInstance.getUpdatedBy().equalsIgnoreCase(currentUser)}" disabled="true" /></td>
						<td><g:message code="default.anropsbehorighet.label" /></td>
						<td><g:link controller="anropsbehorighet" action="show" id="${anropsbehorighetInstance.id}">${anropsbehorighetInstance.getPublishInfo()}</g:link></td>
						<td>${anropsbehorighetInstance.getUpdatedBy()}</td>
					</tr>
				</g:if>
			</g:each>

			<g:each in="${vagvalList}" status="i" var="vagvalInstance">
				<g:if test="${vagvalInstance.isDeleted()}">
					<tr class="even">
						<td><g:checkBox value="${vagvalInstance.getUpdatedBy().equalsIgnoreCase(currentUser)}" disabled="true" /></td>
						<td><g:message code="default.vagval.label" /></td>
						<td><g:link controller="vagval" action="show" id="${vagvalInstance.id}">${vagvalInstance.getPublishInfo()}</g:link></td>
						<td>${vagvalInstance.getUpdatedBy()}</td>
					</tr>
				</g:if>
			</g:each>

		</tbody>
		
	</table>
</div>
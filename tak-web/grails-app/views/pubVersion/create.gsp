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
<%@ page import="se.skltp.tak.core.entity.PubVersion" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'pubVersion.label', default: 'PubVersion')}" />
		<title><g:message code="default.create.label" args="[entityName]" /></title>
		<r:require module="select2"/>
	</head>
	<body>
		<a href="#create-pubVersion" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		
		<div id="publish-header-list" class="content scaffold-list" role="main">
			<table>
				<thead>
					<tr>
						<g:sortableColumn property="namn" title="${message(code: 'default.entity.label')}" width="14%"/>
						<g:sortableColumn property="namn" title="${message(code: 'pubVersion.namn.label', default: 'x_Namn')}" width="36%" />
						<g:sortableColumn property="beskrivning" title="${message(code: 'pubVersion.beskrivning.label', default: 'x_Beskrivning')}" width="34%"/>						
						<g:sortableColumn property="beskrivning" title="${message(code: 'pubVersion.summary.label', default: 'x_Sammanställning')}" />
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
				
		<div id="publish-created-list" class="content scaffold-list" role="main">			
			<table>
				<tbody>
				<g:each in="${rivTaProfilList}" status="i" var="rivTaProfilInstance">
					<g:if test="${rivTaProfilInstance.isNewlyCreated()}">
						<tr class="even">
							<td>
								<g:if test="${i==0}">
									<g:message code="default.rivTaProfil.label" />
								</g:if>
							</td>
							<td><g:link controller="rivTaProfil" action="show" id="${rivTaProfilInstance.id}">${rivTaProfilInstance.namn}</g:link></td>							
							<td>${rivTaProfilInstance.beskrivning}</td>
							<td>
								<g:link controller="anropsAdress" action="list">
									<g:message code="default.anropsadress.label" default="xxx"/> 
									<img src='${resource(dir:'images',file: "${rivTaProfilInstance.AnropsAdresser?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								</g:link>
							</td>							
						</tr>
					</g:if>
				</g:each>
				
				<g:each in="${logiskAdressList}" status="i" var="logiskAdressInstance">
					<g:if test="${logiskAdressInstance.isNewlyCreated()}">
						<tr class="odd">
							<td><g:message code="default.logiskAdress.label" /></td>
							<td><g:link action="show" id="${logiskAdressInstance.id}">${logiskAdressInstance.hsaId}</g:link></td>
							<td>${logiskAdressInstance.beskrivning}</td>
							<td>
								<g:link controller="vagval" action="list">
									<g:message code="default.vagval.label"/> 
									<img src='${resource(dir:'images',file: "${logiskAdressInstance.vagval?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								</g:link>
								<br>
								<g:link controller="anropsbehorighet" action="list">
									<g:message code="default.anropsbehorighet.label"/> 
									<img src='${resource(dir:'images',file: "${logiskAdressInstance.anropsbehorigheter?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								</g:link>
							</td>
						</tr>
					</g:if>
				</g:each>
				<g:each in="${tjanstekontraktList}" status="i" var="tjanstekontraktInstance">
					<g:if test="${tjanstekontraktInstance.isNewlyCreated()}">
						<tr class="even">
							<td><g:message code="default.tjanstekontrakt.label" /></td>
							<td><g:link action="show" id="${tjanstekontraktInstance.id}">${tjanstekontraktInstance.namnrymd}</g:link></td>
							<td>${tjanstekontraktInstance.beskrivning}</td>
							<td>
								<g:link controller="vagval" action="list">
									<g:message code="default.vagval.label"/>
									<img src='${resource(dir:'images',file: "${tjanstekontraktInstance.vagval?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								</g:link>
								<br>
								<g:link controller="anropsbehorighet" action="list">
									<g:message code="default.anropsbehorighet.label"/>
									<img src='${resource(dir:'images',file: "${tjanstekontraktInstance.anropsbehorigheter?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								</g:link>
							</td>
						</tr>
					</g:if>
				</g:each>				
				<g:each in="${tjanstekomponentList}" status="i" var="tjanstekomponentInstance">
					<g:if test="${tjanstekomponentInstance.isNewlyCreated()}">
						<tr class="odd">
							<td><g:message code="default.tjanstekomponent.label" /></td>
							<td><g:link action="show" id="${tjanstekomponentInstance.id}">${tjanstekomponentInstance.hsaId}</g:link></td>
							<td>${tjanstekomponentInstance.beskrivning}</td>
							<td>
								<g:link controller="anropsbehorighet" action="list" >
									<g:message code="default.tjanstekontrakt.label"/>
									<img src='${resource(dir:'images',file: "${tjanstekomponentInstance.anropsbehorigheter?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								</g:link>
								<br>
								<g:link controller="anropsadress" action="list" >
									<g:message code="default.anropsadress.label"/>
									<img src='${resource(dir:'images',file: "${tjanstekomponentInstance.anropsAdresser?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								</g:link>
							</td>
						</tr>
					</g:if>
				</g:each>
				
				<g:each in="${filterList}" status="i" var="filterInstance">
					<g:if test="${filterInstance.isNewlyCreated()}">
						<tr class="even">
							<td><g:message code="default.filter.label" /></td>
							<td><g:link action="show" id="${filterInstance.id}">${filterInstance.servicedomain}</g:link></td>
							<td>-</td>
							<td>
								<g:link controller="filtercategorization" action="list">
									<g:message code="default.filtercategorization.label"/>
									<img src='${resource(dir:'images',file: "${filterInstance.categorization?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								</g:link>
								<br>
								<g:link controller="anropsbehorighet" action="show" id="${filterInstance.anropsbehorighet?.id}">
									<g:message code="default.anropsbehorighet.label"/>
									<img src='${resource(dir:'images',file: "${filterInstance.anropsbehorighet ? 'connected.png' : 'disconnected.png'}")}' />
								</g:link>
							</td>
						</tr>
					</g:if>
				</g:each>
				<g:each in="${filtercategorizationList}" status="i" var="filtercategorizationInstance">
					<g:if test="${filtercategorizationInstance.isNewlyCreated()}">
						<tr class="odd">
							<td><g:message code="default.filtercategorization.label" /></td>						
							<td><g:link action="show" id="${filtercategorizationInstance.id}">${filtercategorizationInstance.category}</g:link></td>
							<td>-</td>
							<td>
								<g:link controller="filter" action="show" id="${filtercategorizationInstance.filter?.id}">
									<g:message code="default.filter.label"/>
									<img src='${resource(dir:'images',file: "${filtercategorizationInstance.filter ? 'connected.png' : 'disconnected.png' }")}' />
								</g:link>
							</td>
						</tr>
					</g:if>
				</g:each>
				<g:each in="${anropsAdressList}" status="i" var="anropsAdressInstance">
					<g:if test="${anropsAdressInstance.isNewlyCreated()}">
						<tr class="even">						
							<td><g:message code="default.anropsadress.label" /></td>
							<td><g:link action="show" id="${anropsAdressInstance.id}">${anropsAdressInstance.adress}</g:link></td>
							<td>-</td>
							<td>
								<g:message code="default.vagval.label"/>
								<img src='${resource(dir:'images',file: "${anropsAdressInstance.vagVal?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								<br>
									<g:message code="default.tjanstekomponent.label"/>
									<img src='${resource(dir:'images',file: "${anropsAdressInstance.tjanstekomponent ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
									<g:message code="default.rivTaProfil.label"/>
									<img src='${resource(dir:'images',file: "${anropsAdressInstance.rivTaProfil ? 'connected.png' : 'disconnected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				<g:each in="${anropsbehorighetList}" status="i" var="anropsbehorighetInstance">
					<g:if test="${anropsbehorighetInstance.isNewlyCreated()}">
						<tr class="odd">
						<td><g:message code="default.anropsbehorighet.label" /></td>
							<td><g:link action="show" id="${anropsbehorighetInstance.id}">${anropsbehorighetInstance.integrationsavtal}</g:link></td>
							<td>-</td>
							<td>
								<g:message code="default.tjanstekomponent.label"/>
								<img src='${resource(dir:'images',file: "${anropsbehorighetInstance.tjanstekonsument ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
									<g:message code="default.tjanstekontrakt.label"/>
									<img src='${resource(dir:'images',file: "${anropsbehorighetInstance.tjanstekontrakt ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
									<g:message code="default.logiskAdress.label"/>
									<img src='${resource(dir:'images',file: "${anropsbehorighetInstance.logiskAdress ? 'connected.png' : 'disconnected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				
				<g:each in="${vagvalList}" status="i" var="vagvalInstance">
					<g:if test="${vagvalInstance.isNewlyCreated()}">
						<tr class="even">
							<td><g:message code="default.vagval.label" /></td>
							<td><g:link action="show" id="${vagvalInstance.id}">${vagvalInstance.fromTidpunkt}-${vagvalInstance.tomTidpunkt}</g:link></td>
							<td>-</td>
							<td>
								<g:message code="default.tjanstekontrakt.label"/>
								<img src='${resource(dir:'images',file: "${vagvalInstance.tjanstekontrakt ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
									<g:message code="default.logiskAdress.label"/>
									<img src='${resource(dir:'images',file: "${vagvalInstance.logiskAdress ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
								<g:message code="default.anropsadress.label"/>
								<img src='${resource(dir:'images',file: "${vagvalInstance.anropsAdress ? 'connected.png' : 'disconnected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				
				</tbody>
			</table>
		</div>
		
		<div id="publish-updated-list" class="content scaffold-list" role="main">
			<h1><g:message code="pubVersion.updated.list" /></h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<tbody>
				<g:each in="${rivTaProfilList}" status="i" var="rivTaProfilInstance">
					<g:if test="${rivTaProfilInstance.isUpdated()}">
						<tr class="even">
							<td><g:message code="default.rivTaProfil.label" default="xxx"/></td>
							<td><g:link controller="rivTaProfil" action="show" id="${rivTaProfilInstance.id}">${rivTaProfilInstance.namn}</g:link></td>							
							<td>${rivTaProfilInstance.beskrivning}</td>
							<td>
								<g:message code="default.anropsadress.label" default="xxx"/> <img src='${resource(dir:'images',file: "${rivTaProfilInstance.AnropsAdresser?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
							</td>							
						</tr>
					</g:if>
				</g:each>
				
				<g:each in="${logiskAdressList}" status="i" var="logiskAdressInstance">
					<g:if test="${logiskAdressInstance.isUpdated()}">
						<tr class="odd">
							<td><g:message code="default.logiskAdress.label" default="xxx"/></td>
							<td><g:link action="show" id="${logiskAdressInstance.id}">${logiskAdressInstance.hsaId}</g:link></td>
							<td>${logiskAdressInstance.beskrivning}</td>
							<td>
								<g:message code="default.tjanstekontrakt.label"/> <img src='${resource(dir:'images',file: "${logiskAdressInstance.vagval.tjanstekontrakt?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								<br>
								<g:message code="default.anropsadress.label"/> <img src='${resource(dir:'images',file: "${logiskAdressInstance.vagval.anropsAdress?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				<g:each in="${tjanstekontraktList}" status="i" var="tjanstekontraktInstance">
					<g:if test="${tjanstekontraktInstance.isUpdated()}">
						<tr class="even">
							<td><g:message code="default.tjanstekontrakt.label" default="xxx"/></td>
							<td><g:link action="show" id="${tjanstekontraktInstance.id}">${tjanstekontraktInstance.namnrymd}</g:link></td>
							<td>${tjanstekontraktInstance.beskrivning}</td>
							<td>
								<g:message code="default.vagval.label"/>
								<img src='${resource(dir:'images',file: "${tjanstekontraktInstance.vagval?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								<br>
								<g:message code="default.anropsbehorighet.label"/>
								<img src='${resource(dir:'images',file: "${tjanstekontraktInstance.anropsbehorigheter?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>				
				<g:each in="${tjanstekomponentList}" status="i" var="tjanstekomponentInstance">
					<g:if test="${tjanstekomponentInstance.isUpdated()}">
						<tr class="odd">
							<td><g:message code="default.tjanstekomponent.label" default="xxx"/></td>
							<td><g:link action="show" id="${tjanstekomponentInstance.id}">${tjanstekomponentInstance.hsaId}</g:link></td>
							<td>${tjanstekomponentInstance.beskrivning}</td>
							<td>
								<g:message code="default.tjanstekontrakt.label"/>
								<img src='${resource(dir:'images',file: "${tjanstekomponentInstance.anropsbehorigheter?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								<br>
									<g:message code="default.anropsadress.label"/>
									<img src='${resource(dir:'images',file: "${tjanstekomponentInstance.anropsAdresser?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				
				<g:each in="${filterList}" status="i" var="filterInstance">
					<g:if test="${filterInstance.isUpdated()}">
						<tr class="even">
							<td><g:message code="default.filter.label" default="xxx"/></td>
							<td><g:link action="show" id="${filterInstance.id}">${filterInstance.servicedomain}</g:link></td>
							<td>${filterInstance.beskrivning}</td>
							<td>
								<g:message code="default.filtercategorization.label"/>
								<img src='${resource(dir:'images',file: "${filterInstance.categorization?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								<br>
									<g:message code="default.anropsbehorighet.label"/>
									<img src='${resource(dir:'images',file: "${filterInstance.anropsbehorighet ? 'connected.png' : 'disconnected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				<g:each in="${filtercategorizationList}" status="i" var="filtercategorizationInstance">
					<g:if test="${filtercategorizationInstance.isUpdated()}">
						<tr class="odd">
							<td><g:message code="default.filtercategorization.label" default="xxx"/></td>						
							<td><g:link action="show" id="${filtercategorizationInstance.id}">${filtercategorizationInstance.category}</g:link></td>
							<td>${filtercategorizationInstance.beskrivning}</td>
							<td>
								<g:message code="default.filter.label"/>
								<img src='${resource(dir:'images',file: "${filtercategorizationInstance.filter ? 'connected.png' : 'disconnected.png' }")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				<g:each in="${anropsAdressList}" status="i" var="anropsAdressInstance">
					<g:if test="${anropsAdressInstance.isUpdated()}">
						<tr class="even">						
							<td><g:message code="default.anropsadress.label" default="xxx"/></td>
							<td><g:link action="show" id="${anropsAdressInstance.id}">${anropsAdressInstance.adress}</g:link></td>
							<td>-</td>
							<td>
								<g:message code="default.vagval.label"/>
								<img src='${resource(dir:'images',file: "${anropsAdressInstance.vagVal?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								<br>
									<g:message code="default.tjanstekomponent.label"/>
									<img src='${resource(dir:'images',file: "${anropsAdressInstance.tjanstekomponent ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
									<g:message code="default.rivTaProfil.label"/>
									<img src='${resource(dir:'images',file: "${anropsAdressInstance.rivTaProfil ? 'connected.png' : 'disconnected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				<g:each in="${anropsbehorighetList}" status="i" var="anropsbehorighetInstance">
					<g:if test="${anropsbehorighetInstance.isUpdated()}">
						<tr class="odd">
						<td><g:message code="default.anropsbehorighet.label" default="xxx"/></td>
							<td><g:link action="show" id="${anropsbehorighetInstance.id}">${anropsbehorighetInstance.integrationsavtal}</g:link></td>
							<td>-</td>
							<td>
								<g:message code="default.tjanstekomponent.label"/>
								<img src='${resource(dir:'images',file: "${anropsbehorighetInstance.tjanstekonsument ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
									<g:message code="default.tjanstekontrakt.label"/>
									<img src='${resource(dir:'images',file: "${anropsbehorighetInstance.tjanstekontrakt ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
									<g:message code="default.logiskAdress.label"/>
									<img src='${resource(dir:'images',file: "${anropsbehorighetInstance.logiskAdress ? 'connected.png' : 'disconnected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				
				<g:each in="${vagvalList}" status="i" var="vagvalInstance">
					<g:if test="${vagvalInstance.isUpdated()}">
						<tr class="even">
							<td><g:message code="default.vagVal.label" default="xxx"/></td>
							<td><g:link action="show" id="${vagvalInstance.id}">${vagvalInstance.fromTidpunkt}-${vagvalInstance.tomTidpunkt}</g:link></td>
							<td>-</td>
							<td>
								<g:message code="default.tjanstekontrakt.label"/>
								<img src='${resource(dir:'images',file: "${vagvalInstance.tjanstekontrakt ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
									<g:message code="default.logiskAdress.label"/>
									<img src='${resource(dir:'images',file: "${vagvalInstance.logiskAdress ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
								<g:message code="default.anropsadress.label"/>
								<img src='${resource(dir:'images',file: "${vagvalInstance.anropsAdress ? 'connected.png' : 'disconnected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>				
				</tbody>
			</table>					
		</div>
		
		<div id="publish-deleted-list" class="content scaffold-list" role="main">
			<h1><g:message code="pubVersion.deleted.list" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<tbody>
				<g:each in="${rivTaProfilList}" status="i" var="rivTaProfilInstance">
					<g:if test="${rivTaProfilInstance.isDeleted()}">
						<tr class="even">
							<td><g:message code="default.rivTaProfil.label" default="xxx"/></td>
							<td><g:link controller="rivTaProfil" action="show" id="${rivTaProfilInstance.id}">${rivTaProfilInstance.namn}</g:link></td>							
							<td>${rivTaProfilInstance.beskrivning}</td>
							<td>
								<g:message code="default.anropsadress.label" default="xxx"/> <img src='${resource(dir:'images',file: "${rivTaProfilInstance.AnropsAdresser?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
							</td>							
						</tr>
					</g:if>
				</g:each>
				
				<g:each in="${logiskAdressList}" status="i" var="logiskAdressInstance">
					<g:if test="${logiskAdressInstance.isDeleted()}">
						<tr class="odd">
							<td><g:message code="default.logiskAdress.label" default="xxx"/></td>
							<td><g:link action="show" id="${logiskAdressInstance.id}">${logiskAdressInstance.hsaId}</g:link></td>
							<td>${logiskAdressInstance.beskrivning}</td>
							<td>
								<g:message code="default.tjanstekontrakt.label"/> <img src='${resource(dir:'images',file: "${logiskAdressInstance.vagval.tjanstekontrakt?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								<br>
								<g:message code="default.anropsadress.label"/> <img src='${resource(dir:'images',file: "${logiskAdressInstance.vagval.anropsAdress?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				<g:each in="${tjanstekontraktList}" status="i" var="tjanstekontraktInstance">
					<g:if test="${tjanstekontraktInstance.isDeleted()}">
						<tr class="even">
							<td><g:message code="default.tjanstekontrakt.label" default="xxx"/></td>
							<td><g:link action="show" id="${tjanstekontraktInstance.id}">${tjanstekontraktInstance.namnrymd}</g:link></td>
							<td>${tjanstekontraktInstance.beskrivning}</td>
							<td>
								<g:message code="default.vagval.label"/>
								<img src='${resource(dir:'images',file: "${tjanstekontraktInstance.vagval?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								<br>
								<g:message code="default.anropsbehorighet.label"/>
								<img src='${resource(dir:'images',file: "${tjanstekontraktInstance.anropsbehorigheter?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>				
				<g:each in="${tjanstekomponentList}" status="i" var="tjanstekomponentInstance">
					<g:if test="${tjanstekomponentInstance.isDeleted()}">
						<tr class="odd">
							<td><g:message code="default.tjanstekomponent.label" default="xxx"/></td>
							<td><g:link action="show" id="${tjanstekomponentInstance.id}">${tjanstekomponentInstance.hsaId}</g:link></td>
							<td>${tjanstekomponentInstance.beskrivning}</td>
							<td>
								<g:message code="default.tjanstekontrakt.label"/>
								<img src='${resource(dir:'images',file: "${tjanstekomponentInstance.anropsbehorigheter?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								<br>
									<g:message code="default.anropsadress.label"/>
									<img src='${resource(dir:'images',file: "${tjanstekomponentInstance.anropsAdresser?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				
				<g:each in="${filterList}" status="i" var="filterInstance">
					<g:if test="${filterInstance.isDeleted()}">
						<tr class="even">
							<td><g:message code="default.filter.label" default="xxx"/></td>
							<td><g:link action="show" id="${filterInstance.id}">${filterInstance.servicedomain}</g:link></td>
							<td>${filterInstance.beskrivning}</td>
							<td>
								<g:message code="default.filtercategorization.label"/>
								<img src='${resource(dir:'images',file: "${filterInstance.categorization?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								<br>
									<g:message code="default.anropsbehorighet.label"/>
									<img src='${resource(dir:'images',file: "${filterInstance.anropsbehorighet ? 'connected.png' : 'disconnected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				<g:each in="${filtercategorizationList}" status="i" var="filtercategorizationInstance">
					<g:if test="${filtercategorizationInstance.isDeleted()}">
						<tr class="odd">
							<td><g:message code="default.filtercategorization.label" default="xxx"/></td>						
							<td><g:link action="show" id="${filtercategorizationInstance.id}">${filtercategorizationInstance.category}</g:link></td>
							<td>${filtercategorizationInstance.beskrivning}</td>
							<td>
								<g:message code="default.filter.label"/>
								<img src='${resource(dir:'images',file: "${filtercategorizationInstance.filter ? 'connected.png' : 'disconnected.png' }")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				<g:each in="${anropsAdressList}" status="i" var="anropsAdressInstance">
					<g:if test="${anropsAdressInstance.isDeleted()}">
						<tr class="even">						
							<td><g:message code="default.anropsadress.label" default="xxx"/></td>
							<td><g:link action="show" id="${anropsAdressInstance.id}">${anropsAdressInstance.adress}</g:link></td>
							<td>-</td>
							<td>
								<g:message code="default.vagval.label"/>
								<img src='${resource(dir:'images',file: "${anropsAdressInstance.vagVal?.isEmpty() ? 'disconnected.png' : 'connected.png'}")}' />
								<br>
									<g:message code="default.tjanstekomponent.label"/>
									<img src='${resource(dir:'images',file: "${anropsAdressInstance.tjanstekomponent ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
									<g:message code="default.rivTaProfil.label"/>
									<img src='${resource(dir:'images',file: "${anropsAdressInstance.rivTaProfil ? 'connected.png' : 'disconnected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				<g:each in="${anropsbehorighetList}" status="i" var="anropsbehorighetInstance">
					<g:if test="${anropsbehorighetInstance.isDeleted()}">
						<tr class="odd">
						<td><g:message code="default.anropsbehorighet.label" default="xxx"/></td>
							<td><g:link action="show" id="${anropsbehorighetInstance.id}">${anropsbehorighetInstance.integrationsavtal}</g:link></td>
							<td>-</td>
							<td>
								<g:message code="default.tjanstekomponent.label"/>
								<img src='${resource(dir:'images',file: "${anropsbehorighetInstance.tjanstekonsument ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
									<g:message code="default.tjanstekontrakt.label"/>
									<img src='${resource(dir:'images',file: "${anropsbehorighetInstance.tjanstekontrakt ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
									<g:message code="default.logiskAdress.label"/>
									<img src='${resource(dir:'images',file: "${anropsbehorighetInstance.logiskAdress ? 'connected.png' : 'disconnected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>
				
				<g:each in="${vagvalList}" status="i" var="vagvalInstance">
					<g:if test="${vagvalInstance.isDeleted()}">
						<tr class="even">
							<td><g:message code="default.vagVal.label" default="xxx"/></td>
							<td><g:link action="show" id="${vagvalInstance.id}">${vagvalInstance.fromTidpunkt}-${vagvalInstance.tomTidpunkt}</g:link></td>
							<td>-</td>
							<td>
								<g:message code="default.tjanstekontrakt.label"/>
								<img src='${resource(dir:'images',file: "${vagvalInstance.tjanstekontrakt ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
									<g:message code="default.logiskAdress.label"/>
									<img src='${resource(dir:'images',file: "${vagvalInstance.logiskAdress ? 'connected.png' : 'disconnected.png'}")}' />
								<br>
								<g:message code="default.anropsadress.label"/>
								<img src='${resource(dir:'images',file: "${vagvalInstance.anropsAdress ? 'connected.png' : 'disconnected.png'}")}' />
							</td>
						</tr>
					</g:if>
				</g:each>				
				</tbody>
			</table>
		</div>
		
		<div id="create-pubVersion" class="content scaffold-create" role="main">
			<h1><g:message code="default.create.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<g:hasErrors bean="${pubVersionInstance}">
			<ul class="errors" role="alert">
				<g:eachError bean="${pubVersionInstance}" var="error">
				<li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
				</g:eachError>
			</ul>
			</g:hasErrors>
			<g:form action="save" >
				<fieldset class="form">
					<g:render template="form"/>
				</fieldset>
				<fieldset class="buttons">
					<g:submitButton name="create" class="save" value="${message(code: 'pubVersion.publish', default: 'x_Publicera')}" />
				</fieldset>
			</g:form>
		</div>
		
	<%--
		<div class="pagination">
			<g:paginate total="${rivTaProfilInstanceTotal}" />
		</div>--%>		
		
	</body>
</html>

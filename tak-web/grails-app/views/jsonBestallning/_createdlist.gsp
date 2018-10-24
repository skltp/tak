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
<div id="publish-updated-list" class="content scaffold-list" role="main">
	<h1>
		<g:message code="bestallning.create.list" />
	</h1>
	<table>
		<thead>
		<tr>
			<th width="15%" />
			<th width="70%"/>
		</tr>
		</thead>
		<tbody>
		<!-- cssClass is alternatively set to even:odd based on previous item row (note: not necessarily an item may exists)  -->
		<g:set var="cssClass" value="${'even'}"/>

		<g:set var="firstRow" value="${true}"/>
		<g:set var="tjanstekontrakt" value="${bestallning.inkludera.getTjanstekontrakt()}"/>
		<g:each in="${tjanstekontrakt}" status="i" var="tjanstekontraktInstance">
			<g:if test="${!tjanstekontraktInstance.getTjanstekontrakt()}">
				<tr class="${cssClass}">
					<g:if test="${firstRow}">
						<g:set var="firstRow" value="${false}"/>
						<td><g:message code="default.tjanstekontrakt.label" /></td>
					</g:if><g:else><td></td></g:else>
					<td>${tjanstekontraktInstance}</td>
				</tr>
			</g:if>
		</g:each>

		<g:if test="${!firstRow}">
			<g:set var="cssClass" value="${cssClass.equals('even')? 'odd' : 'even'}"/>
		</g:if>

		<g:set var="firstRow" value="${true}"/>
		<g:set var="logiskadress" value="${bestallning.inkludera.getLogiskadresser()}"/>
		<g:each in="${logiskadress}" status="i" var="logiskadressInstance">
			<g:if test="${!logiskadressInstance.getLogiskAdress()}">
				<tr class="${cssClass}">
					<g:if test="${firstRow}">
						<g:set var="firstRow" value="${false}"/>
						<td><g:message code="default.logiskAdress.label" /></td>
					</g:if><g:else><td></td></g:else>
					<td>${logiskadressInstance}</td>
				</tr>
			</g:if>
		</g:each>

		<g:if test="${!firstRow}">
			<g:set var="cssClass" value="${cssClass.equals('even')? 'odd' : 'even'}"/>
		</g:if>

		<g:set var="firstRow" value="${true}"/>
		<g:set var="tjanstekomponent" value="${bestallning.inkludera.getTjanstekomponenter()}"/>
		<g:each in="${tjanstekomponent}" status="i" var="tjanstekomponentInstance">
			<g:if test="${!tjanstekomponentInstance.getTjanstekomponent()}">
				<tr class="${cssClass}">
					<g:if test="${firstRow}">
						<g:set var="firstRow" value="${false}"/>
						<td><g:message code="default.tjanstekomponent.label" /></td>
					</g:if>
					<g:else>
						<td></td>
					</g:else>
					<td>${tjanstekomponentInstance}</td>
				</tr>
			</g:if>
		</g:each>

		<g:if test="${!firstRow}">
			<g:set var="cssClass" value="${cssClass.equals('even')? 'odd' : 'even'}"/>
		</g:if>

		<g:set var="firstRow" value="${true}"/>
		<g:set var="anropsbehorighet" value="${bestallning.inkludera.getAnropsbehorigheter()}"/>
		<g:each in="${anropsbehorighet}" status="i" var="anropsbehorighetInstance">
			<g:if test="${!anropsbehorighetInstance.getAnropsbehorighet()}">
				<tr class="${cssClass}">
					<g:if test="${firstRow}">
						<g:set var="firstRow" value="${false}"/>
						<td><g:message code="default.anropsbehorighet.label" /></td>
					</g:if>
					<g:else>
						<td></td>
					</g:else>
					<td>${anropsbehorighetInstance}</td>
				</tr>
			</g:if>
		</g:each>

		<g:if test="${!firstRow}">
			<g:set var="cssClass" value="${cssClass.equals('even')? 'odd' : 'even'}"/>
		</g:if>

		<g:set var="firstRow" value="${true}"/>
		<g:set var="vagval" value="${bestallning.inkludera.getVagval()}"/>
		<g:each in="${vagval}" status="i" var="vagvalInstance">
			<g:if test="${!vagvalInstance.getVagval()}">
				<tr class="${cssClass}">
					<g:if test="${firstRow}">
						<g:set var="firstRow" value="${false}"/>
						<td><g:message code="default.vagval.label" /></td>
					</g:if>
					<g:else>
						<td></td>
					</g:else>
					<td>${vagvalInstance}</td>
				</tr>
			</g:if>
		</g:each>

		</tbody>
	</table>
</div>
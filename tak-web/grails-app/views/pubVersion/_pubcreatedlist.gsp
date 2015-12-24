<div id="publish-created-list" class="content scaffold-list" role="main">
	<h1>
		<g:message code="pubVersion.created.list" />
	</h1>
	<table>
		<thead>
			<tr>
				<th width="15%" />
				<th />
			</tr>
		</thead>
		<tbody>
			<g:each in="${rivTaProfilList}" status="i" var="rivTaProfilInstance">
				<g:if test="${rivTaProfilInstance.isNewlyCreated()}">
					<tr class="even">
						<td><g:message code="default.rivTaProfil.label" /></td>
						<td><g:link controller="rivTaProfil" action="show" id="${rivTaProfilInstance.id}">${rivTaProfilInstance.getPublishInfo()}</g:link></td>
					</tr>
				</g:if>
			</g:each>

			<g:each in="${logiskAdressList}" status="i" var="logiskAdressInstance">
				<g:if test="${logiskAdressInstance.isNewlyCreated()}">
					<tr class="odd">
						<td><g:message code="default.logiskAdress.label" /></td>
						<td><g:link controller="logiskAdress" action="show"	id="${logiskAdressInstance.id}">${logiskAdressInstance.getPublishInfo()}</g:link></td>
					</tr>
				</g:if>
			</g:each>
			
			<g:each in="${tjanstekontraktList}" status="i" var="tjanstekontraktInstance">
				<g:if test="${tjanstekontraktInstance.isNewlyCreated()}">
					<tr class="even">
						<td><g:message code="default.tjanstekontrakt.label" /></td>
						<td><g:link controller="tjanstekontrakt" action="show" id="${tjanstekontraktInstance.id}">${tjanstekontraktInstance.getPublishInfo()}</g:link></td>
					</tr>
				</g:if>
			</g:each>
			
			<g:each in="${tjanstekomponentList}" status="i" var="tjanstekomponentInstance">
				<g:if test="${tjanstekomponentInstance.isNewlyCreated()}">
					<tr class="odd">
						<td><g:message code="default.tjanstekomponent.label" /></td>
						<td><g:link controller="tjanstekomponent" action="show" id="${tjanstekomponentInstance.id}">${tjanstekomponentInstance.getPublishInfo()}</g:link></td>
					</tr>
				</g:if>
			</g:each>

			<g:each in="${filterList}" status="i" var="filterInstance">
				<g:if test="${filterInstance.isNewlyCreated()}">
					<tr class="even">
						<td><g:message code="default.filter.label" /></td>
						<td><g:link controller="filter" action="show" id="${filterInstance.id}">${filterInstance.getPublishInfo()}</g:link></td>
					</tr>
				</g:if>
			</g:each>
			
			<g:each in="${filtercategorizationList}" status="i" var="filtercategorizationInstance">
				<g:if test="${filtercategorizationInstance.isNewlyCreated()}">
					<tr class="odd">
						<td><g:message code="default.filtercategorization.label" /></td>
						<td><g:link controller="filtercategorization" action="show"	id="${filtercategorizationInstance.id}">${filtercategorizationInstance.getPublishInfo()}</g:link></td>
					</tr>
				</g:if>
			</g:each>
			
			<g:each in="${anropsAdressList}" status="i" var="anropsAdressInstance">
				<g:if test="${anropsAdressInstance.isNewlyCreated()}">
					<tr class="even">
						<td><g:message code="default.anropsadress.label" /></td>
						<td><g:link controller="anropsAdress" action="show" id="${anropsAdressInstance.id}">${anropsAdressInstance.getPublishInfo()}</g:link></td>
					</tr>
				</g:if>
			</g:each>
			
			<g:each in="${anropsbehorighetList}" status="i" var="anropsbehorighetInstance">
				<g:if test="${anropsbehorighetInstance.isNewlyCreated()}">
					<tr class="odd">
						<td><g:message code="default.anropsbehorighet.label" /></td>
						<td><g:link controller="anropsbehorighet" action="show" id="${anropsbehorighetInstance.id}">${anropsbehorighetInstance.getPublishInfo()}</g:link></td>
					</tr>
				</g:if>
			</g:each>

			<g:each in="${vagvalList}" status="i" var="vagvalInstance">
				<g:if test="${vagvalInstance.isNewlyCreated()}">
					<tr class="even">
						<td><g:message code="default.vagval.label" /></td>
						<td><g:link controller="vagval" action="show" id="${vagvalInstance.id}">${vagvalInstance.getPublishInfo()}</g:link></td>
					</tr>
				</g:if>
			</g:each>

		</tbody>
	</table>
</div>
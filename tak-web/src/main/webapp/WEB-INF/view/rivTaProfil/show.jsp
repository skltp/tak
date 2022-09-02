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

<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:main>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="/">Hem</a></li>
				<li><a class="list" href="/rivTaProfil">RivTaProfil - Lista</a></li>
				<li><a class="create" href="/rivTaProfil/create">Skapa RivTaProfil</a></li>
			</ul>
		</div>
		<div id="show-rivTaProfil" class="content scaffold-show" role="main">
			<h1>RivTaProfil</h1>
			<%--<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>--%>
			<ol class="property-list rivTaProfil">

				<li class="fieldcontain">
					<span id="namn-label" class="property-label">Namn</span>
					
						<span class="property-value" aria-labelledby="namn-label">${rivTaProfilInstance.namn}</span>
					
				</li>

				<li class="fieldcontain">
					<span id="beskrivning-label" class="property-label">Beskrivning</span>
					
						<span class="property-value" aria-labelledby="beskrivning-label">${rivTaProfilInstance.beskrivning}</span>
					
				</li>

					<li class="fieldcontain">
						<span id="anropsAdresser-label" class="property-label">AnropsAdresser</span>

						<c:forEach items="${rivTaProfilInstance.anropsAdresser}" var="a">
							<%--<g:if test="${!a.isDeletedInPublishedVersion()}">--%>
								<span class="property-value" aria-labelledby="AnropsAdresser-label">
									<%--<tmpl:/chooseEntityIconCRUD entity="${a}" />--%>
									<a href="/anropsAdress/${a.id}">${a.toString()}</a>
								</span>
							<%--</g:if>--%>
						</c:forEach>
					</li>
				<%--
				<g:if test="${rivTaProfilInstance?.id}">
					<li class="fieldcontain">
						<span id="uniqueid-label" class="property-label"><g:message code="default.uniqueId.label" /></span>
						<span class="property-value" aria-labelledby="uniqueid-label"><g:fieldValue bean="${rivTaProfilInstance}" field="id"/></span>					
					</li>
				</g:if>
				
				<g:if test="${rivTaProfilInstance?.pubVersion}">
					<li class="fieldcontain">
						<span id="pubVersion-label" class="property-label"><g:message code="default.pubVersion.label" /></span>
						<span class="property-value" aria-labelledby="pubVersion-label"><g:fieldValue bean="${rivTaProfilInstance}" field="pubVersion"/></span>					
					</li>
				</g:if>
			
				<g:if test="${rivTaProfilInstance?.updatedTime}">
					<li class="fieldcontain">
						<g:if test="${flash.isCreated}">
							<span id="updatedTime-label" class="property-label"><g:message code="default.createdTime.label" /></span>
						</g:if>
						<g:else>
							<span id="updatedTime-label" class="property-label"><g:message code="default.updatedTime.label" /></span>
    					</g:else>
						<span class="property-value" aria-labelledby="updatedTime-label"><g:formatDate date="${rivTaProfilInstance?.updatedTime}" /></span>					
					</li>
				</g:if>
			
				<g:if test="${rivTaProfilInstance?.updatedBy}">
					<li class="fieldcontain">
						<g:if test="${flash.isCreated}">
							<span id="updatedBy-label" class="property-label"><g:message code="default.createdBy.label" /></span>
						</g:if>
						<g:else>
							<span id="updatedBy-label" class="property-label"><g:message code="default.updatedBy.label" /></span>
						</g:else>
						<span class="property-value" aria-labelledby="updatedBy-label"><g:fieldValue bean="${rivTaProfilInstance}" field="updatedBy"/></span>					
					</li>
				</g:if>
			
				<g:if test="${rivTaProfilInstance?.deleted}">
					<li class="fieldcontain">
						<span id="deleted-label" class="property-label"><g:message code="default.deleted.label" /></span>
						<span class="property-value" aria-labelledby="deleted-label"><g:formatBoolean boolean="${rivTaProfilInstance?.deleted}" /></span>
					
					</li>
				</g:if>--%>
			
			</ol>
			<%--<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${rivTaProfilInstance?.id}" />
					<g:link class="edit" action="edit" id="${rivTaProfilInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:if test="${!rivTaProfilInstance?.deleted}">
						<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
					</g:if>
				</fieldset>
			</g:form>--%>
		</div>
</t:main>

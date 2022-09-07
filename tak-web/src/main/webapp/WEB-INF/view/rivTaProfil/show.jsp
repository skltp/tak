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
			<c:if test="${message != null}">
			    <div class="message" role="status">${message}</div>
			</c:if>
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

						<c:forEach items="${rivTaProfilInstance.anropsAdresser}" var="entity">
							<c:if test="${!entity.isDeletedInPublishedVersion()}">
								<span class="property-value" aria-labelledby="AnropsAdresser-label">
									<%@include file="../_chooseEntityIconCRUD.jsp" %>
									<a href="/anropsAdress/${entity.id}">${entity.toString()}</a>
								</span>
							</c:if>
						</c:forEach>
					</li>

					<li class="fieldcontain">
						<span id="uniqueid-label" class="property-label">Id</span>
						<span class="property-value" aria-labelledby="uniqueid-label">${rivTaProfilInstance.id}</span>
					</li>
				
				<c:if test="${rivTaProfilInstance.pubVersion != null}">
					<li class="fieldcontain">
						<span id="pubVersion-label" class="property-label">Publicerad version</span>
						<span class="property-value" aria-labelledby="pubVersion-label">${rivTaProfilInstance.pubVersion}</span>
					</li>
				</c:if>

				<c:if test="${rivTaProfilInstance.updatedTime != null}">
					<li class="fieldcontain">
						<span id="updatedTime-label" class="property-label">Uppdaterad den</span>
						<span class="property-value" aria-labelledby="updatedTime-label">${rivTaProfilInstance.updatedTime}</span>
					</li>
				</c:if>
			
				<c:if test="${rivTaProfilInstance.updatedBy != null}">
					<li class="fieldcontain">
    					<span id="updatedBy-label" class="property-label">Uppdaterad av</span>
						<span class="property-value" aria-labelledby="updatedBy-label">${rivTaProfilInstance.updatedBy}</span>
					</li>
				</c:if>
			
				<c:if test="${rivTaProfilInstance.deleted}">
					<li class="fieldcontain">
						<span id="deleted-label" class="property-label">Borttagen</span>
						<span class="property-value" aria-labelledby="deleted-label">${rivTaProfilInstance.deleted}</span>
					</li>
				</c:if>
			</ol>
			<form method="POST" action="/rivTaProfil/delete">
				<fieldset class="buttons">
				    <input type="hidden" name="id" value="${rivTaProfilInstance.id}" />
					<a href="/rivTaProfil/edit/${rivTaProfilInstance.id}" class="edit">Redigera</a>
					<c:if test="${!rivTaProfilInstance.deleted}">
						<input type="submit" class="delete" value="Ta bort" onclick="return confirm('Är du säker?');" />
					</c:if>
				</fieldset>
			</form>
		</div>
</t:main>

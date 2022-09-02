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
				<li><a class="create" href="/rivTaProfil/create">Skapa RivTaProfil</a></li>
			</ul>
		</div>
		<div id="list-rivTaProfil" class="content scaffold-list" role="main">
			<h1>RivTaProfil - Lista</h1>
			<c:if test="${message != null}">
			    <div class="message" role="status">${message}</div>
			</c:if>
			<table>
				<thead>
					<tr>
						<th class="rightmostColumn" />
						
						<th class="rightmostColumn">PV</th>
										
						<th>Namn</th>
					
						<th>Beskrivning</th>
						
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${rivTaProfilInstanceList}" var="rivTaProfilInstance">
					<%--<g:if test="${!rivTaProfilInstance.isDeletedInPublishedVersion()}">--%>
						<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
							
							<td>
								<a href="rivTaProfil/${rivTaProfilInstance.id}">Visa
									<%--<g:if test="${rivTaProfilInstance.isNewlyCreated()}">
										<img src="${resource(dir:'images',file:'created.png')}" alt="Skapad" />
									</g:if>
									<g:elseif test="${rivTaProfilInstance.isUpdatedAfterPublishedVersion()}">
										<img src="${resource(dir:'images',file:'updated.png')}" alt="Uppdaterad" />
									</g:elseif>
									<g:elseif test="${rivTaProfilInstance.getDeleted()}">
										<img src="${resource(dir:'images',file:'trash.png')}" alt="Borttagen" />
									</g:elseif>--%>
									
								</a>
							</td>
							
							<td>${rivTaProfilInstance.pubVersion}</td>
						
							<td>${rivTaProfilInstance.namn}</td>
						
							<td>${rivTaProfilInstance.beskrivning}</td>
							
						</tr>
					<%--</g:if><g:else><tr id="${i++}"></tr></g:else>--%>
				</c:forEach>
				</tbody>
			</table>
			<%--<div class="pagination">
				<g:paginate total="${rivTaProfilInstanceTotal}" />
			</div>--%>
		</div>
</t:main>

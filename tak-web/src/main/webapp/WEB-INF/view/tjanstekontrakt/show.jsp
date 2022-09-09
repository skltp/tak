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
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:main>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="/">Hem</a></li>
				<li><a class="list" href="/tjanstekontrakt">Tjänstekontrakt - Lista</a></li>
				<li><a class="create" href="/tjanstekontrakt/create">Skapa Tjänstekontrakt</a></li>
			</ul>
		</div>
		<div id="show-tjanstekontrakt" class="content scaffold-show" role="main">
			<h1>Tjänstekontrakt</h1>
			<c:if test="${message != null}">
			    <div class="message" role="status">${message}</div>
			</c:if>
			<ol class="property-list">

				<li class="fieldcontain">
					<span id="namnrymd-label" class="property-label">Namn</span>
    				<span class="property-value" aria-labelledby="namnrymd-label">${instance.namnrymd}</span>
				</li>

				<li class="fieldcontain">
				<span id="beskrivning-label" class="property-label">Beskrivning</span>
				<span class="property-value" aria-labelledby="beskrivning-label">${instance.beskrivning}</span>
				</li>

				<li class="fieldcontain">
				<span id="majorVersion-label" class="property-label">Major version</span>
				<span class="property-value" aria-labelledby="majorVersion-label">${instance.majorVersion}</span>
				</li>

				<li class="fieldcontain">
				<span id="minorVersion-label" class="property-label">Minor version</span>
				<span class="property-value" aria-labelledby="minorVersion-label">${instance.minorVersion}</span>
				</li>

					<li class="fieldcontain">
						<span id="anropsbehorigheter-label" class="property-label">Anropsbehörigheter</span>

						<c:forEach items="${instance.anropsbehorigheter}" var="entity">
							<c:if test="${!entity.isDeletedInPublishedVersion()}">
								<span class="property-value" aria-labelledby="anropsbehorigheter-label">
									<%@include file="../_chooseEntityIconCRUD.jsp" %>
									<a href="/anropsbehorighet/${entity.id}">${entity.toString()}</a>
								</span>
							</c:if>
						</c:forEach>
					</li>

					<li class="fieldcontain">
						<span id="vagval-label" class="property-label">Vägval</span>

						<c:forEach items="${instance.vagval}" var="entity">
							<c:if test="${!entity.isDeletedInPublishedVersion()}">
								<span class="property-value" aria-labelledby="vagval-label">
									<%@include file="../_chooseEntityIconCRUD.jsp" %>
									<a href="/vagval/${entity.id}">${entity.toString()}</a>
								</span>
							</c:if>
						</c:forEach>
					</li>

					<li class="fieldcontain">
						<span id="uniqueid-label" class="property-label">Id</span>
						<span class="property-value" aria-labelledby="uniqueid-label">${instance.id}</span>
					</li>
				
				<c:if test="${instance.pubVersion != null}">
					<li class="fieldcontain">
						<span id="pubVersion-label" class="property-label">Publicerad version</span>
						<span class="property-value" aria-labelledby="pubVersion-label">${instance.pubVersion}</span>
					</li>
				</c:if>

				<c:if test="${instance.updatedTime != null}">
					<li class="fieldcontain">
						<span id="updatedTime-label" class="property-label">Uppdaterad den</span>
						<span class="property-value" aria-labelledby="updatedTime-label">${instance.updatedTime}</span>
					</li>
				</c:if>
			
				<c:if test="${instance.updatedBy != null}">
					<li class="fieldcontain">
    					<span id="updatedBy-label" class="property-label">Uppdaterad av</span>
						<span class="property-value" aria-labelledby="updatedBy-label">${instance.updatedBy}</span>
					</li>
				</c:if>
			
				<c:if test="${instance.deleted}">
					<li class="fieldcontain">
						<span id="deleted-label" class="property-label">Borttagen</span>
						<span class="property-value" aria-labelledby="deleted-label">${instance.deleted}</span>
					</li>
				</c:if>
			</ol>
			<form method="POST" action="/tjanstekontrakt/delete">
				<fieldset class="buttons">
				    <input type="hidden" name="id" value="${instance.id}" />
					<a href="/tjanstekontrakt/edit/${instance.id}" class="edit">Redigera</a>
					<c:if test="${!instance.deleted}">
						<input type="submit" class="delete" value="Ta bort" onclick="return confirm('Är du säker?');" />
					</c:if>
				</fieldset>
			</form>
		</div>
</t:main>

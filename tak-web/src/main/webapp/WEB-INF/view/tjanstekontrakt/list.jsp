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
				<li><a class="create" href="${basePath}/create">Skapa Tjänstekontrakt</a></li>
			</ul>
		</div>
		<div id="list-tjanstekontrakt" class="content scaffold-list" role="main">
			<h1>Tjänstekontrakt - Lista</h1>
			<c:if test="${message != null}">
			    <div class="message" role="status">${message}</div>
			</c:if>
			<table>
				<thead>
					<tr>
						<th class="rightmostColumn" />
						<th class="rightmostColumn">PV</th>
						<th>Namnrymd</th>
						<th>Major version</th>
						<th>Minor version</th>
						<th>Beskrivning</th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${list.content}" var="entity" varStatus="status">
						<tr class="${(status.index % 2) == 0 ? 'even' : 'odd'}">
							<td>
								<a href="${basePath}/${entity.id}">Visa
									<%@include file="../_chooseEntityIconCRUD.jsp" %>
								</a>
							</td>
							<td>${entity.pubVersion}</td>
							<td>${entity.namnrymd}</td>
							<td>${entity.majorVersion}</td>
							<td>${entity.minorVersion}</td>
							<td>${entity.beskrivning}</td>
						</tr>
				</c:forEach>
				</tbody>
			</table>
			<%@include file="../_listPagination.jsp" %>
		</div>
</t:main>

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
<%@ page import="se.skl.tp.vagval.admin.core.entity.Filter" %>



<div class="fieldcontain ${hasErrors(bean: filterInstance, field: 'anropsbehorighet', 'error')} ">
	<label for="anropsbehorighet">
		<g:message code="filter.anropsbehorighet.label" default="Anropsbehorighet" />
		
	</label>
	<g:select id="anropsbehorighet" name="anropsbehorighet.id" from="${se.skl.tp.vagval.admin.core.entity.Anropsbehorighet.list()}" optionKey="id" required="" value="${filterInstance?.anropsbehorighet?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: filterInstance, field: 'servicedomain', 'error')} ">
	<label for="servicedomain">
		<g:message code="filter.servicedomain.label" default="Servicedomain" />
		
	</label>
	<g:textField name="servicedomain" value="${filterInstance?.servicedomain}" />
</div>
<g:if test="${filterInstance.id}">

<div class="fieldcontain ${hasErrors(bean: filterInstance, field: 'categorization', 'error')} ">
	<label for="categorization">
		<g:message code="filter.categorization.label" default="Categorization" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${filterInstance?.categorization?}" var="c">
    <li><g:link controller="filtercategorization" action="show" id="${c.id}">${c?.id?.encodeAsHTML()} - ${c?.category?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="filtercategorization" action="create" params="['filter.id': filterInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'filtercategorization.label', default: 'Filtercategorization')])}</g:link>
</li>
</ul>

</div>

</g:if>



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
<%@ page import="se.skltp.tak.core.entity.Filtercategorization" %>



<div class="fieldcontain ${hasErrors(bean: filtercategorizationInstance, field: 'filter', 'error')} ">
	<label for="filter">
		<g:message code="filtercategorization.filter.label" default="Filter" />
		
	</label>
	<g:select id="filter" name="filter.id" from="${se.skltp.tak.core.entity.Filter.list()}" optionKey="id" required="" optionValue="${{it.id + '-' + it.servicedomain}}" value="${filtercategorizationInstance?.filter?.id}" class="many-to-one"/>
</div>

<div class="fieldcontain ${hasErrors(bean: filtercategorizationInstance, field: 'category', 'error')} ">
	<label for="category">
		<g:message code="filtercategorization.category.label" default="Category" />
		
	</label>
	<g:textField name="category" value="${filtercategorizationInstance?.category}" />
</div>


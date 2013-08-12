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
<%@ page import="se.skl.tp.vagval.admin.web.entity.Anvandare" %>



<div class="fieldcontain ${hasErrors(bean: anvandareInstance, field: 'anvandarnamn', 'error')} required">
	<label for="anvandarnamn">
		<g:message code="anvandare.anvandarnamn.label" default="Anvandarnamn" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="anvandarnamn" required="" value="${anvandareInstance?.anvandarnamn}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: anvandareInstance, field: 'administrator', 'error')} ">
	<label for="administrator">
		<g:message code="anvandare.administrator.label" default="Administrator" />
		
	</label>
	<g:checkBox name="administrator" value="${anvandareInstance?.administrator}" />
</div>

<div class="fieldcontain ${hasErrors(bean: anvandareInstance, field: 'losenord', 'error')} ">
	<label for="losenord">
		<g:message code="anvandare.losenord.label" default="Losenord" />
		
	</label>
	<g:passwordField name="losenord" value="${anvandareInstance?.losenord}"/>
</div>


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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="fieldcontain <%--${hasErrors(bean: instance, field: 'namn', 'error')}--%> required">
	<label for="namn">
		Namn
		<span class="required-indicator">*</span>
	</label>
	<form:textarea path="namn" cols="40" rows="5" maxlength="255" required="" />
</div>

<div class="fieldcontain <%--${hasErrors(bean: instance, field: 'beskrivning', 'error')} --%>">
	<label for="beskrivning">
		Beskrivning
	</label>
	<form:textarea path="beskrivning" cols="40" rows="5" maxlength="255" />
</div>

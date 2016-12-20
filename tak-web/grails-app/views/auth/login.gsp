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
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="layout" content="main" />
  <title><g:message code="login.title" default="Login to TP ${grailsApplication.metadata.'app.version'}" /> (${grailsApplication.config.tak.environment})</title>
</head>
<body OnLoad="document.signInForm.username.focus();">
  <div class="body">
  <h1 style="margin-left:20px;"><g:message code="login.title" default="Login to TP ${grailsApplication.metadata.'app.version'}" /></h1>
  <br/>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <g:form name="signInForm" action="signIn">
    <input type="hidden" name="targetUri" value="${targetUri}" />
		<fieldset class="form">
      <div class="fieldcontain">
        	<label for="username">
        		<g:message code="login.username" default="Username" />
        	</label>
        	<g:textField name="username" required="" value="${username}"/>
        </div>
        <div class="fieldcontain">
          	<label for="username">
          		<g:message code="login.password" default="Password" />
          	</label>
          	<g:passwordField name="password" required="" value=""/>
          </div>
  		</fieldset>
			<fieldset class="buttons">
				<input type="submit" value="${message(code: 'login.signIn', 'default': 'Sign in')}" />
  		</fieldset>
  </g:form>
  </div>
<script language="JavaScript">
document.signInForm.username.focus();
</script>
</body>
</html>

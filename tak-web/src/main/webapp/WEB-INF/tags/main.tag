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
<%@tag description="Main layout" pageEncoding="UTF-8"%>
<%@attribute name="title" fragment="true" %>
<%@attribute name="header" fragment="true" %>
<html>
    <head>
        <title><jsp:invoke fragment="title"/></title>
        <link rel="stylesheet" href="/static/css/main1.css" />
        <jsp:invoke fragment="header"/>
    </head>
    <body>
        <%--<div id="spinner" class="spinner" style="display:none;">
            <img src="/static/images/spinner.gif" alt="Spinner" />
        </div>--%>
        <div class="logo">
          <table border="0" cellpadding="3" cellspacing="3" style="background: ${grailsApplication.config.tak.background}">
            <tr style="background: ${grailsApplication.config.tak.background}">
              <td><img src="/static/images/inera-logo.png" alt="Inera" height="100px"/></td>
              <td><p align="right">Version <g:meta name="app.version"/><br />Milj&ouml; ${grailsApplication.config.tak.environment}</p></td>
            </tr>
            <%--<shiro:isLoggedIn>
            <tr style="background: ${grailsApplication.config.tak.background}">
     			 <td><g:message code="login.signedInAs" default="Signed in as" />: <shiro:principal/> (<g:link controller="auth" action="signOut"><g:message code="login.signOut" default="Sign out" /></g:link>)</td>
            </tr>
    		</shiro:isLoggedIn>--%>
          </table>
        </div>
        <jsp:doBody/>
    </body>
</html>

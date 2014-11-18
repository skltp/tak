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
        <title><g:layoutTitle/></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead/>
		<r:layoutResources />			
    </head>
    <body>
        <div id="spinner" class="spinner" style="display:none;">
            <img src="${resource(dir:'images',file:'spinner.gif')}" alt="Spinner" />
        </div>	
        <div class="logo">
          <table border="0" cellpadding="3" cellspacing="3">
            <tr>
              <td><img src="${resource(dir:'images',file:'inera-logo.png')}" alt="CeHis" height="100px"/></td>
              <td><p align="right">Version <g:meta name="app.version"/></p></td>
            </tr>
            <shiro:isLoggedIn>
                <tr>
         			    <td><g:message code="login.signedInAs" default="Signed in as" />: <shiro:principal/> (<g:link controller="auth" action="signOut"><g:message code="login.signOut" default="Sign out" /></g:link>)</td>
                </tr>
        		</shiro:isLoggedIn>
          </table>
        </div>
        <g:layoutBody />
        <g:javascript library="application"/>
        <r:layoutResources />
    </body>	
</html>
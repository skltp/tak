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
        <title><g:message code="application.title" default="Welcome to TP ${grailsApplication.metadata.'app.version'}" /></title>
		<meta name="layout" content="main" />
    </head>
    <body>
        <h1 style="margin-left:20px;"><g:message code="application.title" default="Welcome to TP ${grailsApplication.metadata.'app.version'}" /></h1>
        <p style="margin-left:20px;width:80%"><g:message code="application.description"
        default="This is the Administration Application for the Service Catalogue of TP ${grailsApplication.metadata.'app.version'}" /></p>
        <br/>
        <hr/>
        <br/>
        <div class="dialog" style="margin-left:40px;width:60%;">
            <ul>
              <li>
                <g:link controller="rivTaProfil">
                  <g:message code="topmenu.rivTaProfil" default="Administrate Riv TA profiles" />
                </g:link>
              </li>
              <li>
                <g:link controller="tjanstekontrakt">
                  <g:message code="topmenu.tjanstekontrakt" default="Administrate Service Contracts" />
                </g:link>
              </li>
              <li>
                <g:link controller="tjanstekomponent">
                  <g:message code="topmenu.tjanstekomponent" default="Administrate Service Components" />
                </g:link>
              </li>
              <li>
                <g:link controller="logiskAdress">
                  <g:message code="topmenu.logiskAdress" default="Administrate Logical Addressees" />
                </g:link>
              </li>
              <li>
                <g:link controller="anropsAdress">
                  <g:message code="topmenu.anropsadresser" default="Administrate Adresses" />
                </g:link>
              </li>
              <li>
                <g:link controller="vagval">
                  <g:message code="topmenu.vagval" default="Administrate Routing" />
                </g:link>
              </li>
              <li>
                <g:link controller="anropsbehorighet">
                  <g:message code="topmenu.anropsbehorighet" default="Administrate Call Authorizations" />
                </g:link>
              </li>
              <li>
                <g:link controller="filter">
                  <g:message code="topmenu.filter" default="Administrate Filters" />
                </g:link>
              </li>
              <li>
                <g:link controller="filtercategorization">
                  <g:message code="topmenu.filtercategorization" default="Administrate Filtercategorizations" />
                </g:link>
              </li>
              <shiro:hasRole name="Admin">
               <li>
                 <g:link controller="logiskAdress" action="bulkcreate">
                   <g:message code="beställning" />
                 </g:link>
               </li>
              </shiro:hasRole>
            </ul>
        </div>
        <shiro:hasRole name="Admin">
          <br/>
	      <hr/>
      	  <br/>
          <div class="dialog" style="margin-left:40px;width:60%;">
            <ul>
              <li>
                <g:link controller="pubVersion">
                  <g:message code="topmenu.publish" action="list"/>
                </g:link>
              </li>
              <li>
                <g:link controller="anvandare">
                  <g:message code="topmenu.anvandare" default="Administrate Users" />
                </g:link>
              </li>              
            </ul>
          </div>
        </shiro:hasRole>        
        <br/>
        <hr/>
        <br/>
    </body>
</html>
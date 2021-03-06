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
        <title><g:message code="application.title" default="Welcome to TP ${grailsApplication.metadata.'app.version'}" /> (${grailsApplication.config.tak.environment})</title>
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
                  <g:message code="topmenu.rivTaProfile" default="Administrate Riv TA profiles" />
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
            </ul>
        </div>
        <br/>
      	<hr/>
   	  	<br/>
       	<div class="dialog" style="margin-left:40px;width:60%;">
        	<ul>
            	<shiro:hasRole name="Admin">
               		<li>
                 		<g:link controller="logiskAdress" action="bulkcreate">
                   			<g:message code="beställning" />
                 		</g:link>
               		</li>
                    <li>
                        <g:link controller="jsonBestallning" action="createPage">
                            <g:message code="json.bestallning" />
                        </g:link>
                    </li>
                    <!--
                    <li>
                 		<g:link controller="logiskAdress" action="bulkdelete">
                   			<g:message code="topmenu.bulkdelete" />
                 		</g:link>
               		</li>
               		-->
              </shiro:hasRole>
               	<li>
                	<g:link controller="pubVersion">
                  		<g:message code="topmenu.publish" action="list"/>
                	</g:link>
              	</li>
              	<li>
              		<g:link controller="pubVersion" action="create">
              			<g:message code="pubVersion.preview.label" />
              		</g:link>
              	</li> 
              	<li>
              		<g:link url ="../tak-services/reset/pv" target="_blank">
              			<g:message code="pubVersion.rensa.cache.label" />
              		</g:link>
              	</li> 
				<br/>
                <li>
                    <g:link controller="anropsAdress" action="deletelist">
                        <g:message code="topmenu.anropsAdressDelete" default="Delete anropsAdress" />
                    </g:link>
                </li>
                <li>
                    <g:link controller="vagval" action="deletelist">
                        <g:message code="topmenu.vagvaldelete" default="Delete vagval" />
                    </g:link>
                </li>
                <li>
                    <g:link controller="anropsbehorighet" action="deletelist">
                        <g:message code="topmenu.anropsbehorighetDelete" default="Delete anropsbehorighet" />
                    </g:link>
                </li>
            </ul>
        </div>
        <shiro:hasRole name="Admin">
        	<br/>
	      	<hr/>
      	  	<br/>
          	<div class="dialog" style="margin-left:40px;width:60%;">
            	<ul>
              		<li>
                		<g:link controller="anvandare">
                  			<g:message code="topmenu.anvandare" default="Administrate Users" />
                		</g:link>
              		</li>
                    <li>
                        <g:link controller="TAKSettings">
                            <g:message code="topmenu.settings" default="Administrate Settings" />
                        </g:link>
                    </li>
                    <br/>
                    <li>
                        <g:link controller="pubVersion" action="list" params="[rollback:true]">
                            <g:message code="topmenu.rollback"/>
                        </g:link>
                    </li>
                    <li>
                        <g:link controller="pubVersion" action="list" params="[resetCache:true]">
                            <g:message code="topmenu.resetCacheToVersion"/>
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
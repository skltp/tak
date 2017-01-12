
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
<%@ page import="se.skltp.tak.core.entity.Anropsbehorighet"%>
<%@	page import="org.joda.time.DateTime"%>
<!DOCTYPE html>
<html>

 <head>
  <meta name="layout" content="main">
  <g:set var="entityName" value="${message(code: 'anropsbehorighet.label', default: 'Anropsbehorighet')}" />
  <title><g:message code="default.bulkdelete.label" args="[entityName]" /></title>
  <r:require module="select2" />
 </head>
 
 <body>
  <a href="#bulk-delete-anropsbehorighet" class="skip" tabindex="-1">
   <g:message code="default.link.skip.label" default="Skip to content&hellip;" />
  </a>
   
 <div class="nav" role="navigation">
  <ul>
   <li>
    <a class="home" href="${createLink(uri: '/')}">
     <g:message code="default.home.label" />
    </a>
   </li>
  </ul>
 </div>
 
 <div id="bulkconfirm-anropsbehorighet" class="content scaffold-create" role="main">
  <h1>
   <g:message code="default.bulkdelete.label" args="[entityName]" />
  </h1>
  <g:if test="${flash.message}">
   <div class="message" role="status">
    ${flash.message}
   </div>
  </g:if>

     <g:form params="${params}">
         <table>
             <thead>
             <tr>
                 <g:sortableColumn property="pubVersion" title="${message(code: 'default.version.label', default: 'x_PV')}" class="rightmostColumn" />

                 <g:sortableColumn property="integrationsavtal" title="${message(code: 'anropsbehorighet.integrationsavtal.label', default: 'Integrationsavtal')}" params="${filterParams}" />

                 <g:sortableColumn property="tjanstekonsument" title="${message(code: 'anropsbehorighet.tjanstekonsument.label', default: 'Tjanstekonsument')}" params="${filterParams}" />

                 <g:sortableColumn property="tjanstekontrakt" title="${message(code: 'anropsbehorighet.tjanstekontrakt.label', default: 'Tjanstekontrakt')}" params="${filterParams}" />

                 <g:sortableColumn property="logiskAdress" title="${message(code: 'anropsbehorighet.logiskAdress.label', default: 'Logisk Adress')}" params="${filterParams}" />

                 <g:sortableColumn property="fromTidpunkt" title="${message(code: 'anropsbehorighet.fromTidpunkt.label', default: 'From Tidpunkt')}" params="${filterParams}"/>

                 <g:sortableColumn property="tomTidpunkt" title="${message(code: 'anropsbehorighet.tomTidpunkt.label', default: 'Tom Tidpunkt')}" params="${filterParams}"/>

             </tr>
             </thead>
             <tbody>
             <g:each in="${anropsbehorighetInstanceListDelete}" status="i" var="anropsbehorighetInstance">
                 <g:if test="${!anropsbehorighetInstance.isDeletedInPublishedVersion()}">
                     <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">

                         <td>${anropsbehorighetInstance.pubVersion}</td>

                         <td>${fieldValue(bean: anropsbehorighetInstance, field: "integrationsavtal")}</td>

                         <td><g:link action="show" controller="tjanstekomponent" id="${anropsbehorighetInstance.tjanstekonsument.id}">${fieldValue(bean: anropsbehorighetInstance, field: "tjanstekonsument")}</g:link></td>

                         <td><g:link action="show" controller="tjanstekontrakt" id="${anropsbehorighetInstance.tjanstekontrakt.id}">${fieldValue(bean: anropsbehorighetInstance, field: "tjanstekontrakt")}</g:link></td>

                         <td><g:link action="show" controller="logiskAdress" id="${anropsbehorighetInstance.logiskAdress.id}">${fieldValue(bean: anropsbehorighetInstance, field: "logiskAdress")}</g:link></td>

                         <td><g:formatDate date="${anropsbehorighetInstance.fromTidpunkt}" /></td>

                         <td><g:formatDate date="${anropsbehorighetInstance.tomTidpunkt}" /></td>

                     </tr>
                 </g:if><g:else><tr id="${i++}"></tr></g:else>
             </g:each>
             </tbody>
         </table>
         <fieldset class="buttons">
             <g:actionSubmit class="delete" action="bulkDelete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.bulkdelete.confirm.message', default: 'Are you sure?')}');" />
         </fieldset>
     </g:form>
 </div>
</body>
</html>

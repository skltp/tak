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

<%@ page import="se.skltp.tak.core.entity.Vagval"%>
<!DOCTYPE html>
<html>

  <head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'vagval.label')}" />
    <title><g:message code="default.bulkdelete.label" args="[entityName]" /></title>
    <r:require module="select2" />
  </head>

  <body>
    <a href="#bulk-tabort-vagval" class="skip" tabindex="-1">
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

    <div id="bulkconfirm-vagval" class="content scaffold-create" role="main">

      <h1 style="...">
       <g:message code="default.bulkdelete.label" args="[entityName]" /> <p>   Antal: ${vagvalInstanceListDelete.size()} </p>
      </h1>

      <g:if test="${flash.message}">
        <div class="message" role="status">
          ${flash.message}
        </div>
      </g:if>

      <g:form params="${params}"  url="[controller:'vagval',action:'bulkDelete']" >
        <table>
          <thead>
          <tr>
            <g:sortableColumn property="toDelete" title="Valda" class="rightmostColumn" />

            <g:sortableColumn property="pubVersion" title="${message(code: 'default.version.label', default: 'x_PV')}"  />

            <g:sortableColumn property="rivTaProfil" title="${message(code: 'vagval.rivTaProfil.label', default: 'Riv TA Version')}" params="${filterParams}" />

            <g:sortableColumn property="tjanstekontrakt" title="${message(code: 'vagval.tjanstekontrakt.label', default: 'Tjanstekontrakt')}" params="${filterParams}" />

            <g:sortableColumn property="logiskAdress" title="${message(code: 'vagval.logiskAdress.label', default: 'Logisk Adress')}" params="${filterParams}" />

            <g:sortableColumn property="anropsAdress" title="${message(code: 'vagval.anropsAdress.label', default: 'AnropsAdress')}" params="${filterParams}" />

            <g:sortableColumn property="fromTidpunkt" title="${message(code: 'vagval.fromTidpunkt.label', default: 'From Tidpunkt')}" />

            <g:sortableColumn property="tomTidpunkt" title="${message(code: 'vagval.tomTidpunkt.label', default: 'Tom Tidpunkt')}" />

          </tr>
          </thead>
          <tbody>
          <g:each in="${vagvalInstanceListDelete}" status="i" var="vagvalInstance">
            <g:if test="${!vagvalInstance.isDeletedInPublishedVersion()}">
              <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                <td>
                  <g:checkBox name="toDelete" class="columnCheckbox" id="${vagvalInstance.id}" value="${vagvalInstance.id}" checked="true"></g:checkBox>
                </td>

                <td>${vagvalInstance.pubVersion}</td>

                <td>${fieldValue(bean: vagvalInstance, field: "anropsAdress.rivTaProfil.namn")}</td>

                <td><g:link action="show" controller="tjanstekontrakt" id="${vagvalInstance.tjanstekontrakt.id}">${fieldValue(bean: vagvalInstance, field: "tjanstekontrakt")}</g:link></td>

                <td><g:link action="show" controller="logiskAdress" id="${vagvalInstance.logiskAdress.id}">${fieldValue(bean: vagvalInstance, field: "logiskAdress")}</g:link></td>

                <td><g:link action="show" controller="anropsAdress" id="${vagvalInstance.anropsAdress.tjanstekomponent.id}">
                  ${fieldValue(bean: vagvalInstance, field: "anropsAdress.adress")}
                </g:link>
                </td>

                <td><g:formatDate date="${vagvalInstance.fromTidpunkt}" /></td>

                <td><g:formatDate date="${vagvalInstance.tomTidpunkt}" /></td>

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

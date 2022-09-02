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
<%@ page import="tak.web.jsonBestallning.ReportService" %>
<%
    def reportService = grailsApplication.classLoader.loadClass('tak.web.jsonBestallning.ReportService').newInstance()
%>

<div id="publish-updated-list" class="content scaffold-list" role="main">

    <h1>
        <g:message code="bestallning.inkludera"/>
    </h1>

    <h1>
        <g:message code="bestallning.logiskadress"/>
    </h1>
    <table>
        <thead>
        <tr>
            <th width="15%"/>
            <th width="70%"/>
        </tr>
        </thead>
        <tbody>
        <!-- cssClass is alternatively set to even:odd based on previous item row (note: not necessarily an item may exists)  -->
        <g:set var="cssClass" value="${'even'}"/>
        <g:set var="firstRow" value="${true}"/>
        <g:set var="bestallningar" value="${bestallning.inkludera.logiskadresser}"/>
        <g:each in="${bestallningar}" status="i" var="bestallning">
            <tr class="${cssClass}">
                <g:if test="${firstRow}">
                    <g:set var="firstRow" value="${false}"/>
                </g:if>
                <g:set var="report" value="${reportService.getReportData(bestallning, bestallningsData)}"/>
                <td class="${report.warning ? 'warning' : ''}">
                    ${report.status}
                </td>
                <td>${report.value}</td>
            </tr>
            <g:if test="${!firstRow}">
                <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
            </g:if>
        </g:each>
        </tbody>
    </table>



    <g:if test="${!firstRow}">
        <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
    </g:if>


    <h1>
        <g:message code="bestallning.tjanstekontrakt"/>
    </h1>
    <table>
        <thead>
        <tr>
            <th width="15%"/>
            <th width="70%"/>
        </tr>
        </thead>
        <tbody>
        <!-- cssClass is alternatively set to even:odd based on previous item row (note: not necessarily an item may exists)  -->
        <g:set var="cssClass" value="${'even'}"/>
        <g:set var="firstRow" value="${true}"/>
        <g:set var="bestallningar" value="${bestallning.inkludera.tjanstekontrakt}"/>
        <g:each in="${bestallningar}" status="i" var="bestallning">
            <tr class="${cssClass}">
                <g:if test="${firstRow}">
                    <g:set var="firstRow" value="${false}"/>
                </g:if>
                <g:set var="report" value="${reportService.getReportData(bestallning, bestallningsData)}"/>
                <td class="${report.warning ? 'warning' : ''}">
                    ${report.status}
                </td>
                <td>${report.value}</td>
            </tr>
            <g:if test="${!firstRow}">
                <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
            </g:if>
        </g:each>
        </tbody>
    </table>



    <g:if test="${!firstRow}">
        <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
    </g:if>


    <h1>
        <g:message code="bestallning.tjanstekomponent"/>
    </h1>
    <table>
        <thead>
        <tr>
            <th width="15%"/>
            <th width="70%"/>
        </tr>
        </thead>
        <tbody>
        <!-- cssClass is alternatively set to even:odd based on previous item row (note: not necessarily an item may exists)  -->
        <g:set var="cssClass" value="${'even'}"/>
        <g:set var="firstRow" value="${true}"/>
        <g:set var="bestallningar" value="${bestallning.inkludera.tjanstekomponenter}"/>
        <g:each in="${bestallningar}" status="i" var="bestallning">
            <tr class="${cssClass}">
                <g:if test="${firstRow}">
                    <g:set var="firstRow" value="${false}"/>
                </g:if>
                <g:set var="report" value="${reportService.getReportData(bestallning, bestallningsData)}"/>
                <td class="${report.warning ? 'warning' : ''}">
                    ${report.status}
                </td>
                <td>${report.value}</td>
            </tr>
            <g:if test="${!firstRow}">
                <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
            </g:if>
        </g:each>
        </tbody>
    </table>

    <g:if test="${!firstRow}">
        <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
    </g:if>


    <h1>
        <g:message code="bestallning.anropsbehorighet"/>
    </h1>
    <table>
        <thead>
        <tr>
            <th width="15%"/>
            <th width="70%"/>
        </tr>
        </thead>
        <tbody>
        <!-- cssClass is alternatively set to even:odd based on previous item row (note: not necessarily an item may exists)  -->
        <g:set var="cssClass" value="${'even'}"/>
        <g:set var="firstRow" value="${true}"/>
        <g:set var="bestallningar" value="${bestallning.inkludera.anropsbehorigheter}"/>
        <g:each in="${bestallningar}" status="i" var="bestallning">
            <tr class="${cssClass}">
                <g:if test="${firstRow}">
                    <g:set var="firstRow" value="${false}"/>
                </g:if>
                <g:set var="report" value="${reportService.getReportData(bestallning, bestallningsData)}"/>
                <td class="${report.warning ? 'warning' : ''}">
                    ${report.status}
                </td>
                <td>${report.value}</td>
            </tr>
            <g:if test="${!firstRow}">
                <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
            </g:if>
        </g:each>
        </tbody>
    </table>

    <g:if test="${!firstRow}">
        <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
    </g:if>


    <h1>
        <g:message code="bestallning.vagval"/>
    </h1>
    <table>
        <thead>
        <tr>
            <th width="15%"/>
            <th width="70%"/>
        </tr>
        </thead>
        <tbody>
        <!-- cssClass is alternatively set to even:odd based on previous item row (note: not necessarily an item may exists)  -->
        <g:set var="cssClass" value="${'even'}"/>
        <g:set var="firstRow" value="${true}"/>
        <g:set var="bestallningar" value="${bestallning.inkludera.vagval}"/>
        <g:each in="${bestallningar}" status="i" var="bestallning">
            <g:if test="${firstRow}">
                <g:set var="firstRow" value="${false}"/>
            </g:if>
            <g:set var="report" value="${reportService.getReportData(bestallning, bestallningsData)}"/>
            <g:each in="${report}" status="j" var="reportElement">
                <tr class="${cssClass}">
                    <td>
                        ${reportElement.status}
                    </td>
                    <td>${reportElement.value}</td>
                </tr>
                <g:if test="${!firstRow}">
                    <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
                </g:if>
            </g:each>
        </g:each>
        </tbody>
    </table>


    <h1>
        <g:message code="bestallning.exkludera"/>
    </h1>

    <g:if test="${!firstRow}">
        <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
    </g:if>


    <h1>
        <g:message code="bestallning.logiskadress"/>
    </h1>
    <table>
        <thead>
        <tr>
            <th width="15%"/>
            <th width="70%"/>
        </tr>
        </thead>
        <tbody>
        <!-- cssClass is alternatively set to even:odd based on previous item row (note: not necessarily an item may exists)  -->
        <g:set var="cssClass" value="${'even'}"/>
        <g:set var="firstRow" value="${true}"/>
        <g:set var="bestallningar" value="${bestallning.exkludera.logiskadresser}"/>
        <g:each in="${bestallningar}" status="i" var="bestallning">
            <tr class="${cssClass}">
                <g:if test="${firstRow}">
                    <g:set var="firstRow" value="${false}"/>
                </g:if>
                <g:set var="report" value="${reportService.getReportData(bestallning, bestallningsData)}"/>
                <td class="${report.warning ? 'warning' : ''}">
                    ${report.status}
                </td>
                <td>${report.value}</td>
            </tr>
            <g:if test="${!firstRow}">
                <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
            </g:if>
        </g:each>
        </tbody>
    </table>



    <g:if test="${!firstRow}">
        <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
    </g:if>


    <h1>
        <g:message code="bestallning.tjanstekontrakt"/>
    </h1>
    <table>
        <thead>
        <tr>
            <th width="15%"/>
            <th width="70%"/>
        </tr>
        </thead>
        <tbody>
        <!-- cssClass is alternatively set to even:odd based on previous item row (note: not necessarily an item may exists)  -->
        <g:set var="cssClass" value="${'even'}"/>
        <g:set var="firstRow" value="${true}"/>
        <g:set var="bestallningar" value="${bestallning.exkludera.tjanstekontrakt}"/>
        <g:each in="${bestallningar}" status="i" var="bestallning">
            <tr class="${cssClass}">
                <g:if test="${firstRow}">
                    <g:set var="firstRow" value="${false}"/>
                </g:if>
                <g:set var="report" value="${reportService.getReportData(bestallning, bestallningsData)}"/>
                <td class="${report.warning ? 'warning' : ''}">
                    ${report.status}
                </td>
                <td>${report.value}</td>
            </tr>
            <g:if test="${!firstRow}">
                <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
            </g:if>
        </g:each>
        </tbody>
    </table>



    <g:if test="${!firstRow}">
        <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
    </g:if>


    <h1>
        <g:message code="bestallning.tjanstekomponent"/>
    </h1>
    <table>
        <thead>
        <tr>
            <th width="15%"/>
            <th width="70%"/>
        </tr>
        </thead>
        <tbody>
        <!-- cssClass is alternatively set to even:odd based on previous item row (note: not necessarily an item may exists)  -->
        <g:set var="cssClass" value="${'even'}"/>
        <g:set var="firstRow" value="${true}"/>
        <g:set var="bestallningar" value="${bestallning.exkludera.tjanstekomponenter}"/>
        <g:each in="${bestallningar}" status="i" var="bestallning">
            <tr class="${cssClass}">
                <g:if test="${firstRow}">
                    <g:set var="firstRow" value="${false}"/>
                </g:if>
                <g:set var="report" value="${reportService.getReportData(bestallning, bestallningsData)}"/>
                <td class="${report.warning ? 'warning' : ''}">
                    ${report.status}
                </td>
                <td>${report.value}</td>
            </tr>
            <g:if test="${!firstRow}">
                <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
            </g:if>
        </g:each>
        </tbody>
    </table>

    <g:if test="${!firstRow}">
        <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
    </g:if>


    <h1>
        <g:message code="bestallning.anropsbehorighet"/>
    </h1>
    <table>
        <thead>
        <tr>
            <th width="15%"/>
            <th width="70%"/>
        </tr>
        </thead>
        <tbody>
        <!-- cssClass is alternatively set to even:odd based on previous item row (note: not necessarily an item may exists)  -->
        <g:set var="cssClass" value="${'even'}"/>
        <g:set var="firstRow" value="${true}"/>
        <g:set var="bestallningar" value="${bestallning.exkludera.anropsbehorigheter}"/>
        <g:each in="${bestallningar}" status="i" var="bestallning">
            <tr class="${cssClass}">
                <g:if test="${firstRow}">
                    <g:set var="firstRow" value="${false}"/>
                </g:if>
                <g:set var="report" value="${reportService.getReportData(bestallning, bestallningsData)}"/>
                <td class="${report.warning ? 'warning' : ''}">
                    ${report.status}
                </td>
                <td>${report.value}</td>
            </tr>
            <g:if test="${!firstRow}">
                <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
            </g:if>
        </g:each>
        </tbody>
    </table>

    <g:if test="${!firstRow}">
        <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
    </g:if>


    <h1>
        <g:message code="bestallning.vagval"/>
    </h1>
    <table>
        <thead>
        <tr>
            <th width="15%"/>
            <th width="70%"/>
        </tr>
        </thead>
        <tbody>
        <!-- cssClass is alternatively set to even:odd based on previous item row (note: not necessarily an item may exists)  -->
        <g:set var="cssClass" value="${'even'}"/>
        <g:set var="firstRow" value="${true}"/>
        <g:set var="bestallningar" value="${bestallning.exkludera.vagval}"/>
        <g:each in="${bestallningar}" status="i" var="bestallning">
            <g:if test="${firstRow}">
                <g:set var="firstRow" value="${false}"/>
            </g:if>
            <g:set var="report" value="${reportService.getReportData(bestallning, bestallningsData)}"/>
            <g:each in="${report}" status="j" var="reportElement">
                <tr class="${cssClass}">
                    <td class="${reportElement.warning ? 'warning' : ''}">
                        ${reportElement.status}
                    </td>
                    <td>${reportElement.value}</td>
                </tr>
                <g:if test="${!firstRow}">
                    <g:set var="cssClass" value="${cssClass.equals('even') ? 'odd' : 'even'}"/>
                </g:if>
            </g:each>
        </g:each>
        </tbody>
    </table>



</div>
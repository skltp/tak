<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="beställning.bekrafta.label" /></title>
    <r:require module="select2"/>
</head>
<body>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>

<table>
    <thead>
    <tr>
        <th width="15%" />
        <th width="70%"/>
    </tr>
    </thead>
    <tbody>
        <tr><td>Platform</td> <td>${bestallning.plattform}</td> </tr>
        <tr><td>Format Version</td> <td>${bestallning.version}</td> </tr>
        <tr><td>Version</td> <td>${bestallning.version}</td> </tr>
        <tr><td>Tidpunkt</td> <td>${bestallning.tidpunkt}</td> </tr>
        <tr><td>Utforare</td> <td>${bestallning.utforare}</td> </tr>
        <tr><td>Kommentar</td> <td>${bestallning.kommentar}</td> </tr>
    </tbody>
</table>
<h1>
    <g:message code="beställning.bekrafta.label" />
</h1>
<div id="publish-header-list" class="content scaffold-list" role="main">
    <table>
        <thead>
        <tr>
            <g:sortableColumn property="namn" title="${message(code: 'default.entity.label')}" width="83%"/>
            <th/>
        </tr>
        </thead>
        <g:if test="${flash.info}">
            <div class="message" role="status">
                ${flash.info}
            </div>
        </g:if>
        <tbody>
        <g:if test="${flash.message}">
            <div class="message" role="status">
                ${flash.message}
            </div>
        </g:if>
        <g:if test="${flash.error}">
            <div class="errors" role="status">
                ${flash.error}
            </div>
        </g:if>
        </tbody>
    </table>
</div>


<g:render template="createdlist" />
<g:render template="updatedlist" />
<g:render template="deletedlist" />

<g:form action="save">
    <fieldset class="buttons">
        <input name="bestallning" value="${bestallning}" type="hidden" />
        <g:actionSubmit action="spara" value="${message(code: 'beställning.spara.label', default: 'Spara')}" />
        <g:actionSubmit action="decline" value="${message(code: 'beställning.decline.label', default: 'Decline')}"/>
    </fieldset>
</g:form>

</body>
</html>
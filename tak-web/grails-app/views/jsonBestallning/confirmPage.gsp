<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="bestallning.bekrafta.label" /></title>
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
        <tr><td>Format Version</td> <td>${bestallning.formatVersion}</td> </tr>
        <tr><td>Version</td> <td>${bestallning.version}</td> </tr>
        <tr><td>BestallningsTidpunkt</td> <td>${bestallning.bestallningsTidpunkt}</td> </tr>
        <tr><td>GenomforandeTidpunkt</td> <td>${bestallning.genomforandeTidpunkt}</td> </tr>
        <tr><td>Utforare</td> <td>${bestallning.utforare}</td> </tr>
        <tr><td>Kommentar</td> <td>${bestallning.kommentar}</td> </tr>
    </tbody>
</table>
<h1 style="margin-left:1em;">
    <g:message code="bestallning.bekrafta.label" />
</h1>
<div id="publish-header-list" class="content scaffold-list" role="main">
    <table>
        <thead>
        <tr>
            <g:sortableColumn property="namn" title="${message(code: 'beställning')}" width="83%"/>
            <th/>
        </tr>
        </thead>
    </table>
</div>


<g:render template="reportData" />

<g:form>
    <fieldset class="buttons">
        <g:hiddenField name="jsonBestallningText" value="${jsonBestallningText}" />
        <g:actionSubmit action="saveOrder" class="save" value="${message(code: 'bestallning.spara.label', default: 'Spara')}" />
        <g:actionSubmit action="decline" class="save" value="${message(code: 'bestallning.decline.label', default: 'Avbryt')}"/>
    </fieldset>
</g:form>

</body>
</html>
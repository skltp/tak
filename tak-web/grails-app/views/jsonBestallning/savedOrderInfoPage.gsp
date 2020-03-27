<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <title><g:message code="bestallning.saved.label"/></title>
    <r:require module="select2"/>


</head>

<body>
<div class="nav" role="navigation">
    <ul>
        <li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
    </ul>
</div>

<h1 style="margin-left:1em;">
    <g:message code="bestallning.saved.label"/>
</h1>

<g:form onsubmit="return false;">
    <fieldset class="form">
        <g:textArea readonly="readonly"
                    style="width:75%;height: 500px;"
                    name="reportData"
                    cols="100"
                    rows="50"
                    maxSize="15000"
                    required=""
                    value="${report}"/>
    </fieldset>
    <fieldset class="buttons">
        <input type="submit" id="copyButton" value="${message(code: 'button.copy.label')}" />
    </fieldset>
</g:form>

<script type="text/javascript">
    $(document).ready(function () {
        $("#copyButton").click(function(){
            $("#reportData").select();
            document.execCommand('copy');
        });
    })
</script>
</body>
</html>
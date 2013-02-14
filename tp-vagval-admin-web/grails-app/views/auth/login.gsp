<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="layout" content="main" />
  <title><g:message code="login.title" default="Login to TP ${grailsApplication.metadata.'app.version'}" /></title>
</head>
<body OnLoad="document.signInForm.username.focus();">
  <div class="body">
  <h1 style="margin-left:20px;"><g:message code="login.title" default="Login to TP ${grailsApplication.metadata.'app.version'}" /></h1>
  <br/>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <g:form name="signInForm" action="signIn">
    <input type="hidden" name="targetUri" value="${targetUri}" />
		<fieldset class="form">
      <div class="fieldcontain">
        	<label for="username">
        		<g:message code="login.username" default="Username" />
        	</label>
        	<g:textField name="username" required="" value="${username}"/>
        </div>
        <div class="fieldcontain">
          	<label for="username">
          		<g:message code="login.password" default="Password" />
          	</label>
          	<g:passwordField name="password" required="" value=""/>
          </div>
  		</fieldset>
			<fieldset class="buttons">
				<input type="submit" value="${message(code: 'login.signIn', 'default': 'Sign in')}" />
  		</fieldset>
  </g:form>
  </div>
<script language="JavaScript">
document.signInForm.username.focus();
</script>
</body>
</html>

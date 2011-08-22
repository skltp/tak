<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="layout" content="main" />
  <title><g:message code="login.title" default="Login to TP 1.2.0" /></title>
</head>
<body OnLoad="document.signInForm.username.focus();">
  <div class="body">
  <h1 style="margin-left:20px;"><g:message code="login.title" default="Login to TP 1.2.0" /></h1>
  <br/>
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <g:form name="signInForm" action="signIn">
    <input type="hidden" name="targetUri" value="${targetUri}" />
    <table>
      <tbody>
        <tr>
          <td><g:message code="login.username" default="Username" />:</td>
          <td><input type="text" name="username" value="${username}" /></td>
        </tr>
        <tr>
          <td><g:message code="login.password" default="Password" />:</td>
          <td><input type="password" name="password" value="" /></td>
        </tr>
        <tr>
          <td />
          <td><input type="submit" value="${message(code: 'login.signIn', 'default': 'Sign in')}" /></td>
        </tr>
      </tbody>
    </table>
  </g:form>
  </div>
<script language="JavaScript">
document.signInForm.username.focus();
</script>
</body>
</html>

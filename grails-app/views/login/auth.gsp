<html>
<head>
    <meta name='layout' content='main'/>
    <title><g:message code="springSecurity.login.title"/></title>
</head>

<body>
<div class="row">
    <div class="span5 offset4">
        <h1><g:message code="springSecurity.login.header"/></h1>

        <form class="well" action='${postUrl}' method='POST' id='loginForm' autocomplete='off'>
            <g:if test='${flash.message}'>
                <div class="alert alert-error" data-alert="alert">
                    <a class="close" data-dismiss="alert" href="#">&times;</a>

                    <p>${flash.message}</p></div>
            </g:if>
            <fieldset>
                <label for='username'><g:message code="springSecurity.login.username.label"/>:</label>
                <input class="span4" type='text' name='j_username' id='username'/>
                <label for='password'><g:message code="springSecurity.login.password.label"/>:</label>
                <input class="span4" type='password' name='j_password' id='password'/>

                <label class="checkbox">
                    <input type='checkbox' name='${rememberMeParameter}' id='remember_me'
                           <g:if test='${hasCookie}'>checked='checked'</g:if>/>
                    <g:message code="springSecurity.login.remember.me.label"/>
                </label>

                <input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}'
                       class="btn btn-large btn-primary"/>
            </fieldset>
        </form>
    </div>
</div>
<r:script>
    $(function () {
        $(".alert-message").alert()
    });
</r:script>
</body>
</html>

<html>
<head>
    <meta name='layout' content='main'/>
    <title><g:message code="springSecurity.login.title"/></title>
</head>

<body>
<div class="row show-grid">
    <div class="well span8 offset4">
        <h1><g:message code="springSecurity.login.header"/></h1>

        <form action='${postUrl}' method='POST' id='loginForm' autocomplete='off'>
            <g:if test='${flash.message}'>
                <div class="alert-message error" data-alert="alert">
                    <a class="close" href="#">&times;</a>

                    <p>${flash.message}</p></div>
            </g:if>
            <fieldset>

                <div class="clearfix">
                    <label for='username'><g:message code="springSecurity.login.username.label"/>:</label>

                    <div class="input">
                        <input type='text' name='j_username' id='username'/>
                    </div>
                </div>

                <div class="clearfix">
                    <label for='password'><g:message code="springSecurity.login.password.label"/>:</label>

                    <div class="input">
                        <input type='password' name='j_password' id='password'/>
                    </div>
                </div>

                <div class="clearfix">

                    %{--<label for='remember_me'><g:message code="springSecurity.login.remember.me.label"/></label>--}%

                    <div class="input">
                        <ul class="inputs-list">
                            <label>
                                <input type='checkbox' name='${rememberMeParameter}' id='remember_me'
                                       <g:if test='${hasCookie}'>checked='checked'</g:if>/>
                                <span><g:message code="springSecurity.login.remember.me.label"/></span>
                            </label>
                        </ul>
                    </div>
                </div>

                <div class="clearfix">
                    <div class="input">
                        <input type='submit' id="submit" value='${message(code: "springSecurity.login.button")}'
                               class="btn primary"/>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>
<r:require module="bootstrap_alerts"/>
<r:script>
    $(function () {
        $(".alert-message").alert()
    });
</r:script>
</body>
</html>

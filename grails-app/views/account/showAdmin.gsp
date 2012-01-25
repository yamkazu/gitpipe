<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<h1>Account Settings</h1>

<ul class="tabs">
    <li>
        <g:link controller="account">Account Admin</g:link>
    </li>
    <li class="active">
        <g:link controller="account" action="admin">Account Admin</g:link>
    </li>
    <li>
        <g:link controller="account" action="ssh">SSH Public Keys</g:link>
    </li>
</ul>

<section>
    <h3>Change your password</h3>

    <g:if test="${flash.passwordMessage}">
        <div class="alert-message success">
            <a class="close" href="#">×</a>

            <p>${flash.passwordMessage}</p>
        </div>
    </g:if>

    <div class="row show-grid">
        <div class="span16">
            <form action="${createLink(mapping: 'updatePassword')}" method="post">
                <fieldset>
                    <div class="clearfix ${hasErrors(bean: user, field: 'password', 'error')}">
                        <label for="password">Password</label>

                        <div class="input">
                            <g:passwordField name="password" size="30"/>
                            <g:if test="${hasErrors(bean: user, field: 'password', 'true')}">
                                <span class="help-inline">${fieldError(bean: user, field: 'password')}</span>
                            </g:if>
                        </div>
                    </div>

                    <div class="clearfix">
                        <label for="passwordConfirmation">Confirm Password</label>

                        <div class="input">
                            <g:passwordField name="passwordConfirmation" size="30"/>
                        </div>
                    </div>

                    <div class="clearfix">
                        <div class="input">
                            <g:submitButton name="update-button" value="Change Password"
                                            class="btn primary"/>
                        </div>
                    </div>
                </fieldset>
            </form>
        </div>
    </div>
</section>
<r:require module="bootstrap_alerts"/>
<r:script>
    $(function () {
        $(".alert-message").alert()
    });
</r:script>
</body>
</html>
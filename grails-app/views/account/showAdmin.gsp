<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<h1>Account Settings</h1>

<div class="tabbable tabs-left">

    <ul class="nav nav-tabs">
        <li>
            <g:link controller="account">Profile</g:link>
        </li>
        <li class="active">
            <g:link controller="account" action="admin">Account Admin</g:link>
        </li>
        <li>
            <g:link controller="account" action="ssh">SSH Public Keys</g:link>
        </li>
    </ul>

    <div class="tab-content span5">
        <h3>Change your password</h3>

        <g:if test="${flash.passwordMessage}">
            <div class="alert alert-success" data-alert="alert">
                <a class="close" data-dismiss="alert" href="#">&times;</a>
                ${flash.passwordMessage}
            </div>
        </g:if>

        <form action="${createLink(mapping: 'updatePassword')}" method="post">
            <fieldset>
                <div class="control-group ${hasErrors(bean: user, field: 'password', 'error')}">
                    <label for="password">Password</label>

                    <div class="controls">
                        <g:passwordField name="password" class="span4"/>
                        <g:if test="${hasErrors(bean: user, field: 'password', 'true')}">
                            <span class="help-inline">${fieldError(bean: user, field: 'password')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="control-group">
                    <label for="passwordConfirmation">Confirm Password</label>

                    <div class="controls">
                        <g:passwordField name="passwordConfirmation" class="span4"/>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <g:submitButton name="update-button" value="Change Password"
                                        class="btn btn-primary"/>
                    </div>
                </div>
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
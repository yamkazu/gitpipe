<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<h1>Account Settings</h1>

<div id="tabs" class="ui-tabs ui-widget ui-widget-content ui-corner-all">
    <ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
        <li class="ui-state-default ui-corner-top">
            <g:link controller="account">Account Admin</g:link>
        </li>
        <li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-active">
            <g:link controller="account" action="admin">Account Admin</g:link>
        </li>
        <li class="ui-state-default ui-corner-top">
            <g:link controller="account" action="ssh">SSH Public Keys</g:link>
        </li>
    </ul>
</div>

<div id="change-password">
    <h3>Change your password</h3>
    %{--<g:form url="[action: 'updatePassword', controller: 'account']">--}%
    <g:if test="${flash.passwordMessage}">
        <div class="form-message success-message">
            <p>${flash.passwordMessage}</p>
        </div>
    </g:if>
    <form action="${request.contextPath}/account/admin/password" method="post">
        <div class="fields-set">
            <div class="fields">
                <dl class="form">
                    <dt><label for="password">Password</label></dt>
                    <dd><g:passwordField name="password" size="30"/>
                        <g:if test="${hasErrors(bean: user, field: 'password', 'true')}">
                            <p class="errors">${fieldError(bean: user, field: 'password')}</p>
                        </g:if>
                    </dd>
                </dl>
                <dl class="form">
                    <dt><label for="passwordConfirmation">Confirm Password</label></dt>
                    <dd><g:passwordField name="passwordConfirmation" size="30"/></dd>
                </dl>
            </div>

            <div class="field-actions">
                <g:submitButton name="update-button" value="Change Password"
                                class="ui-button-success"/>
            </div>
        </div>
    %{--</g:form>--}%
    </form>
</div>

<g:javascript>
    (function ($) {
        $('#update-button').button();
    })(jQuery);
</g:javascript>
</body>
</html>
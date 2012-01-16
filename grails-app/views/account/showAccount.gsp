<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<h1>Account Settings</h1>

<div id="tabs" class="ui-tabs ui-widget ui-widget-content ui-corner-all">
    <ul class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
        <li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-active">
            <g:link controller="account">Account Admin</g:link>
        </li>
        <li class="ui-state-default ui-corner-top">
            <g:link controller="account" action="admin">Account Admin</g:link>
        </li>
        <li class="ui-state-default ui-corner-top">
            <g:link controller="account" action="ssh">SSH Public Keys</g:link>
        </li>
    </ul>
</div>

<div id="profile">
    <g:if test="${flash.message}">
        <div class="form-message success-message">
            <p>${flash.message}</p>
        </div>
    </g:if>
    <g:form controller="account">
        <div class="fields-set">
            <div class="fields">
                <dl class="form">
                    <dt><label for="name">Name</label></dt>
                    <dd>
                        <g:textField name="name" size="30"
                                     value="${fieldValue(bean: user, field: 'name')}"/>
                        <g:if test="${hasErrors(bean: user, field: 'name', 'true')}">
                            <p class="errors">${fieldError(bean: user, field: 'name')}</p>
                        </g:if>
                    </dd>
                </dl>
                <dl class="form">
                    <dt><label for="email">Email Address</label></dt>
                    <dd><g:textField name="email" size="30"
                                     value="${fieldValue(bean: user, field: 'email')}"/>
                        <g:if test="${hasErrors(bean: user, field: 'email', 'true')}">
                            <p class="errors">${fieldError(bean: user, field: 'email')}</p>
                        </g:if>
                    </dd>
                </dl>
                <dl class="form">
                    <dt><label for="location">Location</label></dt>
                    <dd><g:textField name="location" size="30"
                                     value="${fieldValue(bean: user, field: 'location')}"/>
                        <g:if test="${hasErrors(bean: user, field: 'location', 'true')}">
                            <p class="errors">${fieldError(bean: user, field: 'location')}</p>
                        </g:if>
                    </dd>
                </dl>
            </div>

            <div class="field-actions">
                <g:submitButton name="update-button" value="Update information"
                                class="ui-button-success"/>
            </div>
        </div>
    </g:form>
</div>

<g:javascript>
    (function ($) {
        $('#update-button').button();
    })(jQuery);
</g:javascript>
</body>
</html>
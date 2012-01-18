<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<h1>Account Settings</h1>

<ul class="tabs">
    <li class="active">
        <g:link controller="account">Account Admin</g:link>
    </li>
    <li>
        <g:link controller="account" action="admin">Account Admin</g:link>
    </li>
    <li>
        <g:link controller="account" action="ssh">SSH Public Keys</g:link>
    </li>
</ul>

<g:if test="${flash.message}">
    <div class="alert-message success">
        <a class="close" href="#">×</a>

        <p>${flash.message}</p>
    </div>
</g:if>

<div class="row show-grid">
    <div class="span16">
        <g:form controller="account">
            <fieldset>
                <div class="clearfix ${hasErrors(bean: user, field: 'name', 'error')}">
                    <label for="name">Name</label>

                    <div class="input">
                        <g:textField name="name" size="30"
                                     value="${fieldValue(bean: user, field: 'name')}"/>
                        <g:if test="${hasErrors(bean: user, field: 'name', 'true')}">
                            <span class="help-inline">${fieldError(bean: user, field: 'name')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="clearfix ${hasErrors(bean: user, field: 'email', 'error')}">
                    <label for="email">Email Address</label>

                    <div class="input">
                        <g:textField name="email" size="30"
                                     value="${fieldValue(bean: user, field: 'email')}"/>
                        <g:if test="${hasErrors(bean: user, field: 'email', 'true')}">
                            <span class="help-inline">${fieldError(bean: user, field: 'email')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="clearfix ">
                    <label for="location">Location</label>

                    <div class="input ${hasErrors(bean: user, field: 'location', 'error')}">
                        <g:textField name="location" size="30"
                                     value="${fieldValue(bean: user, field: 'location')}"/>
                        <g:if test="${hasErrors(bean: user, field: 'location', 'true')}">
                            <span class="help-inline">${fieldError(bean: user, field: 'location')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="clearfix">
                    <div class="input">
                        <g:submitButton name="update-button" value="Update information"
                                        class="btn primary"/>
                    </div>
                </div>
            </fieldset>
        </g:form>
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
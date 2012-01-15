<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Sign up for gitpipe - gitpipe</title>
</head>

<body>
<h1>Sign up for gitpipe</h1>

<div>
    <h2>Create your personal account</h2>

    <div class="span10">
        <g:form controller="signup">
            <div class="fields-set">
                <div class="fields">
                    <dl class="form">
                        <dt><label for="username">Username</label></dt>
                        <dd>
                            <g:textField name="username" size="30"
                                         value="${fieldValue(bean: user, field: 'username')}"/>
                            <g:if test="${hasErrors(bean: user, field: 'username', 'true')}">
                                <p class="errors">${fieldError(bean: user, field: 'username')}</p>
                            </g:if>
                        </dd>
                    </dl>
                    <dl class="form">
                        <dt><label for="email">Email Address</label></dt>
                        <dd><g:textField name="email" size="30" value="${fieldValue(bean: user, field: 'email')}"/>
                            <g:if test="${hasErrors(bean: user, field: 'email', 'true')}">
                                <p class="errors">${fieldError(bean: user, field: 'email')}</p>
                            </g:if>
                        </dd>
                    </dl>
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
                    <g:submitButton name="signup_button" value="Create an account" class="ui-button-success"/>
                </div>
            </div>
        </g:form>
    </div>
    <g:javascript>
        (function ($) {
            $('#signup_button').button();
        })(jQuery);
    </g:javascript>
</div>
</body>
</html>
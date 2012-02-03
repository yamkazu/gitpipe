<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Sign up for gitpipe - gitpipe</title>
</head>

<body>

<h1>Sign up for gitpipe</h1>

<div class="row show-grid">
    <div class="span16">
        <g:form controller="signup">
            <fieldset>
                <div class="control-group ${hasErrors(bean: user, field: 'username', 'error')}">
                    <label for="username">Username</label>

                    <div class="controls">
                        <g:textField name="username" size="30"
                                     value="${fieldValue(bean: user, field: 'username')}" class="span5"/>
                        <g:if test="${hasErrors(bean: user, field: 'username', 'true')}">
                            <span class="help-inline">${fieldError(bean: user, field: 'username')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="control-group ${hasErrors(bean: user, field: 'email', 'error')}">
                    <label for="email">Email Address</label>

                    <div class="controls">
                        <g:textField name="email" size="30" value="${fieldValue(bean: user, field: 'email')}"
                                     class="span5"/>
                        <g:if test="${hasErrors(bean: user, field: 'email', 'true')}">
                            <span class="help-inline">${fieldError(bean: user, field: 'email')}<span>
                        </g:if>
                    </div>
                </div>

                <div class="control-group ${hasErrors(bean: user, field: 'password', 'error')}">
                    <label for="password">Password</label>

                    <div class="controls">
                        <g:passwordField name="password" size="30" class="span5"/>
                        <g:if test="${hasErrors(bean: user, field: 'password', 'true')}">
                            <span class="help-inline">${fieldError(bean: user, field: 'password')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="control-group">
                    <label for="passwordConfirmation">Confirm Password</label>

                    <div class="controls">
                        <g:passwordField name="passwordConfirmation" size="30" class="span5 "/>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <g:submitButton name="signup_button" value="Create an account" class="btn primary"/>
                    </div>
                </div>
            </fieldset>
        </g:form>
    </div>
</div>
</body>
</html>
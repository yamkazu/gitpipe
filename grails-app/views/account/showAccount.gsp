<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<h1>Account Settings</h1>

<div class="tabbable tabs-left">

    <g:render template="tabs" model="[active: 'profile']" />

    <div class="tab-content span5">
        <g:if test="${flash.message}">
            <div class="alert alert-success" data-alert="alert">
                <a class="close" data-dismiss="alert" href="#">&times;</a>
                ${flash.message}
            </div>
        </g:if>
        <g:form mapping="account">
            <fieldset>
                <div class="control-group ${hasErrors(bean: user, field: 'name', 'error')}">
                    <label for="name">Name</label>

                    <div class="controls">
                        <g:textField name="name" class="span4"
                                     value="${fieldValue(bean: user, field: 'name')}"/>
                        <g:if test="${hasErrors(bean: user, field: 'name', 'true')}">
                            <span class="help-inline">${fieldError(bean: user, field: 'name')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="control-group ${hasErrors(bean: user, field: 'email', 'error')}">
                    <label for="email">Email Address</label>

                    <div class="controls">
                        <g:textField name="email" class="span4"
                                     value="${fieldValue(bean: user, field: 'email')}"/>
                        <g:if test="${hasErrors(bean: user, field: 'email', 'true')}">
                            <span class="help-inline">${fieldError(bean: user, field: 'email')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="control-group">
                    <label for="location">Location</label>

                    <div class="controls ${hasErrors(bean: user, field: 'location', 'error')}">
                        <g:textField name="location" class="span4"
                                     value="${fieldValue(bean: user, field: 'location')}"/>
                        <g:if test="${hasErrors(bean: user, field: 'location', 'true')}">
                            <span class="help-inline">${fieldError(bean: user, field: 'location')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <g:submitButton name="update-button" value="Update information"
                                        class="btn btn-primary"/>
                    </div>
                </div>
            </fieldset>
        </g:form>
    </div>

</div>


<r:script>
    $(function () {
        $(".alert-message").alert()
    });
</r:script>
</body>
</html>
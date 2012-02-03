<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Create a New Repository - gitpipe</title>
</head>

<body>

<h1>Create a New Repository</h1>

<div class="row">
    <div class="span16">
        <g:form controller="project">
            <fieldset>
                <div class="control-group ${hasErrors(bean: project, field: 'name', 'error')}">
                    <label for="name">Project Name</label>

                    <div class="controls">
                        <g:textField name="name" value="${fieldValue(bean: project, field: 'name')}"/>
                        <g:if test="${hasErrors(bean: project, field: 'name', 'true')}">
                            <span class="help-inline">${fieldError(bean: project, field: 'name')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="control-group ${hasErrors(bean: project, field: 'description', 'error')}">
                    <label for="description">Description</label>

                    <div class="controls">
                        <g:textField name="description" size="30"
                                     value="${fieldValue(bean: project, field: 'description')}"/>
                        <g:if test="${hasErrors(bean: project, field: 'description', 'true')}">
                            <span class="help-inline">${fieldError(bean: project, field: 'description')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="control-group">
                    <div class="controls">
                        <g:submitButton name="signup_button" value="Create repository" class="btn"/>
                    </div>
                </div>
            </fieldset>
        </g:form>
    </div>
</div>

</body>
</html>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Create a New Repository - gitpipe</title>
</head>

<body>

<h1>Create a New Repository</h1>

<div class="row show-grid">
    <div class="span16">
        <g:form controller="project">
            <fieldset>
                <div class="clearfix ${hasErrors(bean: repositoryInfo, field: 'name', 'error')}">
                    <label for="projectName">Project Name</label>

                    <div class="input">
                        <g:textField name="projectName" size="30"
                                     value="${fieldValue(bean: repositoryInfo, field: 'name')}"/>
                        <g:if test="${hasErrors(bean: repositoryInfo, field: 'name', 'true')}">
                            <span class="help-inline">${fieldError(bean: repositoryInfo, field: 'name')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="clearfix ${hasErrors(bean: repositoryInfo, field: 'description', 'error')}">
                    <label for="description">Description</label>

                    <div class="input">
                        <g:textField name="description" size="30"
                                     value="${fieldValue(bean: repositoryInfo, field: 'description')}"/>
                        <g:if test="${hasErrors(bean: repositoryInfo, field: 'description', 'true')}">
                            <span class="help-inline">${fieldError(bean: repositoryInfo, field: 'description')}</span>
                        </g:if>
                    </div>
                </div>

                <div class="clearfix">
                    <div class="input">
                        <g:submitButton name="signup_button" value="Create repository" class="btn primary"/>
                    </div>
                </div>
            </fieldset>
        </g:form>
    </div>
</div>

</body>
</html>
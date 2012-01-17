<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Create a New Repository - gitpipe</title>
</head>

<body>
<h1>Create a New Repository</h1>

<div>
    <g:form controller="repository">
        <div class="fields-set">
            <div class="fields">
                <dl class="form">
                    <dt><label for="projectName">Project Name</label></dt>
                    <dd>
                        <g:textField name="projectName" size="30"
                                     value="${fieldValue(bean: repositoryInfo, field: 'projectName')}"/>
                        <g:if test="${hasErrors(bean: repositoryInfo, field: 'projectName', 'true')}">
                            <p class="errors">${fieldError(bean: repositoryInfo, field: 'projectName')}</p>
                        </g:if>
                    </dd>
                </dl>
                <dl class="form">
                    <dt><label for="description">Description</label></dt>
                    <dd>
                        <g:textField name="description" size="30"
                                     value="${fieldValue(bean: repositoryInfo, field: 'description')}"/>
                        <g:if test="${hasErrors(bean: repositoryInfo, field: 'description', 'true')}">
                            <p class="errors">${fieldError(bean: repositoryInfo, field: 'description')}</p>
                        </g:if>
                    </dd>
                </dl>
            </div>

            <div class="field-actions">
                <g:submitButton name="signup_button" value="Create repository" class="ui-button-success"/>
            </div>
        </div>
    </g:form>
</div>
<g:javascript>
    (function ($) {
        $('#signup_button').button();
    })(jQuery);
</g:javascript>
</body>
</html>
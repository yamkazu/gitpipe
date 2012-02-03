<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

<g:render template="header" model="[user: user, project: project]"/>
<g:render template="tabs" model="[user: user, project: project, ref: id, active: 'commits']"/>
<g:render template="lastCommit" model="[user: user, project: project, ref: id, commit: commit]"/>

<div id="slider"></div>

<r:require module="gitpipe_commitviewer"/>
<g:javascript>
    $(function () {
        $('#slider').getCommit("${createLink(mapping: 'repository_commit', params: [username: user.username, project: project.name, id: id])}");
    });
</g:javascript>

</body>
</html>
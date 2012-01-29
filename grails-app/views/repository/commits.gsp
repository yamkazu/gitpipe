<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

<g:render template="header" model="[user: user, project: project]"/>
<g:render template="tabs" model="[user: user, project: project, ref: ref, active: 'commits']"/>
<g:render template="current" model="[ref: ref]"/>

<h3>Commit History</h3>

<div id="commits"></div>

<r:require module="gitpipe_commitsviewer"/>
<g:javascript>
    $(function () {
        $('#commits').getCommits("${createLink(mapping: 'repository_commits', params: [username: user.username, project: project.name, ref: ref, path: path])}");
    });
</g:javascript>

</body>
</html>
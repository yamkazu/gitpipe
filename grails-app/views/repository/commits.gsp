<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

<g:render template="header" model="[user: user, repository: repository]"/>
<g:render template="tabs" model="[user: user, repository: repository, ref: ref, active: 'commits']"/>
<g:render template="current" model="[ref: ref]" />

<h3>Commit History</h3>

<div id="commits"></div>

<r:require module="gitpipe_commitsviewer"/>
<g:javascript>
    $(function () {
        $('#commits').getCommits({
            url: "${createLink(mapping: 'project', params: ['username': user.username, 'project': repository.projectName])}",
            ref: "${ref}",
            path: "${path}"
        });
    });
</g:javascript>

</body>
</html>
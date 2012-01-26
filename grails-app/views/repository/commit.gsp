<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

%{--<g:render template="header" model="[user: user, repository: repository]"/>--}%
%{--<g:render template="tabs" model="[user: user, repository: repository, ref: ref, active: 'commits']"/>--}%

<h3>Commit</h3>

<div id="commit"></div>

<r:require module="gitpipe_commitviewer"/>
<g:javascript>
    $(function () {
        $('#commit').getCommit({
            url: "${createLink(mapping: 'project', params: ['username': user.username, 'project': repository.projectName])}",
            id: "${id}"
        });
    });
</g:javascript>

</body>
</html>
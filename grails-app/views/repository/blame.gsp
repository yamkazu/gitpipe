<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

<g:render template="header" model="[user: user, project: project]"/>
<g:render template="tabs" model="[user: user, project: project, ref: ref, active: 'files']"/>
<g:render template="lastCommit" model="[user: user, project: project, ref: ref, commit: commit]" />

<div id="slider"></div>

<r:require module="gitpipe_treeviewer" />
<g:javascript>
    $(function () {
        $('#slider').gitBlame("${createLink(mapping: 'repository_blame', params: [username: user.username, project: project.name, ref: ref, path: path])}");
    });
</g:javascript>

</body>
</html>
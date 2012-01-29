<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

<g:render template="/repository/header" model="[user: user, project: project]"/>
<g:render template="/repository/tabs" model="[user: user, project: project, ref: ref, active: 'files']"/>
<g:render template="/repository/current" model="[ref: ref]" />
<g:render template="/repository/lastCommit" model="[user: user, project: project, ref: ref, commit: commit]" />

<div id="tree"></div>

<r:require module="gitpipe_treeviewer"/>
<g:javascript>
    $(function () {
        $('#tree').gitTree("${createLink(mapping: 'repository_tree', params: [username: user.username, project: project.name, ref: ref, path: path])}");
    });
</g:javascript>

</body>
</html>
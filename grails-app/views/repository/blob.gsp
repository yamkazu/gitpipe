<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

<g:render template="header" model="[user: user, project: project]"/>
<g:render template="tabs" model="[user: user, project: project, ref: ref, active: 'files']"/>

<h2>current <span class="label notice large">${ref}</span></h2>

<g:render template="lastCommit" model="[user: user, project: project, ref: ref, commit: commit]" />

<div id="tree"></div>

<r:require module="gitpipe_treeviewer" />
<g:javascript>
    $(function () {
        $('#tree').gitBlob({
            rootName: "${project.name}",
            url: "${createLink(mapping: 'project', params: ['username': user.username, 'project': project.name])}",
            ref: "${ref}",
            path: "${path}"
        });
    });
</g:javascript>

</body>
</html>
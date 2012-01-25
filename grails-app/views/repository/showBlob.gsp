<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

<h1>
    <a href="${createLink(mapping: 'user', params: [username: user.username])}">${user.username}</a>
    <em>/</em>
    <a href="${createLink(mapping: 'project', params: [username: user.username, project: repository.projectName])}">${repository.projectName}</a>
</h1>

<div id="tree"></div>

<r:require module="gitpipe_treeviewer" />
<g:javascript>
    $(function () {
        $('#tree').gitBlob({
            rootName: "${repository.projectName}",
            url: "${createLink(mapping: 'project', params: ['username': user.username, 'project': repository.projectName])}",
            ref: "${ref}",
            path: "${path}"
        });
    });
</g:javascript>

</body>
</html>
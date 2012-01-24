<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

<h1>
    <a href="${request.contextPath}/${user.username}">${user.username}</a>
    <em>/</em>
    <a href="${request.contextPath}/${user.username}/${repository.projectName}">${repository.projectName}</a>
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
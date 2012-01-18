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

<g:javascript src="git-tree.js" />
<g:javascript>
    $(function () {
        $('#tree').gitTree("${request.contextPath}/${user.username}", "master");
    });
</g:javascript>

</body>
</html>
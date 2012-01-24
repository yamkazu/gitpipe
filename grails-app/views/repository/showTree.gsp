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

<div>
    <p>${commit.authorIdent.name}</p>
    <p>${commit.shortMessage}</p>
    <p>${commit.id.name}</p>
    <p>${new Date(commit.commitTime * 1000L)}</p>
</div>

<div id="tree"></div>

<r:require module="gitpipe_treeviewer" />
<g:javascript>
    $(function () {
        $('#tree').gitTree({
            rootName: "${repository.projectName}",
            url: "${createLink(mapping: 'project', params: ['username': user.username, 'project': repository.projectName])}",
            ref: "${ref}",
            path: "${path}"
        });
    });
</g:javascript>

</body>
</html>
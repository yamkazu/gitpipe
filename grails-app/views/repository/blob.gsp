<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

<g:render template="header" model="[user: user, repository: repository]"/>
<g:render template="tabs" model="[user: user, repository: repository, ref: ref, active: 'files']"/>

<h2>current <span class="label notice large">${ref}</span></h2>

<h3><small>Latest commit to the <strong>${ref}</strong> branch</small></h3>

<div class="commit">
    <time>${new Date(commit.commitTime * 1000L)}</time>

    <div class="meta">
        <p class="message">${commit.shortMessage}</p>
        <br>

        <p class="author">${commit.authorIdent.name}</p>

        <p class="pull-right id">${commit.id.name}</p>
    </div>
</div>

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
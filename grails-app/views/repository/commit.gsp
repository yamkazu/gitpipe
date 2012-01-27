<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

%{--<g:render template="header" model="[user: user, repository: repository]"/>--}%
%{--<g:render template="tabs" model="[user: user, repository: repository, ref: ref, active: 'commits']"/>--}%
<g:render template="header" model="[user: user, repository: repository]"/>
<g:render template="tabs" model="[user: user, repository: repository, ref: commit.id.name(), active: 'commits']"/>

<div class="commit">
    <time>${new Date(commit.commitTime * 1000L)}</time>

    <div class="meta">
        <p class="message">${commit.shortMessage}</p>
        <br>

        <p class="author">${commit.authorIdent.name}</p>
        <p class="pull-right id">${commit.id.name}</p>
    </div>
</div>
%{--<g:render template="current" model="[ref: commit.id.name()]" />--}%

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
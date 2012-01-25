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

<ul class="tabs">
    <li class="active"><a>Files</a></li>
    <li class="dropdown" data-dropdown="dropdown">
        <a href="#" class="dropdown-toggle">Branches</a>
        <ul class="dropdown-menu">
            <g:each var="b" in="${repository.repository().branches}">
                <li><a href="${createLink(mapping: 'repository_tree', params: [username: user.username, project: repository.projectName, ref: b.key, path: ""])}">${b.key}</a></li>
            </g:each>
        </ul>
    </li>
    <li class="dropdown" data-dropdown="dropdown">
        <a href="#" class="dropdown-toggle">Tags</a>
        <ul class="dropdown-menu">
            <g:each var="t" in="${repository.repository().tags}">
                <li><a>${t.key}</a></li>
            </g:each>
        </ul>
    </li>
</ul>

<h3><small>Latest commit to the ${ref} branch</small></h3>

<div class="commit">
    <h4>${commit.shortMessage}</h4>

    <div class="meta">
        <p class="author">${commit.authorIdent.name}</p>
        <time>${new Date(commit.commitTime * 1000L)}</time>

        <p class="pull-right id">${commit.id.name}</p>
    </div>
</div>

<div id="tree"></div>

<r:require module="gitpipe_treeviewer"/>
<r:require module="bootstrap_dropdown"/>
<g:javascript>
    $(function () {
        $().dropdown();
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
<ul class="tabs">
    <li class="${active == 'files' ? 'active' : ''}"><a href="${createLink(mapping: 'repository_tree', params:[username: user.username, project: repository.projectName, ref: ref, path: ''])}">Files</a></li>
    <li class="${active == 'commits' ? 'active' : ''}"><a href="${createLink(mapping: 'repository_commits', params: [username: user.username, project: repository.projectName, ref: ref])}">Commits</a></li>
    <li class="dropdown" data-dropdown="dropdown">
        <a href="#" class="dropdown-toggle">Branches</a>
        <ul class="dropdown-menu">
            <g:each var="b" in="${repository.repository().branches}">
                <li><a href="${createLink(mapping: 'repository_tree', params: [username: user.username, project: repository.projectName, ref: b.key, path: ""])}">${b.key}</a>
                </li>
            </g:each>
        </ul>
    </li>
    <li class="dropdown" data-dropdown="dropdown">
        <a href="#" class="dropdown-toggle">Tags</a>
        <ul class="dropdown-menu">
            <g:each var="t" in="${repository.repository().tags}">
                <li><a href="${createLink(mapping: 'repository_tree', params: [username: user.username, project: repository.projectName, ref: t.key, path: ""])}">${t.key}</a>
                </li>
            </g:each>
        </ul>
    </li>
</ul>
<r:require module="bootstrap_dropdown"/>
<g:javascript>
    $(function () {
        $().dropdown();
    });
</g:javascript>
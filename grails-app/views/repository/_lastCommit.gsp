<p class="last-commit"><i class="icon-time"></i>Latest commit to the <strong>${ref}</strong> branch</p>

<div class="commit">
    <div class="commit-title">
        <span>${commit.message}</span>
    </div>

    <div class="commit-meta">
        <g:if test="${commitUser}">
            <g:link class="author" mapping="user" params="[username: commitUser.username]">${commitUser.username}</g:link>
        </g:if>
        <g:else>
            <span class="author">${commit.author.name}</span>
        </g:else>
        authored
        <time>${commit.date}</time>
        <g:link class="pull-right sha-block" mapping="repository_commit"
                params="[username: user.username, project: project.name, id: commit.id]">
            commit
            <span class="sha">${commit.id.substring(0, 10)}</span>
        </g:link>
    </div>
</div>

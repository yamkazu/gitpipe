<h3><small>Latest commit to the <strong>${ref}</strong></small></h3>
<div class="commit">
    <time>${commit.date}</time>

    <div class="meta">
        <p class="message">${commit.message}</p>
        <br>

        <g:if test="${commitUser}">
            <g:link mapping="user" params="[username: commitUser.username]">${commitUser.username}</g:link>
        </g:if>
        <g:else>
            <p class="author">${commit.author.name}</p>
        </g:else>

        <p class="pull-right id"><g:link mapping="repository_commit" params="[username: user.username, project: project.name, id: commit.id]">${commit.id.substring(0, 10)}</g:link></p>
    </div>
</div>

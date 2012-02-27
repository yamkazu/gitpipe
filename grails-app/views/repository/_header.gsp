<div class="pagehead">
    <div class="title-actions-bar">
        <h1>
            <a href="${createLink(mapping: 'user', params: [username: user.username])}">${user.username}</a>
            /
            <strong>
                <a href="${createLink(mapping: 'project', params: [username: user.username, project: project.name])}">${project.name}</a>
            </strong>
        </h1>

        <div class="pull-right">
            <g:forkEnable user="${user}" project="${project}">
                <a class="btn"
                   href="${createLink(mapping: 'project_fork', params: [username: user.username, project: project.name])}">Fork</a>
            </g:forkEnable>
        </div>
    </div>
</div>
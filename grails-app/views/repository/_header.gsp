<h1>
    <a href="${createLink(mapping: 'user', params: [username: user.username])}">${user.username}</a>
    <em>/</em>
    <a href="${createLink(mapping: 'project', params: [username: user.username, project: project.name])}">${project.name}</a>
</h1>
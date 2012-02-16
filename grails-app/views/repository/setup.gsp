<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

<g:render template="/repository/header" model="[user: user, project: project]"/>

<h2>Global setup:</h2>

<pre>
Set up git
git config --global user.name "${user.name}"
git config --global user.email ${user.email}
</pre>


<h2>Next steps:</h2>

<pre>
mkdir Note
cd Note
git init
touch README
git add README
git commit -m 'first commit'
git remote add origin ${System.properties['user.name']}@${request.serverName}:${user.username}/${project.repositoryName()}
git push -u origin master
</pre>


<h2>Existing Git Repo?</h2>

<pre>
cd existing_git_repo
git remote add origin ${System.properties['user.name']}@${request.serverName}:${user.username}/${project.repositoryName()}
git push -u origin master
</pre>

</body>
</html>
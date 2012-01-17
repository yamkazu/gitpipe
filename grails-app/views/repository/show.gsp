<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<h1>
    <a href="${request.contextPath}/${user.username}">${user.username}</a>
    <em>/</em>
    <a href="${request.contextPath}/${user.username}/${repository.projectName}">${repository.projectName}</a>
</h1>
</body>
</html>
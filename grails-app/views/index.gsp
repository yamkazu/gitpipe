<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>
<h1>Hello gitpipe</h1>

<div>
    <sec:ifNotLoggedIn>
        <p>
            <g:link controller="signup" action="show" class="btn large primary">Sing up Now</g:link>
        </p>
    </sec:ifNotLoggedIn>
</div>

</body>
</html>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
</head>

<body>

<div class="hero-unit">
    <h1>Hello gitpipe!</h1>
    <p></p>
    <sec:ifNotLoggedIn>
        <p>
            <g:link controller="signup" action="show" class="btn primary large">Sing up Now »</g:link>
        </p>
    </sec:ifNotLoggedIn>
</div>

</body>
</html>
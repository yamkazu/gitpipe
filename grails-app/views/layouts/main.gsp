<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="utf-8">
    <title><g:layoutTitle default="gitpipe"/></title>
    <meta name="author" content="Kazuki YAMAMOTO">
    <r:require modules="gitpipe"/>
    <r:layoutResources/>
    <g:layoutHead/>
</head>

<body>
<div class="navbar navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <a class="brand" href="${createLink(mapping: 'dashboard')}">gitpipe</a>

            <div class="nav-collapse">
                <p class="navbar-text pull-right">
                    <sec:ifLoggedIn>
                        <a href="${createLink(mapping: 'user', params: [username: sec.username()])}">${sec.username()}</a>
                        <g:link controller="logout"><g:message code="logout.label"/></g:link>
                    </sec:ifLoggedIn>
                    <sec:ifNotLoggedIn>
                        <g:link controller='login' action='auth'><g:message code="login.label"/></g:link>
                    </sec:ifNotLoggedIn>
                </p>
            </div>
        </div>
    </div>
</div>

<div class="container">
    <g:layoutBody/>
</div> <!-- /container -->

<footer>
    <div class="container">
        <p>gitpipe &copy; Kazuki YAMAMOTO 2011.</p>
    </div>
</footer>

<r:layoutResources/>
</body>
</html>

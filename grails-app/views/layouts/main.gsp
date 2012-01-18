<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="utf-8">
    <title><g:layoutTitle default="gitpipe"/></title>
    <meta name="author" content="Kazuki YAMAMOTO">
    <r:require modules="jquery, bootstrap, gitpipe"/>
    <r:layoutResources/>
    <g:layoutHead/>
</head>

<body>
<div class="topbar">
    <div class="fill">
        <div class="container">
            <a class="brand" href="${request.contextPath}">gitpipe</a>
            <ul class="pull-right">
                <sec:ifLoggedIn>
                    <li><a href="${request.contextPath}/<sec:username/>"><sec:username/></a></li>
                    <li><g:link controller="logout"><g:message code="logout.label"/></g:link></li>
                </sec:ifLoggedIn>
                <sec:ifNotLoggedIn>
                    <li><g:link controller='login' action='auth'><g:message code="login.label"/></g:link></li>
                </sec:ifNotLoggedIn>
            </ul>
        </div>
    </div>
</div>

<div class="container">
    <g:layoutBody/>
    <footer>
        <p>gitpipe &copy; Kazuki YAMAMOTO 2011.</p>
    </footer>
</div> <!-- /container -->

<r:layoutResources/>
</body>
</html>

<!DOCTYPE html>
<html lang="ja">
  <head>
    <meta charset="utf-8">
    <title><g:layoutTitle default="gitpipe"/></title>
    <meta name="author" content="Kazuki YAMAMOTO">
    <link href="${resource(dir: 'css/custom-theme', file: 'jquery-ui-1.8.16.custom.css')}" rel="stylesheet" />
    <link href="${resource(dir: 'css', file: 'bootstrap.css')}" rel="stylesheet" />
    <link href="${resource(dir: 'css', file: 'demo.css')}" rel="stylesheet" />
    <style type="text/css">
      /* Override some defaults */
      html, body {
        background-color: #eee;
      }
      body {
        padding-top: 40px; /* 40px to make the container go all the way to the bottom of the topbar */
      }
      .container > footer p {
        text-align: center; /* center align it with the container */
      }
      /* The white background content wrapper */
      .container > .content {
        background-color: #fff;
        padding: 20px;
        margin: 0 -20px; /* negative indent the amount of the padding to maintain the grid system */
        -webkit-border-radius: 0 0 6px 6px;
           -moz-border-radius: 0 0 6px 6px;
                border-radius: 0 0 6px 6px;
        -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.15);
           -moz-box-shadow: 0 1px 2px rgba(0,0,0,.15);
                box-shadow: 0 1px 2px rgba(0,0,0,.15);
      }
      /* Page header tweaks */
      .page-header {
        background-color: #f5f5f5;
        padding: 20px 20px 10px;
        margin: -20px -20px 20px;
      }
      /* Styles you shouldn't keep as they are for displaying this base example only */
      .content .span10,
      .content .span4 {
        min-height: 500px;
      }
      /* Give a quick and non-cross-browser friendly divider */
      .content .span4 {
        margin-left: 0;
        padding-left: 19px;
        border-left: 1px solid #eee;
      }
      .topbar .btn {
        border: 0;
      }
      .topbar div > ul, .nav {
        float: right;
      }
    </style>
    <r:require module="jquery"/>
    <g:layoutHead/>
    <r:layoutResources />
  </head>

  <body>
    <div class="topbar">
      <div class="fill">
        <div class="container">
          <a class="brand" href="${request.contextPath}">gitpipe</a>
          <ul class="nav">
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
      <div class="content">
        <g:layoutBody/>
      </div>
      <footer>
        <p>gitpipe &copy; Kazuki YAMAMOTO 2011.</p>
      </footer>
    </div> <!-- /container -->

    <!--scripts-->
    %{--<g:javascript library="jQuery" />--}%
    <g:javascript src="jquery-ui-1.8.16.custom.min.js"/>

    <!--daterangepicker-->
    <g:javascript src="jQuery-UI-Date-Range-Picker/date.js"/>
    <g:javascript src="jQuery-UI-Date-Range-Picker/daterangepicker.jQuery.js"/>

    <!--wijmo menu-->
    <g:javascript src="wijmo/jquery.wijmo.wijutil.js"/>
    <g:javascript src="wijmo/jquery.wijmo.wijsuperpanel.js"/>
    <g:javascript src="wijmo/jquery.wijmo.wijmenu.js"/>

    <r:layoutResources />
  </body>
</html>

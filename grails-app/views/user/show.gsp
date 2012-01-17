<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="content_username">
    <h1 class="username">${user.username}&nbsp;<span class="notice">(${user.name})</span></h1>
    <g:if test="${sec.username() == user.username}">
        <div class="profile_actions">
            <span>This is you! <g:link controller="account" class="button">Edit Profile</g:link></span>
        </div>
    </g:if>
</div>

<hr class="separator">

<div class="profile">
    <dl>
        <dt>Name</dt>
        <dd>${user.name}</dd>
    </dl>
    <dl>
        <dt>Email</dt>
        <dd>${user.email}</dd>
    </dl>
    <dl>
        <dt>Company</dt>
        <dd>${user.company}</dd>
    </dl>
    <dl>
        <dt>Location</dt>
        <dd>${user.location}</dd>
    </dl>
    <dl>
        <dt>Member Since</dt>
        <dd><g:formatDate format="MMM dd, yyyy" date="${user.createDate}" locale="en"/></dd>
    </dl>
</div>

<hr>

<div class="row">
    <div class="span10">
        <div>
            <h2 class="">Repositories</h2>

            <g:if test="${sec.username() == user.username}">
                <div>
                    <g:link controller="repository" class="button">New Repository</g:link>
                </div>
            </g:if>
        </div>

        <g:each in="${repositories}" var="repository">
            <div class="repository-box">
                <div class="repository-header">
                    <a href="${request.contextPath}/${user.username}/${repository.projectName}">${repository.projectName}</a>
                </div>

                <div class="repository-body">
                    <span>${repository.description}</span>
                </div>
            </div>
        </g:each>

    </div>

    <div class="span4">
        <h2>Public Activity</h2>

        <p>xxx</p>
    </div>
</div>

<g:javascript>
    (function ($) {
        $('.button').button();
    })(jQuery);
</g:javascript>
</body>

</html>
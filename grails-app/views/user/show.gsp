<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="main"/>
</head>

<body>
<div class="user row">
    <div class="span9">
        <h1 class="username">${user.username}&nbsp;<em>(${user.name})</em></h1>
    </div>

    <div class="span3 acctions">
        <g:if test="${sec.username() == user.username}">
            <span class="label">This is you!</span>
            <g:link mapping="account" class="btn small">Edit Profile</g:link>
        </g:if>
    </div>
</div>

<hr/>

<div class="row">
    <div class="span5 first vcard">
        <dl>
            <dt>Name</dt>
            <dd>${user.name ?: "&nbsp;"}</dd>
        </dl>
        <dl>
            <dt>EMail</dt>
            <dd>${user.email}</dd>
        </dl>
        <dl>
            <dt>Location</dt>
            <dd>${user.location ?: "&nbsp;"}</dd>
        </dl>
        <dl>
            <dt>Member Since</dt>
            <dd><g:formatDate format="MMM dd, yyyy" date="${user.createDate}" locale="en"/></dd>
        </dl>
    </div>
</div>

<hr>

<div class="row repositories">
    <div class="span6">
        <div>
            <h2>Repositories
            <g:if test="${sec.username() == user.username}">
                <g:link controller="project" class="pull-right btn">New Repository</g:link>
            </g:if>
            </h2>
        </div>

        <g:each in="${repositories}" var="repository">
            <div class="repository">
                <div class="repository-title">
                    <h3><a href="${createLink(mapping: 'project', params: [username: user.username, project: repository.name])}">${repository.name}</a></h3>
                </div>

                <div class="repository-meta">
                    <span>${repository.description}</span>
                </div>
            </div>
        </g:each>

    </div>

    <div class="span6">
        <h2>Public Activity</h2>

        <p>xxx</p>
    </div>
</div>
</body>

</html>
<!DOCTYPE html>
<html>

<head>
    <meta name="layout" content="main"/>
</head>

<body>
<section>
    <h1 class="username">${user.username}&nbsp;<small>(${user.name})</small>
        <g:if test="${sec.username() == user.username}">
            <span class="label">This is you!</span>
        </g:if>
    </h1>
</section>

<hr/>

<section>
    <div class="row">
        <div class="span3">
            <h4>Name</h4>

            <p>${user.name}</p>
        </div>

        <div class="span3">
            <h4>Email</h4>

            <p>${user.email}</p>
        </div>

        <div class="span3">
            <h4>Company</h4>

            <p>${user.company}</p>
        </div>

        <div class="span3">
            <h4>Location</h4>

            <p>${user.location}</p>
        </div>

        <div class="span3">
            <h4>Member Since</h4>

            <p><g:formatDate format="MMM dd, yyyy" date="${user.createDate}" locale="en"/></p>
        </div>
    </div>
    <g:if test="${sec.username() == user.username}">
        <g:link controller="account" class="btn small">Edit Profile</g:link>
    </g:if>
</section>

<hr>

<section>
    <div class="row">
        <div class="span8">
            <div>
                <h2 class="">Repositories</h2>

                <g:if test="${sec.username() == user.username}">
                    <g:link controller="repository" class="btn small">New Repository</g:link>
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

        <div class="span6">
            <h2>Public Activity</h2>

            <p>xxx</p>
        </div>
    </div>
</section>
</body>

</html>
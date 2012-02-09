<ul class="nav nav-tabs">
    <li class="${active == 'profile' ? 'active' : ''}">
        <g:link controller="account">Profile</g:link>
    </li>
    <li class="${active == 'admin' ? 'active' : ''}">
        <g:link controller="account" action="admin">Account Admin</g:link>
    </li>
    <li class="${active == 'ssh' ? 'active' : ''}">
        <g:link controller="account" action="ssh">SSH Public Keys</g:link>
    </li>
</ul>
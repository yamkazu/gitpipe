<ul class="nav nav-tabs">
    <li class="${active == 'profile' ? 'active' : ''}">
        <g:link mapping="account">Profile</g:link>
    </li>
    <li class="${active == 'admin' ? 'active' : ''}">
        <g:link mapping="account_admin">Account Admin</g:link>
    </li>
    <li class="${active == 'ssh' ? 'active' : ''}">
        <g:link mapping="account_ssh">SSH Public Keys</g:link>
    </li>
</ul>
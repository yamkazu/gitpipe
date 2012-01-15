package org.gitpipe

class User {

    transient springSecurityService

    String username
    String password
    String email
//    String passwordConfirmation
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    static constraints = {
        username unique: true
    }
//        username blank: false, size: 3..20, unique: true
//        FIXME なぜかvalidatorを使用すると変なことになる
//        http://jira.grails.org/browse/GRAILS-7814
//        password(blank: false, validator: { password , user ->
//            password == user.passwordConfirmation
//        })
//        password blank: false, size: 3..20
//        email email: true, blank: false
//    }

    static mapping = {
        password column: '`password`'
    }

//    static transients = ['passwordConfirmation']

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role } as Set
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService.encodePassword(password)
    }

}

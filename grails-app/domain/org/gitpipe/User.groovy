package org.gitpipe

class User {

    transient springSecurityService

    String name
    String username
    String password
    String email
    Date createDate

    String company
    String location
    
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

//  FIXME
//  http://jira.grails.org/browse/GRAILS-7814
//  のバグが治ったらcommand分離を辞める
    static constraints = {
        username unique: true
    }

    static mapping = {
        password column: '`password`'
    }

    static hasMany = [repositories: RepositoryInfo]

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
        if (springSecurityService) {
            password = springSecurityService.encodePassword(password)
        }
    }

}

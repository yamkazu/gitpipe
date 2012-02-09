package org.gitpipe

class PublicKey {

    String title
    String key

    static belongsTo = [user: User]

    static constraints = {
        title(blank: false)
        key(blank: false)
    }

}

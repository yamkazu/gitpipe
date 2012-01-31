package org.gitpipe.git

import org.eclipse.jgit.lib.PersonIdent

class GpAuthor {

    String name
    String email

    GpAuthor(PersonIdent personIdent) {
        this.name = personIdent.name
        this.email = personIdent.emailAddress
    }

}

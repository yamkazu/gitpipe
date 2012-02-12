package org.gitpipe

class PublicKeyService {

    def grailsApplication

    synchronized writeAuthorizedKeys() {
        new File(grailsApplication.config.app.authorizedkeyfile).withPrintWriter { w ->
            PublicKey.findAll().each { key ->
                w.println "command=\"${grailsApplication.config.app.gitcmd} ${key.user.username}\",no-port-forwarding,no-X11-forwarding,no-agent-forwarding,no-pty ${key.key}"
            }
        }
    }

}

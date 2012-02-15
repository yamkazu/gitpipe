package org.gitpipe

class PublicKeyService {

    def grailsApplication

    synchronized writeAuthorizedKeys() {
        new File(System.getProperty("user.home") + "/.ssh/authorized_keys").withPrintWriter { w ->
            PublicKey.findAll().each { key ->
                w.println "command=\"/usr/bin/env groovy ${grailsApplication.config.gitpipe.command} ${key.user.username}\",no-port-forwarding,no-X11-forwarding,no-agent-forwarding,no-pty ${key.key}"
            }
        }
    }

}

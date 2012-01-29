package org.gitpipe

import org.eclipse.jgit.lib.Constants

import org.gitpipe.util.RepositoryUtil

class Project {

    def grailsApplication

    String name
    String description

    static belongsTo = [user: User]

    static constraints = {
        name(blank: false, size: 3..20, matches: "[a-zA-Z0-9\\-_]+", unique: ['user', 'name'])
    }

    RepositoryUtil getRepository() {
        new RepositoryUtil(directory())
    }

    private File directory() {
        new File(repositoryUserDir(), repositoryName())
    }

    private String repositoryName() {
        name + Constants.DOT_GIT;
    }

    private File repositoryBaseDir() {
        new File(grailsApplication.config.gitpipe.repositories.dir)
    }

    private File repositoryUserDir() {
        new File(repositoryBaseDir(), user.username)
    }

}

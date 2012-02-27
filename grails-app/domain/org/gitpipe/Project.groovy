package org.gitpipe

import org.eclipse.jgit.lib.Constants
import org.gitpipe.git.GpRepository

class Project {

    def grailsApplication

    String name
    String description
    
    Long projectId
    Long forkProjectId

    static belongsTo = [user: User]

    static constraints = {
        name(blank: false, size: 3..20, matches: "[a-zA-Z0-9\\-_]+", unique: ['user', 'name'])
        projectId(nullable: true)
        forkProjectId(nullable: true)
    }

    static transients = ['repository', 'fork']

    GpRepository getRepository() {
        new GpRepository(directory())
    }

    String repositoryName() {
        name + Constants.DOT_GIT;
    }
    
    Project fork(User user) {
        def project = new Project()
        project.name = this.name
        project.description = this.description
        project.projectId = this.projectId ?: this.id
        project.forkProjectId = this.id
        project.user = user
        repository.cloneRepository(project.directory())
        project.save()
    }

    private File directory() {
        new File(repositoryUserDir(), repositoryName())
    }

    private File repositoryBaseDir() {
        new File(grailsApplication.config.gitpipe.repositories.dir)
    }

    private File repositoryUserDir() {
        new File(repositoryBaseDir(), user.username)
    }

}

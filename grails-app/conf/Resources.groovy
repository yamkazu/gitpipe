modules = {

    gitpipe {
        dependsOn 'bootstrap'
        resource url: '/css/gitpipe.css'
    }

    bootstrap {
        resource url: '/css/bootstrap.css'
    }

    bootstrap_alerts {
        dependsOn 'jquery, bootstrap'
        resource url: '/js/bootstrap/bootstrap-alerts.js'
    }

    bootstrap_buttons {
        dependsOn 'jquery, bootstrap'
        resource url: '/js/bootstrap/bootstrap-buttons.js'
    }

    bootstrap_dropdown {
        dependsOn 'jquery, bootstrap'
        resource url: '/js/bootstrap/bootstrap-dropdown.js'
    }

    bootstrap_modal {
        dependsOn 'jquery, bootstrap'
        resource url: '/js/bootstrap/bootstrap-modal.js'
    }

    bootstrap_popover {
        dependsOn 'jquery, bootstrap'
        resource url: '/js/bootstrap/bootstrap-popover.js'
    }

    bootstrap_scrollspy {
        dependsOn 'jquery, bootstrap'
        resource url: '/js/bootstrap/bootstrap-scrollspy.js'
    }

    bootstrap_tabs {
        dependsOn 'jquery, bootstrap'
        resource url: '/js/bootstrap/bootstrap-tabs.js'
    }

    bootstrap_twipsy {
        dependsOn 'jquery, bootstrap'
        resource url: '/js/bootstrap/bootstrap-twipsy.js'
    }

}


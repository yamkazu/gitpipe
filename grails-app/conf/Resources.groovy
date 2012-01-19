modules = {

    gitpipe {
        dependsOn 'bootstrap'
        resource url: '/css/gitpipe.css'
    }

    gitpipe_treeviewer {
        dependsOn 'jquery, ui_effects_slide'
        resource url: 'js/treeviewer.js'
    }

    ui_effects_core {
        dependsOn 'jquery'
        resource url: 'js/ui/jquery.effects.core.min.js'
    }

    ui_effects_slide {
        dependsOn 'jquery, ui_effects_core'
        resource url: 'js/ui/jquery.effects.slide.min.js'
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


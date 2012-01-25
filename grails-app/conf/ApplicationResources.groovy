modules = {

    gitpipe {
        dependsOn 'bootstrap'
        resource url: '/css/gitpipe.css'
    }

    gitpipe_treeviewer {
        dependsOn 'jquery, ui_effects_slide, syntax_highlighter'
        resource url: 'js/tree.js'
    }

    gitpipe_commitsviewer {
        dependsOn 'jquery'
        resource url: 'js/commits.js'
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

    syntax_highlighter {
        // style
        resource url: '/css/shThemeGitpipe.css'
        // core scripts
        resource url: '/js/sh/XRegExp.js'
        resource url: '/js/sh/shCore.js'
        resource url: '/js/sh/shAutoloader.js'
        // language scripts
        resource url: '/js/sh/shBrushAppleScript.js'
        resource url: '/js/sh/shBrushAS3.js'
        resource url: '/js/sh/shBrushBash.js'
        resource url: '/js/sh/shBrushColdFusion.js'
        resource url: '/js/sh/shBrushCpp.js'
        resource url: '/js/sh/shBrushCSharp.js'
        resource url: '/js/sh/shBrushCss.js'
        resource url: '/js/sh/shBrushDelphi.js'
        resource url: '/js/sh/shBrushDiff.js'
        resource url: '/js/sh/shBrushErlang.js'
        resource url: '/js/sh/shBrushGroovy.js'
        resource url: '/js/sh/shBrushJava.js'
        resource url: '/js/sh/shBrushJavaFX.js'
        resource url: '/js/sh/shBrushJScript.js'
        resource url: '/js/sh/shBrushPerl.js'
        resource url: '/js/sh/shBrushPhp.js'
        resource url: '/js/sh/shBrushPlain.js'
        resource url: '/js/sh/shBrushPowerShell.js'
        resource url: '/js/sh/shBrushPython.js'
        resource url: '/js/sh/shBrushRuby.js'
        resource url: '/js/sh/shBrushSass.js'
        resource url: '/js/sh/shBrushScala.js'
        resource url: '/js/sh/shBrushSql.js'
        resource url: '/js/sh/shBrushVb.js'
        resource url: '/js/sh/shBrushXml.js'
    }

}


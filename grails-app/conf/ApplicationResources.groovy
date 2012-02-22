modules = {

    bootstrap {
        dependsOn 'jquery'
        resource url: 'css/bootstrap.min.css'
        resource url: 'js/bootstrap.min.js'
    }

    gitpipe {
        dependsOn 'bootstrap'
        resource url: '/css/gitpipe.css'
    }

    gitpipe_treeviewer {
        dependsOn 'gitpipe, ui_effects_slide, syntax_highlighter, history'
        resource url: 'js/tree.js'
    }

    gitpipe_commitsviewer {
        dependsOn 'gitpipe'
//        resource url: 'js/commits.js'
        resource url: 'cs/commits.coffee'
    }

    gitpipe_commitviewer {
        dependsOn 'gitpipe, syntax_highlighter'
        resource url: 'js/commit.js'
    }

    history {
        dependsOn 'jquery'
        resource url: 'js/history/jquery.history.js'
    }

    ui_effects_core {
        dependsOn 'jquery'
        resource url: 'js/ui/jquery.effects.core.min.js'
    }

    ui_effects_slide {
        dependsOn 'jquery, ui_effects_core'
        resource url: 'js/ui/jquery.effects.slide.min.js'
    }

    syntax_highlighter {
        // style
        resource url: 'css/shThemeGitpipe.css'
        // core scripts
        resource url: 'js/sh/XRegExp.js'
        resource url: 'js/sh/shCore.js'
        resource url: 'js/sh/shAutoloader.js'
        // language scripts
        resource url: 'js/sh/shBrushAppleScript.js'
        resource url: 'js/sh/shBrushAS3.js'
        resource url: 'js/sh/shBrushBash.js'
        resource url: 'js/sh/shBrushColdFusion.js'
        resource url: 'js/sh/shBrushCpp.js'
        resource url: 'js/sh/shBrushCSharp.js'
        resource url: 'js/sh/shBrushCss.js'
        resource url: 'js/sh/shBrushDelphi.js'
        resource url: 'js/sh/shBrushDiff.js'
        resource url: 'js/sh/shBrushErlang.js'
        resource url: 'js/sh/shBrushGroovy.js'
        resource url: 'js/sh/shBrushJava.js'
        resource url: 'js/sh/shBrushJavaFX.js'
        resource url: 'js/sh/shBrushJScript.js'
        resource url: 'js/sh/shBrushPerl.js'
        resource url: 'js/sh/shBrushPhp.js'
        resource url: 'js/sh/shBrushPlain.js'
        resource url: 'js/sh/shBrushPowerShell.js'
        resource url: 'js/sh/shBrushPython.js'
        resource url: 'js/sh/shBrushRuby.js'
        resource url: 'js/sh/shBrushSass.js'
        resource url: 'js/sh/shBrushScala.js'
        resource url: 'js/sh/shBrushSql.js'
        resource url: 'js/sh/shBrushVb.js'
        resource url: 'js/sh/shBrushXml.js'
    }

}


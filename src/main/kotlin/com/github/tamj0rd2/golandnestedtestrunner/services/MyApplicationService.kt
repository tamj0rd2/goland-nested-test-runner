package com.github.tamj0rd2.golandnestedtestrunner.services

import com.github.tamj0rd2.golandnestedtestrunner.MyBundle

class MyApplicationService {

    init {
        println(MyBundle.message("applicationService"))

        System.getenv("CI")
            ?: TODO("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }
}

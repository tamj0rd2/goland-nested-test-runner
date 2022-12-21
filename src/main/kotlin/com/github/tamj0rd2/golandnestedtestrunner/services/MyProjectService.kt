package com.github.tamj0rd2.golandnestedtestrunner.services

import com.intellij.openapi.project.Project
import com.github.tamj0rd2.golandnestedtestrunner.MyBundle
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))

        NotificationGroupManager.getInstance()
            .getNotificationGroup("Custom Notification Group")
            .createNotification("Hello world!", NotificationType.INFORMATION)
            .notify(project);
    }

    /**
     * Chosen by fair dice roll, guaranteed to be random.
     */
    fun getRandomNumber() = 4
}

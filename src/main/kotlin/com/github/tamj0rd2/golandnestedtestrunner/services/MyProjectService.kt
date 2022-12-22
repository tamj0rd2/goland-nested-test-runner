package com.github.tamj0rd2.golandnestedtestrunner.services

import com.github.tamj0rd2.golandnestedtestrunner.MyBundle
import com.goide.psi.GoCallExpr
import com.goide.psi.GoFunctionDeclaration
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.apache.commons.lang3.StringUtils

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))

        NotificationGroupManager.getInstance()
            .getNotificationGroup("Custom Notification Group")
            .createNotification("Hello world!", NotificationType.INFORMATION)
            .notify(project);
    }

    fun getTestRegex(element: PsiElement): String {
        val subtestCallExpressions = mutableListOf<SubtestCallExpression>()

        var cursor: PsiElement? = element
        while (cursor != null) {
            val subtestCallExpr = findNearestSubtestCallExpression(cursor)
            if (subtestCallExpr != null) {
                subtestCallExpressions.add(subtestCallExpr)
            }
            cursor = subtestCallExpr?.element
        }

        val functionDeclaration = findNearestFunctionDeclaration(subtestCallExpressions.last().element)
            ?: return "Cover edge case where there is no function declaration found"

        val testNames = subtestCallExpressions.reversed().joinToString("$/") { "^${it.name}" }
        return "^${functionDeclaration.identifier.text}$/${testNames}"
    }
}

data class SubtestCallExpression(val element: GoCallExpr) {
    val name: String = element.argumentList.expressionList[0].value!!.string!!.replace(" ", "_")
}

fun findNearestSubtestCallExpression(element: PsiElement): SubtestCallExpression? {
    var cursor = element
    var count = 0

    do {
        val callExpr = PsiTreeUtil.getParentOfType(cursor, GoCallExpr::class.java) ?: break

        if (StringUtils.startsWithAny(callExpr.text, "t.Run")) {
            return SubtestCallExpression(callExpr)
        }

        cursor = callExpr
        count++
    } while (count < 100)

    return null
}

fun findNearestFunctionDeclaration(element: PsiElement): GoFunctionDeclaration? {
    return PsiTreeUtil.getParentOfType(element, GoFunctionDeclaration::class.java)
}

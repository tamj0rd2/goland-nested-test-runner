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

    fun getFullTestName(element: PsiElement): String {
        val subtestCallExpr = mustFindNearestSubtestCallExpression(element)
        if (subtestCallExpr == null) {
            return "Cover edge case where there is no call expression found"
        }

        val functionDeclaration = findNearestFunctionDeclaration(subtestCallExpr.element)
        if (functionDeclaration == null) {
            return "Cover edge case where there is no function declaration found"
        }

        return "^${functionDeclaration.identifier.text}$/^${subtestCallExpr.name}"
    }
}

data class SubtestCallExpression(val element: GoCallExpr) {
    val name: String = element.argumentList.expressionList[0].value?.string ?:
    throw InvalidSubtestCallExpression("Call expression has no first argument value:\n${element.text}")
}

class InvalidSubtestCallExpression(message: String) : Exception(message)

class ParentSubtestCallExpressionNotFound : Exception("parent subtest call expression not found")

fun mustFindNearestSubtestCallExpression(element: PsiElement): SubtestCallExpression {
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

    throw ParentSubtestCallExpressionNotFound()
}

fun findNearestFunctionDeclaration(element: PsiElement): GoFunctionDeclaration? {
    return PsiTreeUtil.getParentOfType(element, GoFunctionDeclaration::class.java)
}

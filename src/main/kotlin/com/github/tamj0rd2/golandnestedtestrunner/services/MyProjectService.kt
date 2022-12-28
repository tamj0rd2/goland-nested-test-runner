package com.github.tamj0rd2.golandnestedtestrunner.services

import com.goide.psi.GoCallExpr
import com.goide.psi.GoFunctionDeclaration
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import org.apache.commons.lang3.StringUtils

class MyProjectService(project: Project) {

    init {
        logMessage(project, "Hello world :D")
    }

    fun logMessage(project: Project, message: String) {
        println(message)
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Custom Notification Group")
            .createNotification(message, NotificationType.INFORMATION)
            .notify(project);
    }

    fun getTestRegex(element: PsiElement): String {
        val subtestCallExpressions = mutableListOf<SubtestCallExpression>()

        var cursor: PsiElement? = element
        while (cursor != null) {
            val subtestCallExpr = findParentSubtest(cursor)
            cursor = subtestCallExpr?.element

            if (subtestCallExpr != null) {
                subtestCallExpressions.add(subtestCallExpr)
                continue
            }
        }

        val testNames = subtestCallExpressions.reversed().joinToString("/") { "^${it.name}$" }

        findHighestFunctionDeclaration(subtestCallExpressions.last().element)?.let {
            val topLevelTestName = "^${it.getIdentifier().text}\$"
            return "$topLevelTestName/$testNames"
        }

        throw Exception("Couldn't figure out test regex. Got as far as: ${testNames}")
    }
}

data class SubtestCallExpression(val element: GoCallExpr) {
    val name: String = element.argumentList.expressionList[0].value!!.string!!.replace(" ", "_")
}

fun findParentSubtest(element: PsiElement): SubtestCallExpression? {
    var cursor: PsiElement? = element
    var count = 0

    while (cursor != null && count < 50) {
        count++

        val callExpr = PsiTreeUtil.getParentOfType(cursor, GoCallExpr::class.java)
        if (callExpr == null) {
            val functionDeclaration = PsiTreeUtil.getParentOfType(element, GoFunctionDeclaration::class.java) ?: break
            cursor = ReferencesSearch.search(functionDeclaration).findFirst()?.element
            continue
        }

        if (StringUtils.startsWithAny(callExpr.text, "t.Run")) {
            return SubtestCallExpression(callExpr)
        }

        cursor = callExpr
    }

    return null
}

fun findHighestFunctionDeclaration(element: PsiElement): GoFunctionDeclaration? {
    var cursor: PsiElement = element
    var lastFound: GoFunctionDeclaration? = null
    var count = 0

    while (count < 20) {
        val functionDeclaration = PsiTreeUtil.getTopmostParentOfType(cursor, GoFunctionDeclaration::class.java) ?: return lastFound
        val references = ReferencesSearch.search(functionDeclaration).findAll()
        if (references.size == 0) {
            return functionDeclaration
        }

        lastFound = functionDeclaration
        cursor = references.first().element
        count++
        continue
    }

    return null
}

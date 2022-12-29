package com.github.tamj0rd2.golandnestedtestrunner.services

import com.goide.psi.GoCallExpr
import com.goide.psi.GoFunctionDeclaration
import com.goide.psi.GoFunctionOrMethodDeclaration
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

    fun getTestRegex(element: PsiElement): List<String> {
        val callExpr = findNearestSubtestCallExpression(element) ?: throw Exception("Couldn't find any subtests for: ${element.text}")
        return Subtest.from(callExpr).getFullTestNames()
    }
}

private data class Subtest(val parents: List<Subtest>, val name: SubtestName) {

    companion object Factory {
        fun from(callExpr: GoCallExpr): Subtest {
            return Subtest(
                getParents(callExpr),
                SubtestName(callExpr.argumentList.expressionList[0].value!!.string!!)
            )
        }

        fun from(functionDeclaration: GoFunctionDeclaration): Subtest {
            return Subtest(
                getParents(functionDeclaration),
                SubtestName(functionDeclaration.getIdentifier().text)
            )
        }

        private fun getParents(element: PsiElement): List<Subtest> {
            val parentCallExpr = findNearestSubtestCallExpression(element)
            if (parentCallExpr != null) {
                return listOf(from(parentCallExpr))
            }

            val functionDeclaration = PsiTreeUtil.getParentOfType(element, GoFunctionOrMethodDeclaration::class.java)
            if (functionDeclaration != null) {
                val references = ReferencesSearch.search(functionDeclaration).findAll()
                if (references.size == 0) {
                    val identifier = functionDeclaration.getIdentifier() ?: throw Exception("Couldn't find identifier for function: ${functionDeclaration.text}")
                    return listOf(Subtest(emptyList(), SubtestName(identifier.text)))
                }

                return buildList { references.forEach { addAll(getParents(it.element)) } }
            }

            return emptyList()
        }
    }

    fun getFullTestNames(): List<String> {
        if (parents.isEmpty()) {
            return listOf(this.name.toString())
        }

        val self = this

        return buildList {
            parents.forEach { parent ->
                parent.getFullTestNames().forEach { fullTestName ->
                    add("$fullTestName/${self.name}")
                }
            }
        }
    }
}

private data class SubtestName(val rawName: String) {

    private fun sanitize(): String {
        return rawName.replace(" ", "_")
    }

    override fun toString(): String {
        return "^${sanitize()}\$"
    }
}

private fun findHighestFunctionDeclaration(element: PsiElement): GoFunctionDeclaration? {
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

private fun findNearestSubtestCallExpression(element: PsiElement): GoCallExpr? {
    var cursor: PsiElement = element

    while (true) {
        val callExpr = PsiTreeUtil.getParentOfType(cursor, GoCallExpr::class.java) ?: return null

        if (StringUtils.startsWithAny(callExpr.text, "t.Run")) {
            return callExpr
        }

        cursor = callExpr
    }
}

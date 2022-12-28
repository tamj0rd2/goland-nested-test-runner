package com.github.tamj0rd2.golandnestedtestrunner.actions

import com.github.tamj0rd2.golandnestedtestrunner.MyBundle
import com.github.tamj0rd2.golandnestedtestrunner.services.MyProjectService
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

class RunNestedTestIntentionAction : PsiElementBaseIntentionAction() {
    override fun getFamilyName(): String {
        return MyBundle.message("nestedTestRunner.intentionAction.Name")
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        return editor != null && element.containingFile.fileType.defaultExtension == "go"
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        val service = project.service<MyProjectService>()
        service.logMessage(project, "Hello from intention action :D")
        service.logMessage(project, service.getTestRegex(element))
    }

    override fun getText(): String {
        return MyBundle.message("nestedTestRunner.intentionAction.Name")
    }

    override fun startInWriteAction(): Boolean {
        return false
    }
}
package com.github.tamj0rd2.golandnestedtestrunner

import com.github.tamj0rd2.golandnestedtestrunner.services.MyProjectService
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.PsiErrorElementUtil
import com.goide.psi.GoFile
import com.intellij.openapi.components.service
import com.intellij.psi.util.elementType
import junit.framework.TestCase

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {
    override fun getTestDataPath() = "src/test/testData"

    fun testSingleSubtest() {
        val testFile = openGoFileInEditor("goProject/some_test.go")

        val offset = myFixture.editor.document.text.indexOf("My single subtest")
        val element = testFile.findElementAt(offset)!!

        val got = project.service<MyProjectService>().getFullTestName(element)
        val want = "^TestTopLevelForSingleSubtest$/^My single subtest"
        TestCase.assertEquals(want, got)
    }

    fun openGoFileInEditor(filePath: String): GoFile {
        val psiFile = myFixture.configureByFile(filePath)

        val goFile = assertInstanceOf(psiFile, GoFile::class.java)
        assertFalse(PsiErrorElementUtil.hasErrors(project, goFile.virtualFile))

        myFixture.openFileInEditor(goFile.virtualFile)
        return goFile
    }
}

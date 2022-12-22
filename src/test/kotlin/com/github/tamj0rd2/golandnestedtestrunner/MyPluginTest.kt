package com.github.tamj0rd2.golandnestedtestrunner

import com.github.tamj0rd2.golandnestedtestrunner.services.MyProjectService
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.PsiErrorElementUtil
import com.goide.psi.GoFile
import com.intellij.openapi.components.service
import junit.framework.TestCase

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class MyPluginTest : BasePlatformTestCase() {
    override fun getTestDataPath() = "src/test/testData/goProject"

    fun testSingleSubtest() {
        assertTestRegexEquals(
            "some_test.go",
            "My single subtest",
            "^TestTopLevel$/^My single subtest"
        )
    }

    fun testNestedSubtest() {
        assertTestRegexEquals(
            "some_test.go",
            "My nested subtest",
            "^TestTopLevel$/^My second subtest$/^My nested subtest"
        )
    }

    fun assertTestRegexEquals(filePath: String, subtestName: String, expectedRegex: String) {
        val testFile = openTestFileInEditor(filePath)

        val offset = myFixture.editor.document.text.indexOf(subtestName)
        val element = testFile.findElementAt(offset)!!

        val gotRegex = project.service<MyProjectService>().getTestRegex(element)
        TestCase.assertEquals(expectedRegex, gotRegex)
    }

    fun openTestFileInEditor(filePath: String): GoFile {
        val psiFile = myFixture.configureByFile(filePath)

        val goFile = assertInstanceOf(psiFile, GoFile::class.java)
        assertFalse(PsiErrorElementUtil.hasErrors(project, goFile.virtualFile))

        myFixture.openFileInEditor(goFile.virtualFile)
        return goFile
    }
}

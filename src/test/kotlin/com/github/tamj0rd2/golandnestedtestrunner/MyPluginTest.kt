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

    fun testSubtest() {
        assertTestRegexEquals(
            "some_test.go",
            "Subtest",
            "^TestTopLevel$/^Subtest$"
        )
    }

    fun testNestedSubtest() {
        assertTestRegexEquals(
            "some_test.go",
            "Nested subtest",
            "^TestTopLevel$/^Subtest$/^Nested_subtest$"
        )
    }

    fun testDeeplyNestedSubtest() {
        assertTestRegexEquals(
            "some_test.go",
            "Deeply nested subtest",
            "^TestTopLevel$/^Subtest$/^Nested_subtest$/^Deeply_nested_subtest$"
        )
    }

    fun testTestFuncWithSingleUsage() {
        assertTestRegexEquals(
            "some_test.go",
            "Subtest with single usage",
            "^TestTopLevel$/^Subtest$/^Subtest_with_single_usage$"
        )
    }

    fun testTestFuncWithMultipleUsages() {
        assertTestRegexEquals(
            "some_test.go",
            "Subtest with multiple usages",
            "^TestTopLevel$/^Subtest$/^First_usage$/^Subtest_with_multiple_usages$",
            "^TestTopLevel$/^Subtest$/^Second_usage$/^Subtest_with_multiple_usages$"
        )
    }

    fun assertTestRegexEquals(filePath: String, subtestName: String, vararg expectedRegexes: String) {
        val testFile = openTestFileInEditor(filePath)

        val offset = myFixture.editor.document.text.indexOf(subtestName)
        val element = testFile.findElementAt(offset)!!

        val gotRegexes = project.service<MyProjectService>().getTestRegex(element)
        TestCase.assertEquals(expectedRegexes.asList(), gotRegexes)
    }

    fun openTestFileInEditor(filePath: String): GoFile {
        val psiFile = myFixture.configureByFile(filePath)

        val goFile = assertInstanceOf(psiFile, GoFile::class.java)
        assertFalse(PsiErrorElementUtil.hasErrors(project, goFile.virtualFile))

        myFixture.openFileInEditor(goFile.virtualFile)
        return goFile
    }
}

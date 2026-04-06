package com.branchcommit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BranchNameFormatterTest {

    private val defaultPattern = "^(?:.+/)?([A-Za-z]+-\\d+)[_-](.+)$"
    private val defaultFormat = "[{ticket}] {description}"

    private fun format(branchName: String) = BranchNameFormatter.formatWith(
        branchName = branchName,
        patternString = defaultPattern,
        formatTemplate = defaultFormat,
        replaceUnderscores = true
    )

    @Test
    fun featureBranchWithUnderscores() {
        assertEquals(
            "[ABC-1234] Add function comment",
            format("feature/ABC-1234_Add_function_comment")
        )
    }

    @Test
    fun bugfixBranch() {
        assertEquals(
            "[BUG-567] Fix login issue",
            format("bugfix/BUG-567_Fix_login_issue")
        )
    }

    @Test
    fun hotfixBranch() {
        assertEquals(
            "[HOT-1] Emergency fix",
            format("hotfix/HOT-1_Emergency_fix")
        )
    }

    @Test
    fun branchWithoutPrefix() {
        assertEquals(
            "[PROJ-99] Update readme",
            format("PROJ-99_Update_readme")
        )
    }

    @Test
    fun branchWithDashSeparator() {
        assertEquals(
            "[ABC-1234] Add function comment",
            format("feature/ABC-1234-Add-function-comment")
        )
    }

    @Test
    fun hotfixBranchWithDashes() {
        assertEquals(
            "[TCS-1234] Add function comment",
            format("hotfix/TCS-1234-Add-function-comment")
        )
    }

    @Test
    fun mixedDashesAndUnderscores() {
        assertEquals(
            "[ABC-1234] Add function comment",
            format("feature/ABC-1234_Add-function_comment")
        )
    }

    @Test
    fun branchWithNestedPrefix() {
        assertEquals(
            "[TEAM-42] Refactor module",
            format("team/feature/TEAM-42_Refactor_module")
        )
    }

    @Test
    fun nonMatchingBranchReturnsNull() {
        assertNull(format("main"))
        assertNull(format("develop"))
        assertNull(format("release/1.0.0"))
    }

    @Test
    fun singleWordDescription() {
        assertEquals(
            "[FIX-1] Hotfix",
            format("FIX-1_Hotfix")
        )
    }

    @Test
    fun customFormat() {
        assertEquals(
            "ABC-1234: Add function comment",
            BranchNameFormatter.formatWith(
                branchName = "feature/ABC-1234_Add_function_comment",
                patternString = defaultPattern,
                formatTemplate = "{ticket}: {description}",
                replaceUnderscores = true
            )
        )
    }

    @Test
    fun replaceUnderscoresDisabled() {
        assertEquals(
            "[ABC-1234] Add_function_comment",
            BranchNameFormatter.formatWith(
                branchName = "feature/ABC-1234_Add_function_comment",
                patternString = defaultPattern,
                formatTemplate = defaultFormat,
                replaceUnderscores = false
            )
        )
    }

    @Test
    fun invalidRegexReturnsNull() {
        assertNull(
            BranchNameFormatter.formatWith(
                branchName = "feature/ABC-1234_Test",
                patternString = "[invalid((",
                formatTemplate = defaultFormat,
                replaceUnderscores = true
            )
        )
    }
}

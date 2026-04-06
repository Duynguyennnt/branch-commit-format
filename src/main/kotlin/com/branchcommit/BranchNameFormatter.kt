package com.branchcommit

object BranchNameFormatter {

    private const val DEFAULT_PATTERN = "^(?:.+/)?([A-Za-z]+-\\d+)[_-](.+)$"
    private const val DEFAULT_FORMAT = "[{ticket}] {description}"

    fun format(branchName: String): String? {
        val settings = runCatching { BranchCommitSettings.getInstance() }.getOrNull()
        return formatWith(
            branchName = branchName,
            patternString = settings?.state?.branchPattern ?: DEFAULT_PATTERN,
            formatTemplate = settings?.state?.commitFormat ?: DEFAULT_FORMAT,
            replaceUnderscores = settings?.state?.replaceUnderscores ?: true
        )
    }

    fun formatWith(
        branchName: String,
        patternString: String,
        formatTemplate: String,
        replaceUnderscores: Boolean
    ): String? {
        val pattern = runCatching { Regex(patternString) }.getOrNull() ?: return null
        val match = pattern.matchEntire(branchName) ?: return null
        val ticketId = match.groupValues.getOrElse(1) { "" }
        var description = match.groupValues.getOrElse(2) { "" }
        if (replaceUnderscores) {
            description = description.replace('_', ' ').replace('-', ' ')
        }
        return formatTemplate
            .replace("{ticket}", ticketId)
            .replace("{description}", description.trim())
    }
}

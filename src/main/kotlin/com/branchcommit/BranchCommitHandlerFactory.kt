package com.branchcommit

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory
import git4idea.repo.GitRepositoryManager
import javax.swing.SwingUtilities

class BranchCommitHandlerFactory : CheckinHandlerFactory() {

    override fun createHandler(panel: CheckinProjectPanel, commitContext: CommitContext): CheckinHandler {
        val settings = runCatching { BranchCommitSettings.getInstance() }.getOrNull()
        if (settings?.state?.enabled == false) {
            return object : CheckinHandler() {}
        }

        SwingUtilities.invokeLater {
            ApplicationManager.getApplication().invokeLater {
                fillCommitMessage(panel)
            }
        }

        return object : CheckinHandler() {}
    }

    private fun fillCommitMessage(panel: CheckinProjectPanel) {
        if (!panel.commitMessage.isNullOrBlank()) return

        val project = panel.project
        val repo = GitRepositoryManager.getInstance(project).repositories.firstOrNull() ?: return
        val branchName = repo.currentBranch?.name ?: return
        val formatted = BranchNameFormatter.format(branchName) ?: return

        panel.setCommitMessage(formatted)
    }
}

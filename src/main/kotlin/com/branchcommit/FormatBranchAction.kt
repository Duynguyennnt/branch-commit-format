package com.branchcommit

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vcs.VcsDataKeys
import git4idea.repo.GitRepositoryManager

class FormatBranchAction : AnAction("Format Branch to Commit Message") {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val commitMessage = e.getData(VcsDataKeys.COMMIT_MESSAGE_CONTROL) ?: return

        val repo = GitRepositoryManager.getInstance(project).repositories.firstOrNull() ?: return
        val branchName = repo.currentBranch?.name ?: return

        val formatted = BranchNameFormatter.format(branchName) ?: return
        commitMessage.setCommitMessage(formatted)
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}

package com.branchcommit

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import git4idea.repo.GitRepositoryManager
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.Font
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class BranchCommitToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = BranchCommitToolWindowPanel(project)
        val content = ContentFactory.getInstance().createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}

private class BranchCommitToolWindowPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val branchLabel = JBLabel("—")
    private val resultField = JBTextField().apply {
        isEditable = false
        columns = 35
    }
    private val copyButton = JButton("Copy")
    private val refreshButton = JButton("Refresh")
    private val statusLabel = JBLabel("").apply {
        foreground = JBUI.CurrentTheme.ContextHelp.FOREGROUND
        font = font.deriveFont(font.size2D - 1f)
    }

    init {
        border = JBUI.Borders.empty(12)
        buildUI()
        refresh()
        listenForBranchChanges()
    }

    private fun buildUI() {
        val content = Box.createVerticalBox()

        content.add(createSection("Current Branch", branchLabel))
        content.add(Box.createVerticalStrut(12))
        content.add(createSection("Commit Message", resultField))
        content.add(Box.createVerticalStrut(8))

        val buttonRow = JPanel(FlowLayout(FlowLayout.LEFT, 0, 0))
        copyButton.addActionListener { copyToClipboard() }
        refreshButton.addActionListener { refresh() }
        buttonRow.add(copyButton)
        buttonRow.add(Box.createHorizontalStrut(8))
        buttonRow.add(refreshButton)
        content.add(buttonRow)

        content.add(Box.createVerticalStrut(8))
        content.add(statusLabel)

        add(content, BorderLayout.NORTH)
    }

    private fun createSection(title: String, component: JComponent): JPanel {
        val section = JPanel(BorderLayout(0, 4))
        section.alignmentX = LEFT_ALIGNMENT
        val label = JBLabel(title).apply {
            font = font.deriveFont(Font.BOLD)
        }
        section.add(label, BorderLayout.NORTH)
        section.add(component, BorderLayout.CENTER)
        return section
    }

    private fun refresh() {
        val repo = GitRepositoryManager.getInstance(project).repositories.firstOrNull()
        val branchName = repo?.currentBranch?.name

        if (branchName == null) {
            branchLabel.text = "(no branch detected)"
            resultField.text = ""
            statusLabel.text = ""
            return
        }

        branchLabel.text = branchName
        val formatted = BranchNameFormatter.format(branchName)
        if (formatted != null) {
            resultField.text = formatted
            statusLabel.text = "Ready to copy"
        } else {
            resultField.text = ""
            statusLabel.text = "Branch name does not match the configured pattern"
        }
    }

    private fun copyToClipboard() {
        val text = resultField.text
        if (text.isBlank()) return
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(StringSelection(text), null)
        statusLabel.text = "Copied!"
    }

    private fun listenForBranchChanges() {
        project.messageBus.connect().subscribe(
            git4idea.repo.GitRepository.GIT_REPO_CHANGE,
            git4idea.repo.GitRepositoryChangeListener { refresh() }
        )
    }
}

package com.branchcommit

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.JBUI
import java.awt.Font
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class BranchCommitConfigurable : Configurable {

    private val settings = BranchCommitSettings.getInstance()

    private val enabledCheckbox = JBCheckBox("Enable auto-fill commit message from branch name")
    private val patternField = JBTextField().apply { columns = 45 }
    private val formatField = JBTextField().apply { columns = 45 }
    private val replaceUnderscoresCheckbox = JBCheckBox("Replace separators (underscores and dashes) with spaces in description")
    private val testInputField = JBTextField("feature/ABC-1234_Add_function_comment").apply { columns = 45 }
    private val previewLabel = JBLabel("").apply {
        font = font.deriveFont(Font.BOLD)
        border = JBUI.Borders.empty(4, 0)
    }

    override fun getDisplayName(): String = "Branch Commit Formatter"

    override fun createComponent(): JComponent {
        reset()
        updatePreview()

        val livePreviewListener = object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = updatePreview()
            override fun removeUpdate(e: DocumentEvent?) = updatePreview()
            override fun changedUpdate(e: DocumentEvent?) = updatePreview()
        }
        testInputField.document.addDocumentListener(livePreviewListener)
        patternField.document.addDocumentListener(livePreviewListener)
        formatField.document.addDocumentListener(livePreviewListener)
        replaceUnderscoresCheckbox.addActionListener { updatePreview() }

        val patternHint = JBLabel("Regex groups: (1) = ticket ID, (2) = description").apply {
            foreground = JBUI.CurrentTheme.ContextHelp.FOREGROUND
            font = font.deriveFont(font.size2D - 1f)
        }
        val formatHint = JBLabel("Placeholders: {ticket}, {description}").apply {
            foreground = JBUI.CurrentTheme.ContextHelp.FOREGROUND
            font = font.deriveFont(font.size2D - 1f)
        }

        return FormBuilder.createFormBuilder()
            .addComponent(JBLabel("General").apply {
                font = font.deriveFont(Font.BOLD)
                border = JBUI.Borders.emptyBottom(4)
            })
            .addComponent(enabledCheckbox)
            .addVerticalGap(12)
            .addComponent(JBLabel("Pattern Configuration").apply {
                font = font.deriveFont(Font.BOLD)
                border = JBUI.Borders.emptyBottom(4)
            })
            .addLabeledComponent("Branch regex pattern:", patternField)
            .addComponentToRightColumn(patternHint, 0)
            .addLabeledComponent("Commit message format:", formatField)
            .addComponentToRightColumn(formatHint, 0)
            .addComponent(replaceUnderscoresCheckbox)
            .addComponent(JButton("Reset to Defaults").apply {
                addActionListener { resetToDefaults() }
                border = JBUI.Borders.empty(6, 0, 0, 0)
                isContentAreaFilled = false
                isBorderPainted = false
                foreground = JBUI.CurrentTheme.Link.Foreground.ENABLED
                cursor = java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR)
            })
            .addVerticalGap(12)
            .addComponent(JBLabel("Live Preview").apply {
                font = font.deriveFont(Font.BOLD)
                border = JBUI.Borders.emptyBottom(4)
            })
            .addLabeledComponent("Test branch name:", testInputField)
            .addLabeledComponent("Result:", previewLabel)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    override fun isModified(): Boolean {
        val state = settings.state
        return enabledCheckbox.isSelected != state.enabled ||
                patternField.text != state.branchPattern ||
                formatField.text != state.commitFormat ||
                replaceUnderscoresCheckbox.isSelected != state.replaceUnderscores
    }

    override fun apply() {
        val state = settings.state
        state.enabled = enabledCheckbox.isSelected
        state.branchPattern = patternField.text
        state.commitFormat = formatField.text
        state.replaceUnderscores = replaceUnderscoresCheckbox.isSelected
    }

    override fun reset() {
        val state = settings.state
        enabledCheckbox.isSelected = state.enabled
        patternField.text = state.branchPattern
        formatField.text = state.commitFormat
        replaceUnderscoresCheckbox.isSelected = state.replaceUnderscores
        updatePreview()
    }

    private fun resetToDefaults() {
        enabledCheckbox.isSelected = true
        patternField.text = BranchCommitSettings.DEFAULT_PATTERN
        formatField.text = BranchCommitSettings.DEFAULT_FORMAT
        replaceUnderscoresCheckbox.isSelected = true
        updatePreview()
    }

    private fun updatePreview() {
        val input = testInputField.text
        if (input.isBlank()) {
            previewLabel.text = ""
            return
        }
        val result = BranchNameFormatter.formatWith(
            branchName = input,
            patternString = patternField.text,
            formatTemplate = formatField.text,
            replaceUnderscores = replaceUnderscoresCheckbox.isSelected
        )
        previewLabel.text = result ?: "(no match)"
    }
}

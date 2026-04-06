# Branch Commit Formatter

A JetBrains IDE plugin (PhpStorm, IntelliJ IDEA, WebStorm, etc.) that automatically converts Git branch names into structured commit messages.

## Example

| Branch Name | Commit Message |
|---|---|
| `feature/ABC-1234_Add_function_comment` | `[ABC-1234] Add function comment` |
| `bugfix/BUG-567_Fix_login_issue` | `[BUG-567] Fix login issue` |
| `hotfix/TCS-1234-Add-function-comment` | `[TCS-1234] Add function comment` |
| `PROJ-99_Update_readme` | `[PROJ-99] Update readme` |

## Features

- **Auto-fill commit message** — when opening the commit dialog, if the message is empty, it is automatically populated from the current branch name
- **Manual action** — "Format Branch to Commit Message" action available in the commit message toolbar
- **Tool Window** — dedicated "Branch Commit" panel showing the current branch and formatted message with a copy button
- **Customizable pattern** — configure the regex pattern and output format via Settings
- **Live preview** — test your pattern configuration with instant preview
- **Enable/disable toggle** — turn auto-fill on or off as needed
- **Reset to defaults** — one-click reset to default configuration

## Installation

### From JetBrains Marketplace

Search for **"Branch Commit Formatter"** in your IDE: Settings > Plugins > Marketplace.

### From Disk

1. Download the latest `.zip` from [Releases](../../releases)
2. In your IDE: Settings > Plugins > gear icon > Install Plugin from Disk...
3. Select the `.zip` file and restart

## Configuration

Go to **Settings > Tools > Branch Commit Formatter** to customize:

| Setting | Default | Description |
|---|---|---|
| Enable auto-fill | `true` | Toggle auto-fill on commit dialog open |
| Branch regex pattern | `^(?:.+/)?([A-Za-z]+-\d+)[_-](.+)$` | Regex with groups: (1) ticket ID, (2) description |
| Commit message format | `[{ticket}] {description}` | Output template using `{ticket}` and `{description}` |
| Replace separators | `true` | Replace underscores and dashes with spaces in description |

## Building from Source

```bash
./gradlew buildPlugin
```

Output: `build/distributions/branch-commit-formatter-*.zip`

## License

[MIT](LICENSE)

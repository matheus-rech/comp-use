# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is Anthropic's reference implementation for Claude Computer Use - a beta feature that allows Claude to interact with a computer through screenshots, keyboard, and mouse control. The demo runs inside a Docker container with an Ubuntu desktop environment.

## Development Commands

```bash
# Initial setup (creates venv, installs deps, sets up pre-commit)
./setup.sh

# Build Docker image locally
docker build . -t computer-use-demo:local

# Run container for development (mounts local code for hot-reload)
docker run \
    -e ANTHROPIC_API_KEY=$ANTHROPIC_API_KEY \
    -v $(pwd)/computer_use_demo:/home/computeruse/computer_use_demo/ \
    -v $HOME/.anthropic:/home/computeruse/.anthropic \
    -p 5900:5900 -p 8501:8501 -p 6080:6080 -p 8080:8080 \
    -it computer-use-demo:local

# Lint
ruff check .

# Format
ruff format .

# Type check (requires pyright, not in dev-requirements)
pyright

# Run all tests
pytest

# Run single test
pytest tests/tools/computer_test.py::test_screenshot -v
```

## Code Style

- Use `ruff` for linting and formatting (configured in `ruff.toml`)
- Imports: use `isort` with `combine-as-imports`
- Types: add type annotations for all parameters and returns
- Classes: use `@dataclass(kw_only=True, frozen=True)` for data classes
- Error handling: raise `ToolError` for recoverable tool failures

## Architecture

### Agent Loop (`loop.py`)
The core agentic sampling loop that:
- Calls Claude API (supports Anthropic, Bedrock, Vertex providers)
- Sends tool definitions and receives tool use requests
- Executes tools locally and returns results with screenshots
- Manages prompt caching and image truncation for efficiency

### Tools (`computer_use_demo/tools/`)
Anthropic-defined tools that Claude can invoke:

- **`computer.py`**: Screen interaction via xdotool - mouse clicks, keyboard input, screenshots. Handles coordinate scaling between API resolution (XGA/WXGA) and actual display.

- **`bash.py`**: Shell command execution with timeout handling.

- **`edit.py`**: File editing with `str_replace_based_edit_tool` (replaces text patterns in files).

- **`collection.py`**: `ToolCollection` aggregates tools and routes execution.

- **`groups.py`**: `ToolGroup` defines version-specific tool combinations and beta flags. Three versions exist:
  - `computer_use_20241022` - Original tools for Claude 3.5 Sonnet
  - `computer_use_20250124` - Enhanced tools with scroll, hold_key, wait actions for Claude 3.7+
  - `computer_use_20250429` - Updated edit tool for Claude 4

### Streamlit UI (`streamlit.py`)
Web interface for interacting with the agent. Auto-reloads when code changes. Model configurations map model IDs to tool versions and token limits.

## Key Patterns

### Tool Implementation
All tools inherit from `BaseAnthropicTool` and must implement:
- `__call__(**kwargs)` - async execution returning `ToolResult`
- `to_params()` - returns `BetaToolUnionParam` for API

### Error Handling
Raise `ToolError` for recoverable tool failures - these become error results in the conversation without crashing the loop. Use `ToolResult` dataclass for tool outputs with optional `output`, `error`, `base64_image`, and `system` fields.

### Resolution Scaling
The computer tool scales coordinates between model-facing resolution (max 1024x768 for XGA) and actual screen resolution. This prevents issues with image resizing in the API.

### Model Configuration
`streamlit.py` contains `MODEL_TO_MODEL_CONF` mapping model IDs to `ModelConfig` dataclasses that specify:
- `tool_version`: which tool group to use
- `max_output_tokens` / `default_output_tokens`: token limits
- `has_thinking`: whether extended thinking is supported

## Environment Variables

- `WIDTH`, `HEIGHT` - Screen dimensions (default 1024x768)
- `DISPLAY_NUM` - X11 display number
- `ANTHROPIC_API_KEY` - API key for Anthropic provider
- `API_PROVIDER` - `anthropic`, `bedrock`, or `vertex`

## Access Points (when container running)

- Combined UI: http://localhost:8080
- Streamlit only: http://localhost:8501
- noVNC desktop: http://localhost:6080/vnc.html
- VNC direct: vnc://localhost:5900

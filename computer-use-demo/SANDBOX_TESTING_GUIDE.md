# Sandboxed Computer Use Environment - Testing & Validation Guide

## Overview

This guide covers how to test and validate the Docker-based sandboxed desktop environment for Claude Computer Use with TSA (Trial Sequential Analysis).

## Quick Start

### Option A: Pull Pre-built Image (Recommended)

Pull the pre-built image from GitHub Container Registry - much faster than building locally:

```bash
# Login to GHCR (use your GitHub username and a Personal Access Token)
echo $GITHUB_TOKEN | docker login ghcr.io -u YOUR_USERNAME --password-stdin

# Pull the image
docker pull ghcr.io/matheus-rech/comp-use:latest

# Tag for easier use
docker tag ghcr.io/matheus-rech/comp-use:latest computer-use-tsa:local
```

### Option B: Build Locally

Only if you need to modify the Dockerfile (takes 20-60+ minutes):

```bash
cd ~/claude-quickstarts/computer-use-demo
docker build -f Dockerfile.tsa -t computer-use-tsa:local .
```

### Run the Container

```bash
docker run \
    -e ANTHROPIC_API_KEY=$ANTHROPIC_API_KEY \
    -v $(pwd)/computer_use_demo:/home/computeruse/computer_use_demo/ \
    -p 5900:5900 \
    -p 8501:8501 \
    -p 6080:6080 \
    -p 8080:8080 \
    -it computer-use-tsa:local
```

### 3. Access Points

| Service | URL/Port | Description |
|---------|----------|-------------|
| Combined UI | http://localhost:8080 | Full interface with desktop + chat |
| Streamlit | http://localhost:8501 | Chat interface only |
| noVNC | http://localhost:6080/vnc.html | Desktop view in browser |
| VNC Direct | vnc://localhost:5900 | Native VNC client |

---

## Validation Checklist

### Phase 1: Container Basics

```bash
# Check container is running
docker ps | grep computer-use-tsa

# Check container logs
docker logs <container_id>

# Exec into container for debugging
docker exec -it <container_id> bash
```

**Expected:**
- [ ] Container starts without errors
- [ ] No crash loops or restarts
- [ ] Logs show X11, VNC, and Streamlit starting

### Phase 2: Desktop Environment

1. **Open noVNC** at http://localhost:6080/vnc.html

**Expected:**
- [ ] Desktop loads with tint2 panel
- [ ] Mouse cursor is visible and responsive
- [ ] Right-click shows context menu

2. **Test File Manager (Thunar)**

```bash
# Inside container
thunar &
```

**Expected:**
- [ ] Thunar opens without errors
- [ ] Can navigate directories
- [ ] Can create/delete files

3. **Test TSA Application**

```bash
# Inside container
~/launch_tsa.sh
```

**Expected:**
- [ ] TSA Java application launches
- [ ] GUI renders correctly
- [ ] No Java exceptions in console

### Phase 3: Computer Use Integration

1. **Open Streamlit UI** at http://localhost:8501

2. **Test Screenshot Capability**
   - Send: "Take a screenshot of the desktop"

**Expected:**
- [ ] Claude receives screenshot
- [ ] Image is clear and correct resolution

3. **Test Mouse Control**
   - Send: "Click on the file manager icon in the panel"

**Expected:**
- [ ] Mouse moves to correct location
- [ ] Click registers
- [ ] File manager opens

4. **Test Keyboard Input**
   - Send: "Open a terminal and type 'echo hello'"

**Expected:**
- [ ] Terminal opens
- [ ] Text is typed correctly
- [ ] Command executes

5. **Test TSA Interaction**
   - Send: "Open the TSA application and describe what you see"

**Expected:**
- [ ] TSA launches
- [ ] Claude can describe the interface
- [ ] Buttons/menus are clickable

---

## Sandbox Security Validation

### File System Isolation

```bash
# From host - check container can't access host files
docker exec <container_id> ls /host  # Should fail or be empty

# Check writable areas are contained
docker exec <container_id> touch /test.txt  # Should fail (read-only root)
docker exec <container_id> touch ~/test.txt  # Should succeed (home writable)
```

### Network Isolation (if configured)

```bash
# Check container network
docker exec <container_id> curl -s https://example.com
# Depending on config, may be blocked or allowed
```

### Process Isolation

```bash
# Container processes should be isolated
docker top <container_id>
# Should only show container processes, not host
```

---

## Troubleshooting

### Container Won't Start

```bash
# Check for port conflicts
lsof -i :5900
lsof -i :8501
lsof -i :6080
lsof -i :8080

# Kill conflicting processes
kill -9 <pid>
```

### Black Screen in VNC

```bash
# Check X11 is running
docker exec <container_id> pgrep -a Xvfb

# Restart X11 if needed
docker exec <container_id> pkill Xvfb
docker exec <container_id> Xvfb :1 -screen 0 1024x768x24 &
```

### TSA Won't Launch

```bash
# Check Java
docker exec <container_id> java -version

# Check TSA files exist
docker exec <container_id> ls -la ~/TSA/

# Run with debug output
docker exec <container_id> bash -c "cd ~/TSA && java -jar TSA.jar 2>&1"
```

### Thunar Won't Open

```bash
# Check if installed
docker exec <container_id> which thunar

# Try with debug
docker exec <container_id> thunar --display=:1 2>&1
```

---

## Performance Tuning

### Increase Resolution

```bash
docker run \
    -e WIDTH=1920 \
    -e HEIGHT=1080 \
    -e ANTHROPIC_API_KEY=$ANTHROPIC_API_KEY \
    ...
```

### Reduce Latency

```bash
# Use direct VNC instead of noVNC for lower latency
# macOS: open vnc://localhost:5900
# Linux: vncviewer localhost:5900
```

### Memory Limits

```bash
docker run \
    --memory=4g \
    --memory-swap=4g \
    ...
```

---

## MCP Server Integration (Advanced)

If using the MCP-based sandboxed Computer Use:

### Test File Sync

```bash
# Copy file into container
python scripts/file_sync.py push local_file.txt /home/computeruse/file.txt

# Copy file out
python scripts/file_sync.py pull /home/computeruse/output.txt ./output.txt
```

### Test Clipboard Sync

```bash
# Copy to container clipboard
echo "test content" | python scripts/clipboard_sync.py push

# Get container clipboard
python scripts/clipboard_sync.py pull
```

### Test Screenshot Diff

```bash
# Capture screenshot
python scripts/screenshot_diff.py capture base.png

# Compare after action
python scripts/screenshot_diff.py diff base.png current.png
```

---

## Automated Test Script

Save as `test_sandbox.sh`:

```bash
#!/bin/bash
set -e

CONTAINER_NAME="computer-use-tsa-test"
IMAGE="computer-use-tsa:local"

echo "=== Starting Container ==="
docker run -d --name $CONTAINER_NAME \
    -p 5900:5900 -p 8501:8501 -p 6080:6080 -p 8080:8080 \
    $IMAGE

sleep 10  # Wait for services to start

echo "=== Testing VNC ==="
nc -zv localhost 5900 && echo "VNC: OK" || echo "VNC: FAIL"

echo "=== Testing Streamlit ==="
curl -s http://localhost:8501 > /dev/null && echo "Streamlit: OK" || echo "Streamlit: FAIL"

echo "=== Testing noVNC ==="
curl -s http://localhost:6080 > /dev/null && echo "noVNC: OK" || echo "noVNC: FAIL"

echo "=== Testing Thunar ==="
docker exec $CONTAINER_NAME which thunar && echo "Thunar: OK" || echo "Thunar: FAIL"

echo "=== Testing TSA ==="
docker exec $CONTAINER_NAME test -f ~/TSA/TSA.jar && echo "TSA jar: OK" || echo "TSA jar: FAIL"

echo "=== Cleanup ==="
docker stop $CONTAINER_NAME
docker rm $CONTAINER_NAME

echo "=== Tests Complete ==="
```

Run with:
```bash
chmod +x test_sandbox.sh
./test_sandbox.sh
```

---

## Success Criteria

The sandboxed environment is validated when:

1. **Container Health**: Starts and runs without errors
2. **Desktop Access**: VNC/noVNC shows working desktop
3. **File Manager**: Thunar opens and navigates correctly
4. **TSA Application**: Java app launches and is interactive
5. **Computer Use**: Claude can take screenshots, click, and type
6. **Isolation**: Container is properly sandboxed from host

---

## Next Steps

After validation:
- Configure persistent volumes for data
- Set up MCP server for programmatic control
- Integrate with your Claude Code workflow
- Add custom applications as needed

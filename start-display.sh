#!/usr/bin/env bash
# ============================================================================
#  start-display.sh — bring up a virtual display + VNC and KEEP IT RUNNING.
#
#  Run this in its OWN terminal. It stays open. Then:
#    1. In another terminal:  vncviewer localhost:5900
#    2. In another terminal:  ./run-tests-visible.sh   (or a specific test)
#
#  Press Ctrl+C in THIS terminal when you're done to shut the display down.
# ============================================================================
set -u

DISP=":99"
SCREEN="1920x1080x24"
VNC_PORT="5900"

# x11vnc detects WAYLAND_DISPLAY/XDG_SESSION_TYPE and refuses to start ("Wayland
# display server detected ... Exiting"), even when told to serve an Xvfb display.
# Unset those so all tools treat :99 as a plain X11 display.
unset WAYLAND_DISPLAY
export XDG_SESSION_TYPE=x11

cleanup() {
    echo; echo ">> Shutting down display/VNC ..."
    pkill -f "x11vnc -display $DISP" 2>/dev/null
    pkill -f "fluxbox"               2>/dev/null
    pkill -f "Xvfb $DISP"            2>/dev/null
    rm -f "/tmp/.X${DISP#:}-lock" 2>/dev/null
    echo ">> Done."
}
trap cleanup EXIT INT TERM

# clear any stale display
pkill -f "Xvfb $DISP" 2>/dev/null
rm -f "/tmp/.X${DISP#:}-lock" 2>/dev/null
sleep 1

echo ">> Starting Xvfb on $DISP ($SCREEN) ..."
Xvfb "$DISP" -screen 0 "$SCREEN" >/tmp/xvfb.log 2>&1 &
sleep 2

echo ">> Starting fluxbox window manager ..."
DISPLAY="$DISP" fluxbox >/tmp/fluxbox.log 2>&1 &
sleep 1

echo ">> Starting x11vnc on port $VNC_PORT ..."
x11vnc -display "$DISP" -rfbport "$VNC_PORT" -localhost -forever -shared -nopw >/tmp/x11vnc.log 2>&1 &
sleep 2

echo
echo "=================================================================="
echo "  Virtual display $DISP is UP and will stay running."
echo
echo "  1) Connect your viewer:   vncviewer localhost:$VNC_PORT"
echo "  2) Run tests (new term):  ./run-tests-visible.sh"
echo
echo "  Leave THIS terminal open. Press Ctrl+C here when finished."
echo "=================================================================="
echo

# keep the script alive so the trap-based cleanup only runs on Ctrl+C
while true; do sleep 3600; done

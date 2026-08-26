#!/usr/bin/env bash
# ============================================================================
#  run-tests-visible.sh — run the suite on the already-running virtual display.
#
#  PREREQUISITE: start-display.sh must be running in another terminal, and you
#  should have connected with:  vncviewer localhost:5900
#
#  USAGE:
#     ./run-tests-visible.sh                        # whole suite
#     ./run-tests-visible.sh LoginTest              # one class
#     ./run-tests-visible.sh "LoginTest#testValidLogin"   # one method
# ============================================================================
set -u

export PATH="$PATH:$HOME/tools/apache-maven-3.9.9/bin"
DISP=":99"
TEST_FILTER="${1:-}"

# Chrome under a Wayland session ignores DISPLAY and uses the Wayland socket,
# so it opens on the REAL desktop instead of our virtual :99. Force X11 and
# remove the Wayland handle so the browser renders into :99 (visible via VNC).
unset WAYLAND_DISPLAY
export XDG_SESSION_TYPE=x11
export GDK_BACKEND=x11
export OZONE_PLATFORM=x11

# sanity check that the display is actually up
if [ ! -e "/tmp/.X${DISP#:}-lock" ]; then
    echo "!! Virtual display $DISP is not running."
    echo "   Start it first:  ./start-display.sh   (in another terminal)"
    exit 1
fi

MVN_ARGS=(test -Dheadless=false)
if [ -n "$TEST_FILTER" ]; then
    MVN_ARGS+=("-Dtest=$TEST_FILTER")
fi

echo ">> Running on display $DISP (watch it in your VNC viewer):"
echo ">> mvn ${MVN_ARGS[*]}"
echo
export DISPLAY="$DISP"
mvn "${MVN_ARGS[@]}"

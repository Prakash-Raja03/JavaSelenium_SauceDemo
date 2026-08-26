# Watching the Selenium tests run

Two ways to watch the tests execute in Chrome. Both need Maven on PATH:
    export PATH=$PATH:~/tools/apache-maven-3.9.9/bin

------------------------------------------------------------------------
OPTION A — Watch DIRECTLY in Chrome on your real screen  (simplest)
------------------------------------------------------------------------
One terminal. Chrome opens on your actual desktop.

    cd ~/Documents/Interview-Prep/selenium-testng-framework

    mvn test -Dheadless=false                              # whole suite
    mvn test -Dheadless=false -Dtest=LoginTest             # one class
    mvn test -Dheadless=false -Dtest=LoginTest#testValidLogin   # one method

------------------------------------------------------------------------
OPTION B — Watch through VNC (isolated virtual display)
------------------------------------------------------------------------
Three terminals. Chrome renders inside the VNC window, not your desktop.

  Terminal 1 — start the virtual display (leave it open):
    cd ~/Documents/Interview-Prep/selenium-testng-framework
    ./start-display.sh
    # wait for the "Virtual display :99 is UP" banner

  Terminal 2 — connect the viewer:
    vncviewer localhost:5900

  Terminal 3 — run the tests:
    cd ~/Documents/Interview-Prep/selenium-testng-framework
    ./run-tests-visible.sh                       # whole suite
    ./run-tests-visible.sh LoginTest             # one class
    ./run-tests-visible.sh "LoginTest#testValidLogin"   # one method

  When finished: press Ctrl+C in Terminal 1 to shut the display down.

------------------------------------------------------------------------
NORMAL / CI runs (no browser window, fastest, default)
------------------------------------------------------------------------
    mvn test

Optional: capture a PNG of each step into screenshots/ for later review:
    mvn test -DscreenshotSteps=true

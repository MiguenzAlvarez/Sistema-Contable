#!/bin/sh
set -eu

Xvfb :0 -screen 0 1440x900x24 -ac +extension GLX +render -noreset &
openbox >/tmp/openbox.log 2>&1 &
x11vnc -display :0 -forever -shared -nopw -rfbport 5900 >/tmp/x11vnc.log 2>&1 &
websockify --web=/usr/share/novnc 6080 localhost:5900 >/tmp/websockify.log 2>&1 &

exec java -jar /app/sistema-contable.jar

#!/bin/sh
set -eu

# Docker reinicia el proceso principal, pero los procesos auxiliares de la
# sesión gráfica pueden quedar vivos. Se limpian antes de iniciar la sesión.
pkill -x Xvfb 2>/dev/null || true
rm -f /tmp/.X0-lock /tmp/.X11-unix/X0

cleanup() {
    for pid in "${java_pid:-}" "${websockify_pid:-}" "${vnc_pid:-}" "${openbox_pid:-}" "${xvfb_pid:-}"; do
        [ -n "$pid" ] && kill "$pid" 2>/dev/null || true
    done
}
trap cleanup INT TERM EXIT

Xvfb :0 -screen 0 1440x900x24 -ac +extension GLX +render -noreset &
xvfb_pid=$!
openbox >/tmp/openbox.log 2>&1 &
openbox_pid=$!
x11vnc -display :0 -forever -shared -nopw -rfbport 5900 >/tmp/x11vnc.log 2>&1 &
vnc_pid=$!
websockify --web=/usr/share/novnc 6080 localhost:5900 >/tmp/websockify.log 2>&1 &
websockify_pid=$!

java -jar /app/sistema-contable.jar &
java_pid=$!
wait "$java_pid"

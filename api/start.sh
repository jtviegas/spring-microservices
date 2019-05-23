#!/bin/sh

this_folder="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
parent_folder=$(dirname $this_folder)

whereis java
ls

debug_switch=""
if [ ! -z "$DEBUG_PORT" ]; then
	debug_switch="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$DEBUG_PORT"
fi

java $debug_switch -jar /opt/app/app.jar

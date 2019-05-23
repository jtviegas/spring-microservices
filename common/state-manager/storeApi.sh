#!/bin/sh

this_folder="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
parent_folder=$(dirname $this_folder)
grand_parent_folder=$(dirname $parent_folder)

START_SCRIPT=$grand_parent_folder/store-api/runContainer.sh
STOP_SCRIPT=$grand_parent_folder/store-api/stopContainer.sh

usage()
{
        cat <<EOM
        usage:
        $(basename $0) start|stop
EOM
        exit 1
}

[ -z $1 ] && { usage; }
[ "$1" != "start" -a "$1" != "stop" ] && { usage; }

echo "going to $1 the store api container..."

if [ "$1" == "start" ]
then
    $START_SCRIPT
    echo "...waiting for the api to load..."
    sleep 16
else
    $STOP_SCRIPT
fi

echo "....done."




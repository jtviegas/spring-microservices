#!/usr/bin/env bash

RESOURCE_GROUP=kubone
SUBSCRIPTION="Free Trial"
CLUSTER=clusterone

this_folder=$(dirname $(readlink -f $0))
if [ -z $this_folder ]; then
	this_folder="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
fi
parent_folder=$(dirname $this_folder)

AZURE_SANDBOX_SUBSCRIPTION=a4fe28da-0262-4b49-a9ea-7f2bba03f85b
NS=jtv009-sandbox-001

az login
az account set -s $AZURE_SANDBOX_SUBSCRIPTION
k -n $NS get all

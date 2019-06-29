#!/usr/bin/env bash

AZURE_SANDBOX_SUBSCRIPTION=a4fe28da-0262-4b49-a9ea-7f2bba03f85b
NS=jtv009-sandbox-001

az login
az account set -s $AZURE_SANDBOX_SUBSCRIPTION
k -n $NS get all

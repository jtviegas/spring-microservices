#!/usr/bin/env bash

LOG_TRACE=1

RESOURCE_GROUP=kubone
SUBSCRIPTION="Free Trial"
CLUSTER=clusterone

this_folder=$(dirname $(readlink -f $0))
if [ -z $this_folder ]; then
	this_folder="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
fi
parent_folder=$(dirname $this_folder)

. "${this_folder}"/include.sh
. "${this_folder}"/commons.sh

# az aks browse --resource-group kubone --name clusterone

sys_init()
{
  goin "[sys_init]"
  info "starting azure cluster config..."
  local __r=0

  az login
  az account set -s "$SUBSCRIPTION"
  if [ ! "$?" -eq "0" ]; then
      info "...could not set subscription"
      __r=1
  fi

  if [ "$__r" -eq "0" ]; then
    az aks get-credentials --resource-group $RESOURCE_GROUP --subscription "$SUBSCRIPTION" --name $CLUSTER --overwrite-existing
    if [ ! "$?" -eq "0" ]; then
      __r=1
    fi
  fi

  if [ "$__r" -eq "0" ]; then
    kubectl get namespaces | grep $NAMESPACE
    if [ ! "$?" -eq "0" ]; then
      kubectl create namespace $NAMESPACE
      if [ ! "$?" -eq "0" ]; then
          info "...could not get create cluster namespace $NAMESPACE"
          __r=1
      else
        info "...created namespace $NAMESPACE..."
      fi
    fi
  else
    info "...there is a namespace $NAMESPACE..."
  fi

  info "...finished azure cluster config"
  goout "[sys_init]" "$__r"
  return $__r
}

sys_destroy()
{
  goin "[sys_destroy]"
  local __r=0

  kubectl delete namespace $NAMESPACE
  if [ ! "$?" -eq "0" ]; then
      info "...could not delete cluster namespace $NAMESPACE"
      __r=1
  else
      info "...deleted cluster namespace $NAMESPACE"
  fi

  kubectl config delete-context $CONTEXT
  if [ ! "$?" -eq "0" ]; then
      info "...could not delete context $CONTEXT"
      __r=1
  else
      info "...deleted context $CONTEXT"
  fi

  goout "[sys_destroy]" "$__r"
  return $__r
}

connect(){
  goin "[connect]"
  local __r=0

  if [ "$__r" -eq "0" ]; then
    kubectl config set-context $CONTEXT --namespace=$NAMESPACE --cluster=$CLUSTER --user=$USER
    if [ ! "$?" -eq "0" ]; then
      __r=1
    fi
  fi

  goout "[connect]" "$__r"
  return $__r
}

services_init()
{
  goin "[services_init]"
  info "starting services..."
  _pwd=`pwd`
  cd "$this_folder"

  local __r=0

  connect
  if [ ! "$?" -eq "0" ]; then
      __r=1
  fi

  if [ "$__r" -eq "0" ]; then
    kubectl apply -f $STORE_DPL
    if [ ! "$?" -eq "0" ]; then
        echo "...couldn't apply $STORE_DPL !!!"
        __r=1
    else
        kubectl get deployments -n $NAMESPACE
        kubectl describe deployments/$STORE -n $NAMESPACE
        echo "...applied $STORE_DPL !"
    fi
  fi

  cd "$_pwd"
  info "...started services."
  goout "[services_init]" "$__r"
  return $__r
}

services_destroy()
{
  goin "[services_destroy]"
  info "stopping services..."
  _pwd=`pwd`
  cd "$this_folder"

  local __r=0

  connect
  if [ ! "$?" -eq "0" ]; then
      __r=1
  fi

  kubectl delete deployments $STORE  -n $NAMESPACE
  if [ ! "$?" -eq "0" ]; then
      echo "...couldn't delete deployment $STORE !!!"
      __r=1
  else
      echo "...deleted deployment $STORE !"
  fi


  cd "$_pwd"
  info "...stopped services."
  goout "[services_destroy]" "$__r"
  return $__r
}

usage()
{
  cat <<EOM
  usage:
  $(basename $0) {sys} {init|destroy}
EOM
  exit 1
}

[ -z $2 ] && { usage; }

case "$1" in
        sys)
            case "$2" in
                    init)
                      sys_init
                      ;;
                    destroy)
                      sys_destroy
                      ;;
                    *)
                      usage
            esac
            ;;
        services)
            case "$2" in
                    init)
                      services_init
                      ;;
                    destroy)
                      services_destroy
                      ;;
                    *)
                      usage
            esac
            ;;
        *)
            usage
esac













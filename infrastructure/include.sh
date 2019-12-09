export CONTEXT_DEFAULT=minikube
DEFAULT_SUBSCRIPTION="Free Trial"
DEFAULT_RESOURCE_GROUP=kubone
DEFAULT_CLUSTER=clusterone
DEFAULT_NAMESPACE=lab
DEFAULT_USER=clusterUser_kubone_clusterone
DEFAULT_CONTEXT=clusterone

if [ -z "$SUBSCRIPTION" ]; then
    export SUBSCRIPTION="$DEFAULT_SUBSCRIPTION"
fi

if [ -z $RESOURCE_GROUP ]; then
    export RESOURCE_GROUP="$DEFAULT_RESOURCE_GROUP"
fi

if [ -z $CLUSTER ]; then
    export CLUSTER="$DEFAULT_CLUSTER"
fi

if [ -z $NAMESPACE ]; then
    export NAMESPACE="$DEFAULT_NAMESPACE"
fi

if [ -z $USER ]; then
    export USER="$DEFAULT_USER"
fi

if [ -z $CONTEXT ]; then
    export CONTEXT="$DEFAULT_CONTEXT"
fi

export STORE=store
export STORE_DPL=store.yaml

  az aks get-credentials --resource-group kubone --subscription "Free Trial" --name clusterone --overwrite-existing

export SUBSCRIPTION="Free Trial"
export RESOURCE_GROUP=kubone
export CLUSTER=clusterone
export NAMESPACE=lab
export USER=clusterUser_kubone_clusterone
export CONTEXT=clusterone
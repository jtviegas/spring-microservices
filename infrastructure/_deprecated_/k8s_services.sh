#!/bin/sh

this_folder="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
parent_folder=$(dirname $this_folder)

CONTEXT_DEFAULT=minikube
CLUSTER=minikube
USER=minikube


CONTEXT=dev
CONFIGMAP=configuration
CONFIGMAP_SPEC=$this_folder/configuration.properties

STORE_DEPLOYMENT=$this_folder/store.yaml
STORE_NAME=store
STORE_SVC=store-service
STORE_SVC_SPEC=$this_folder/store-svc.yaml

SOLVER_DEPLOYMENT=$this_folder/solver.yaml
SOLVER_NAME=solver
SOLVER_SVC=solver-service
SOLVER_SVC_SPEC=$this_folder/solver-svc.yaml

API_DEPLOYMENT=$this_folder/api.yaml
API_NAME=api
API_SVC=api-service
API_SVC_SPEC=$this_folder/api-svc.yaml

LOCAL_NS=local
AZURE_SANDBOX_NS=jtv009-sandbox-001

# parameter check
usage()
{
        cat <<EOM
        usage:
        $(basename $0) deploy | undeploy [local|azure]
EOM
        exit 1
}

[ -z $1 ] && { usage; }

createService(){

    local __spec=$1
    local __ns=$2
    local __name=$3
    echo "creating service $__name ..."
    local __r=0

    kubectl create -f $__spec -n $__ns
    if [ ! "$?" -eq "0" ]; then
        echo "...couldn't create service $__name !!!"
        __r=1
    else
        kubectl get services -n $__ns
        kubectl describe services/$__name -n $__ns
        echo "...created service $__name !"
    fi
    return $__r
}

deleteService(){
    local __name=$1
    local __ns=$2

    echo "deleting service $__name ..."
    local __r=0
    kubectl delete svc $__name -n $__ns
    if [ ! "$?" -eq "0" ]; then
        echo "...couldn't delete service $__name !!!"
        __r=1
    else
        echo "...deleted service $__name !"
    fi
    return $__r
}

createDeployment(){

    local __spec=$1
    local __ns=$2
    local __name=$3
    echo "creating deployment $__name ..."
    local __r=0

    kubectl create -f $__spec -n $__ns
    if [ ! "$?" -eq "0" ]; then
        echo "...couldn't create deployment $__name !!!"
        __r=1
    else
        kubectl get deployments -n $__ns
        kubectl describe deployments/$__name -n $__ns
        echo "...created deployment $__name !"
    fi
    return $__r
}

deleteDeployment(){
    local __name=$1
    local __ns=$2

    echo "deleting deployment $__name ..."
    local __r=0
    kubectl delete deployments $__name -n $__ns
    if [ ! "$?" -eq "0" ]; then
        echo "...couldn't delete deployment $__name !!!"
        __r=1
    else
        echo "...deleted deployment $__name !"
    fi
    return $__r
}


createConfigmap(){

    local __spec=$1
    local __name=$2
    local __ns=$3

    echo "creating configmap $__name ..."
    local __r=0

    kubectl create configmap $__name --from-env-file=$__spec -n $__ns
    if [ ! "$?" -eq "0" ]; then
        echo "...couldn't create configmap $__name !!!"
        __r=1
    else
        echo "...created configmap $__name !"
    fi
    return $__r

}

deleteConfigmap(){
    local __name=$1
    local __ns=$2

    echo "deleting configmap $__name ..."
    local __r=0
    kubectl delete configmap $__name -n $__ns
    if [ ! "$?" -eq "0" ]; then
        echo "...couldn't delete configmap $__name !!!"
        __r=1
    else
        echo "...deleted configmap $__name !"
    fi
    return $__r
}


createNamespace(){
    local __ns=$1
    echo "creating namespace $__ns ..."
    local __r=0
    kubectl create namespace $__ns
    if [ ! "$?" -eq "0" ]; then
        echo "...couldn't create namespace $__ns !!!"
        __r=1
    else
        echo "...created namespace $__ns !"
    fi
    return $__r
}

createNamespaceFromSpec(){
    local __ns=$1
    local __spec=$2
    echo "creating namespace $__ns from spec $__spec..."
    local __r=0
    kubectl create -f $__spec
    if [ ! "$?" -eq "0" ]; then
        echo "...couldn't create namespace $__ns !!!"
        __r=1
    else
        echo "...created namespace $__ns !"
    fi
    return $__r
}

deleteNamespace(){
    local __ns=$1
    echo "deleting namespace $__ns ..."
    local __r=0
    kubectl delete namespace $__ns
    if [ ! "$?" -eq "0" ]; then
        echo "...couldn't delete namespace $__ns !!!"
        __r=1
    else
        echo "...deleted namespace $__ns !"
    fi
    return $__r
}

setupContext(){

    local __ctx=$1
    local __ns=$2
    local __cluster=$3
    local __user=$4

    echo "setting context $__ctx ..."
    local __r=0
    kubectl config set-context $__ctx --namespace=$__ns --cluster=$__cluster --user=$__user
    if [ ! "$?" -eq "0" ]; then
        echo "...couldn't set context $__ctx !!!"
        __r=1
    else
        echo "setting up use of context $__ctx ..."
        kubectl config use-context $__ctx
        if [ ! "$?" -eq "0" ]; then
            echo "...couldn't setup the use of context $__ctx !!!"
            __r=1
        else
            echo "...setup of context $__ctx use done !"
        fi

    fi
    return $__r
}

resetContext(){

    local __ctx=$1

    echo "resetting context $CONTEXT_DEFAULT ..."
    local __r=0

    kubectl config use-context $CONTEXT_DEFAULT
    if [ ! "$?" -eq "0" ]; then
        echo "...could not reset to use context $CONTEXT_DEFAULT"
        __r=1
    else
        echo "...now using context $CONTEXT_DEFAULT"
    fi

    kubectl config delete-context $__ctx
    if [ ! "$?" -eq "0" ]; then
        echo "...could not delete context $__ctx"
        if [ "$__r" -eq "0" ]; then
            __r=1
        fi
    else
        echo "...deleted context $__ctx"
    fi

    return $__r
}

undeploy_local()
{
    echo "undeploying locally..."
    local __r=0

    deleteService $STORE_SVC $LOCAL_NS
    if [ ! "$?" -eq "0"  -a "$__r" -eq "0" ]; then
        __r=1
    fi

    deleteDeployment $STORE_NAME $LOCAL_NS
    if [ ! "$?" -eq "0"  -a "$__r" -eq "0" ]; then
        __r=1
    fi

    deleteConfigmap $CONFIGMAP $LOCAL_NS
    if [ ! "$?" -eq "0"  -a "$__r" -eq "0" ]; then
        __r=1
    fi

    resetContext $CONTEXT
    if [ ! "$?" -eq "0"  -a "$__r" -eq "0" ]; then
        __r=1
    fi

    deleteNamespace $LOCAL_NS
    if [ ! "$?" -eq "0"  -a "$__r" -eq "0" ]; then
        __r=1
    fi

    echo "...undeploying locally finished ($__r)"
    return $__r
}

deploy_local()
{
    echo "deploying locally..."
    local __r=0

    createNamespace $LOCAL_NS
    __r=$?

    if [ "$__r" -eq "0" ]; then
        setupContext $CONTEXT $LOCAL_NS $CLUSTER $USER
        __r=$?
    fi

    if [ "$__r" -eq "0" ]; then
        createConfigmap $CONFIGMAP_SPEC $CONFIGMAP $LOCAL_NS
        __r=$?
    fi

    if [ "$__r" -eq "0" ]; then
        createDeployment $STORE_DEPLOYMENT $LOCAL_NS $STORE_NAME
        __r=$?
    fi

    if [ "$__r" -eq "0" ]; then
        createService $STORE_SVC_SPEC $LOCAL_NS $STORE_SVC
        __r=$?
    fi

    echo "...deploying locally finished ($__r)"
    return $__r
}

undeploy_azure()
{
    echo "undeploying on azure..."
    local __r=0

    deleteService $API_SVC $AZURE_SANDBOX_NS
    if [ ! "$?" -eq "0"  -a "$__r" -eq "0" ]; then
        __r=1
    fi

    deleteDeployment $API_NAME $AZURE_SANDBOX_NS
    if [ ! "$?" -eq "0"  -a "$__r" -eq "0" ]; then
        __r=1
    fi

    deleteService $SOLVER_SVC $AZURE_SANDBOX_NS
    if [ ! "$?" -eq "0"  -a "$__r" -eq "0" ]; then
        __r=1
    fi

    deleteDeployment $SOLVER_NAME $AZURE_SANDBOX_NS
    if [ ! "$?" -eq "0"  -a "$__r" -eq "0" ]; then
        __r=1
    fi

    deleteService $STORE_SVC $AZURE_SANDBOX_NS
    if [ ! "$?" -eq "0"  -a "$__r" -eq "0" ]; then
        __r=1
    fi

    deleteDeployment $STORE_NAME $AZURE_SANDBOX_NS
    if [ ! "$?" -eq "0"  -a "$__r" -eq "0" ]; then
        __r=1
    fi

    deleteConfigmap $CONFIGMAP $AZURE_SANDBOX_NS
    if [ ! "$?" -eq "0"  -a "$__r" -eq "0" ]; then
        __r=1
    fi

    echo "...undeploying on azure finished ($__r)"
    return $__r
}

deploy_azure()
{
    echo "deploying on azure..."
    local __r=0

    kubectl get pods -n $AZURE_SANDBOX_NS
    if [ ! "$?" -eq "0" ]; then
        echo "...could not query the namespace !!!"
        __r=1
    fi

    if [ "$__r" -eq "0" ]; then
        createConfigmap $CONFIGMAP_SPEC $CONFIGMAP $AZURE_SANDBOX_NS
        __r=$?
    fi

    if [ "$__r" -eq "0" ]; then
        createDeployment $STORE_DEPLOYMENT $AZURE_SANDBOX_NS $STORE_NAME
        __r=$?
    fi

    if [ "$__r" -eq "0" ]; then
        createService $STORE_SVC_SPEC $AZURE_SANDBOX_NS $STORE_SVC
        __r=$?
    fi

    if [ "$__r" -eq "0" ]; then
        createDeployment $SOLVER_DEPLOYMENT $AZURE_SANDBOX_NS $SOLVER_NAME
        __r=$?
    fi

    if [ "$__r" -eq "0" ]; then
        createService $SOLVER_SVC_SPEC $AZURE_SANDBOX_NS $SOLVER_SVC
        __r=$?
    fi

    if [ "$__r" -eq "0" ]; then
        createDeployment $API_DEPLOYMENT $AZURE_SANDBOX_NS $API_NAME
        __r=$?
    fi

    if [ "$__r" -eq "0" ]; then
        createService $API_SVC_SPEC $AZURE_SANDBOX_NS $API_SVC
        __r=$?
    fi

    echo "...deploying on azure finished ($__r)"
    return $__r
}

undeploy()
{
    local __r=0
    if [ "$1" == "azure" ];then
        undeploy_azure
    else
	    undeploy_local
	fi
	_r=$?
    return $__r
}

deploy()
{
    local __r=0
    if [ "$1" == "azure" ];then
        deploy_azure
    else
	    deploy_local
	fi
	_r=$?
    return $__r
}


if [ "$1" == "deploy" ]
then
	deploy $2
    if [ ! "$?" -eq "0" ]
    then
        echo "...deploy was unsuccessful"
    else
        echo "...deploy was successful"
    fi
elif [ "$1" == "undeploy" ]
then
	undeploy $2
    if [ ! "$?" -eq "0" ]
    then
        echo "...undeploy was unsuccessful"
    else
        echo "...undeploy was successful"
    fi
else
	usage
fi
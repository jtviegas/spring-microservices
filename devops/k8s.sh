#!/bin/sh

this_folder="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
parent_folder=$(dirname $this_folder)

# default context
USER_DEFAULT=minikube
CONTEXT_DEFAULT=minikube
NAMESPACE_DEFAULT=default
CLUSTER_DEFAULT=minikube

# dev context
DEV_CLUSTER=$CLUSTER_DEFAULT
DEV_USER=$USER_DEFAULT
DEV_NAMESPACE=development
DEV_CONTEXT=dev

CONFIG_DEPLOYMENT_NAME=config
CONFIG_DEPLOYMENT_SPEC=$this_folder/config.yaml
CONFIG_SVC_NAME=config-service
CONFIG_SVC_SPEC=$this_folder/config-svc.yaml
GITHUB_SVC_NAME=github
GITHUB_SVC_SPEC=$this_folder/github-svc.yaml

KUBECTL_URL=https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/darwin/amd64/kubectl
MINIKUBE_URL=https://storage.googleapis.com/minikube/releases/v1.1.0/minikube-darwin-amd64

#https://stackoverflow.com/questions/50041411/kubernetes-externalname-service-does-not-resolve
DNS_FIX_VERSION=1.14.10


# parameter check
usage()
{
        cat <<EOM
        usage:
        $(basename $0) deploy | undeploy | install | uninstall
EOM
        exit 1
}

[ -z $1 ] && { usage; }

undeploy()
{
    echo "undeploying..."
    local __r=0

    kubectl delete svc $CONFIG_SVC_NAME
    if [ ! "$?" -eq "0" ]; then
        echo "...could not delete service $CONFIG_SVC_NAME"
        __r=1
    fi

    kubectl delete deployments $CONFIG_DEPLOYMENT_NAME
    if [ ! "$?" -eq "0" ]; then
        echo "...could not delete deployment $CONFIG_DEPLOYMENT_NAME"
        __r=1
    fi

    kubectl delete svc $GITHUB_SVC_NAME
    if [ ! "$?" -eq "0" ]; then
        echo "...could not delete service $GITHUB_SVC_NAME"
        __r=1
    fi

    kubectl config use-context $CONTEXT_DEFAULT
    if [ ! "$?" -eq "0" ]; then
        echo "...could not switch to context $CONTEXT_DEFAULT"
        __r=1
    fi

    kubectl config delete-context $DEV_CONTEXT
    if [ ! "$?" -eq "0" ]; then
        echo "...could not delete context $DEV_CONTEXT"
        __r=1
    fi

    kubectl delete namespace $DEV_NAMESPACE
    if [ ! "$?" -eq "0" ]; then
        echo "...could not delete namespace $DEV_NAMESPACE"
        __r=1
    fi

    echo "...undeploying finished ($__r)"
    return $__r
}

deploy()
{
    echo "deploying ..."
    local __r=0

    kubectl create namespace $DEV_NAMESPACE
    if [ ! "$?" -eq "0" ]; then
        echo "...couldn't create namespace $DEV_NAMESPACE"
        __r=1
    fi

    if [ "$__r" -eq "0" ]; then
        kubectl config set-context $DEV_CONTEXT --namespace=$DEV_NAMESPACE --cluster=$DEV_CLUSTER --user=$DEV_USER
        if [ ! "$?" -eq "0" ]; then
            echo "...couldn't create context $DEV_CONTEXT"
            __r=1
        fi
    fi

    if [ "$__r" -eq "0" ]; then
        kubectl config use-context $DEV_CONTEXT
        if [ ! "$?" -eq "0" ]; then
            echo "...couldn't use context $DEV_CONTEXT"
            __r=1
        fi
    fi

    if [ "$__r" -eq "0" ]; then
        kubectl create -f $GITHUB_SVC_SPEC
        if [ ! "$?" -eq "0" ]; then
            echo "...couldn't create service $GITHUB_SVC_NAME"
            __r=1
        else
             kubectl get services
             kubectl describe services/$GITHUB_SVC_NAME
        fi
    fi

    if [ "$__r" -eq "0" ]; then
        kubectl create -f $CONFIG_DEPLOYMENT_SPEC
        if [ ! "$?" -eq "0" ]; then
            echo "...couldn't create deployment $CONFIG_DEPLOYMENT_NAME"
            __r=1
        else
             kubectl get pods
        fi
    fi



    if [ "$__r" -eq "0" ]; then
        kubectl create -f $CONFIG_SVC_SPEC
        if [ ! "$?" -eq "0" ]; then
            echo "...couldn't create service $CONFIG_SVC_NAME"
            __r=1
        else
             kubectl get services
             kubectl describe services/$CONFIG_SVC_NAME
        fi
    fi




    echo "...deploying finished ($__r)"
    return $__r
}


install()
{
    echo "installing ..."
    local __r=0

    curl -Lo minikube $MINIKUBE_URL && chmod +x minikube && sudo mv minikube /usr/local/bin/
    if [ ! "$?" -eq "0" ]; then
        echo "...could not install minikube"
        __r=1
    fi

    if [ "$__r" -eq "0" ]; then
        minikube start
        if [ ! "$?" -eq "0" ]; then
            echo "...could not start minikube"
            __r=1
        fi
    fi

    if [ "$__r" -eq "0" ]; then
        minikube addons enable heapster
        if [ ! "$?" -eq "0" ]; then
            echo "...could not enable heapster addon to minikube"
            __r=1
        fi
    fi

    if [ "$__r" -eq "0" ]; then
        curl -LO $KUBECTL_URL && chmod +x ./kubectl && sudo mv ./kubectl /usr/local/bin/kubectl
        if [ ! "$?" -eq "0" ]; then
            echo "...could not install kubectl"
            __r=1
        else
            kubectl config view
            kubectl cluster-info
        fi
    fi

    echo "...finished installing => $__r"
    return $__r
}

uninstall()
{
    echo "uninstalling ..."
    local __r=0

    sudo rm -f /usr/local/bin/kubectl
    if [ ! "$?" -eq "0" ]; then
        echo "...could not uninstall kubectl"
        __r=1
    fi

    minikube addons disable heapster
    if [ ! "$?" -eq "0" ]; then
        echo "...could not disable heapster addon on minikube"
        __r=1
    fi

    minikube stop
    if [ ! "$?" -eq "0" ]; then
        echo "...could not stop minikube"
        __r=1
    fi

    minikube delete
    if [ ! "$?" -eq "0" ]; then
        echo "...could not delete minikube"
        __r=1
    fi

    sudo rm -f /usr/local/bin/minikube
    if [ ! "$?" -eq "0" ]; then
        echo "...could not remove minikube binary"
        __r=1
    fi
    echo "...finished uninstalling => $__r"
    return $__r
}


if [ "$1" == "deploy" ]
then
	deploy
    if [ ! "$?" -eq "0" ]
    then
        echo "...deployment was unsuccessful"
    else
        echo "...deployment was successful"
    fi
elif [ "$1" == "undeploy" ]
then
	undeploy
    if [ ! "$?" -eq "0" ]
    then
        echo "...undeployment was unsuccessful"
    else
        echo "...undeployment was successful"
    fi
elif [ "$1" == "install" ]
then
	install
    if [ ! "$?" -eq "0" ]
    then
        echo "...install was unsuccessful"
    else
        echo "...install was successful"
    fi
elif [ "$1" == "uninstall" ]
then
	uninstall
    if [ ! "$?" -eq "0" ]
    then
        echo "...uninstall was unsuccessful"
    else
        echo "...uninstall was successful"
    fi
else
	usage
fi
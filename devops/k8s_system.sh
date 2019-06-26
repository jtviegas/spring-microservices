#!/bin/sh

this_folder="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
parent_folder=$(dirname $this_folder)

KUBECTL_URL=https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/darwin/amd64/kubectl
MINIKUBE_URL=https://storage.googleapis.com/minikube/releases/v1.1.1/minikube-darwin-amd64

LOCAL_BIN=/usr/local/bin
MINIKUBE_PATH_DIR=$LOCAL_BIN
MINIKUBE_PATH=$MINIKUBE_PATH_DIR/minikube

KUBECTL_PATH_DIR=$LOCAL_BIN
KUBECTL_PATH=$KUBECTL_PATH_DIR/kubectl

AZURE_SANDBOX_SUBSCRIPTION=a4fe28da-0262-4b49-a9ea-7f2bba03f85b
AZURE_SANDBOX_RESOURCEGROUP=rgpazewdmlitdp-aksk8s001
AZURE_SANDBOX_CLUSTER=digital-platform-core-dev-west-2

#https://stackoverflow.com/questions/50041411/kubernetes-externalname-service-does-not-resolve
#DNS_FIX_VERSION=1.14.10

# local: CPUs=2, Memory=2048MB, Disk=20000MB


# parameter check
usage()
{
        cat <<EOM
        usage:
        $(basename $0) install | uninstall [local|azure]
EOM
        exit 1
}

[ -z $1 ] && { usage; }




install()
{
    local __r=0
    if [ "$1" == "azure" ];then
        install_azure
    else
	    install_local
	fi
	_r=$?
    return $__r
}

install_azure()
{
    echo "installing azure cluster config..."
    local __r=0

    az login

    az account set -s $AZURE_SANDBOX_SUBSCRIPTION
    if [ ! "$?" -eq "0" ]; then
        echo "...could not set subscription"
        __r=1
    fi
    if [ "$__r" -eq "0" ]; then
        az aks install-cli
        if [ ! "$?" -eq "0" ]; then
            echo "...could not install kubectl"
            __r=1
        fi
    fi
    if [ "$__r" -eq "0" ]; then
        az aks get-credentials --resource-group $AZURE_SANDBOX_RESOURCEGROUP --subscription $AZURE_SANDBOX_SUBSCRIPTION --name $AZURE_SANDBOX_CLUSTER --overwrite-existing
        if [ ! "$?" -eq "0" ]; then
            echo "...could not get aks cluster credentials"
            __r=1
        fi
    fi

    echo "...finished installing azure cluster config => $__r"
    return $__r
}

uninstall()
{
    local __r=0
    if [ "$1" == "azure" ];then
        uninstall_azure
    else
	    uninstall_local
	fi
	_r=$?
    return $__r
}

uninstall_azure()
{
    echo "uninstalling azure cluster config..."
    local __r=0

    sudo rm -f $KUBECTL_PATH
    if [ ! "$?" -eq "0" ]; then
        echo "...could not uninstall kubectl"
        __r=1
    fi

    az account clear
    if [ ! "$?" -eq "0" ]; then
        echo "...could not clear azure subscriptions"
        __r=1
    fi

    echo "...finished uninstalling azure cluster config => $__r"
    return $__r
}

install_local()
{
    echo "installing cluster locally..."
    local __r=0

    curl -Lo minikube $MINIKUBE_URL && chmod +x minikube && sudo mv minikube $MINIKUBE_PATH_DIR/
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
        minikube addons enable metrics-server
        if [ ! "$?" -eq "0" ]; then
            echo "...could not enable metrics-server addon to minikube"
            __r=1
        fi
    fi



    if [ "$__r" -eq "0" ]; then
        curl -LO $KUBECTL_URL && chmod +x ./kubectl && sudo mv ./kubectl $KUBECTL_PATH
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

uninstall_local()
{
    echo "uninstalling ..."
    local __r=0

    sudo rm -f $KUBECTL_PATH
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

    sudo rm -f $MINIKUBE_PATH
    if [ ! "$?" -eq "0" ]; then
        echo "...could not remove minikube binary"
        __r=1
    fi
    echo "...finished uninstalling => $__r"
    return $__r
}


if [ "$1" == "install" ]
then
	install $2
    if [ ! "$?" -eq "0" ]
    then
        echo "...install was unsuccessful"
    else
        echo "...install was successful"
    fi
elif [ "$1" == "uninstall" ]
then
	uninstall $2
    if [ ! "$?" -eq "0" ]
    then
        echo "...uninstall was unsuccessful"
    else
        echo "...uninstall was successful"
    fi
else
	usage
fi
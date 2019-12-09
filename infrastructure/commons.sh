debug(){
    local __msg="$1"
    echo "\n [DEBUG] `date` ... $__msg\n"
}

info(){
    local __msg="$1"
    echo "\n [INFO]  `date` ->>> $__msg\n"
}

warn(){
    local __msg="$1"
    echo "\n [WARN]  `date` *** $__msg\n"
}

err(){
    local __msg="$1"
    echo "\n [ERR]   `date` !!! $__msg\n"
}

goin(){
    if [ ! -z $LOG_TRACE ]; then
        local __msg="$1"
        local __params="$2"
        echo "\n [IN]    `date` ___ $__msg [$__params]\n"
    fi
}

goout(){
    if [ ! -z $LOG_TRACE ]; then
        local __msg="$1"
        local __outcome="$2"
        echo "\n [OUT]   `date` ___ $__msg [$__outcome]\n"
    fi
}
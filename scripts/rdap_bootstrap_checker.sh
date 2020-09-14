#!/usr/bin/env bash

set -e

function query() {
    echo
    echo -n "$1 - "
    status=$(curl -s -o /dev/null -w '%{http_code}' "$1")
    if [[ $status -ne $2 ]]; then
        echo "FAIL (expected $2)"
    else
        echo "PASS"
    fi
    curl -s -I "$1"
}

# /domain
query "$1/domain/google.com" 302
query "$1//domain/google.foo" 302
query "$1/domain/xn--flw351e" 302
query "$1/domain/2.in-addr.arpa" 302
query "$1/domain/15.in-addr.arpa" 302
query "$1/domain/0.0.e.0.1.0.0.2.ip6.arpa" 302

# /nameserver
query "$1/nameserver/cnn.com" 302
query "$1/nameserver/15.in-addr.arpa" 302

# /ip
query "$1/ip/2.0.0.0/8" 302
query "$1/ip/15.0.0.0/8" 302
query "$1/ip/2c00::/12" 302
query "$1/ip/2c00::/13" 302
query "$1/ip/3c00::/12" 404

# /autnum
query "$1/autnum/1" 302
query "$1/autnum/272796" 302
query "$1/autnum/272797" 404

# /entity
query "$1/entity/ODIN19-ARIN" 302
query "$1/entity/IRT-APNIC-AP" 302

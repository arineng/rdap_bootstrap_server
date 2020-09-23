#!/usr/bin/env bash

set -e

function print_usage() {
    echo 'This script walks through an RDAP Bootstrap service as per RFC 7484.'
    echo
    echo 'Usage:'
    echo '  rdap_bootstrap_checker.sh RDAP_BOOTSTRAP_BASE_URL'
    echo
    echo 'Where:'
    echo '  RDAP_BOOTSTRAP_BASE_URL    Base URL of the RDAP Bootstrap service (e.g. https://rdap.arin.net/bootstrap)'
}

function query() {
    echo
    echo "$1"
    curl -s -I "$1"
}

if [[ $# -ne 1 ]]; then
    print_usage
    exit 1
fi

# /domain
query "$1/domain/google.com"
query "$1/domain/google.foo"
query "$1/domain/xn--flw351e"
query "$1/domain/2.in-addr.arpa"
query "$1/domain/15.in-addr.arpa"
query "$1/domain/0.0.e.0.1.0.0.2.ip6.arpa"

# /nameserver
query "$1/nameserver/ns1.cnn.com"
query "$1/nameserver/ns1.15.in-addr.arpa"

# /ip
query "$1/ip/2.0.0.0/8"
query "$1/ip/15.0.0.0/8"
query "$1/ip/2c00::/12"
query "$1/ip/2c00::/13"
query "$1/ip/3c00::/12"

# /autnum
query "$1/autnum/1"
query "$1/autnum/272796"
query "$1/autnum/272797"

# /entity
query "$1/entity/ARINN-ARIN"
query "$1/entity/IRT-APNIC-AP"

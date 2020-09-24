#!/usr/bin/env bash

set -e

pass=0
fail=0

function print_usage() {
    echo 'This script checks the correctness of an RDAP Bootstrap service as per RFC 7484.'
    echo
    echo 'Usage:'
    echo '  rdap_bootstrap_checker.sh RDAP_BOOTSTRAP_BASE_URL'
    echo
    echo 'Where:'
    echo '  RDAP_BOOTSTRAP_BASE_URL    Base URL of the RDAP Bootstrap service (e.g. https://rdap.arin.net/bootstrap)'
}

function query() {
    echo
    echo -n "$1 - "
    status=$(curl -s -o /dev/null -w '%{http_code}' "$1")
    if [[ $status -ne $2 ]]; then
        echo "FAIL (expected $2)"
        ((fail+=1))
    else
        echo "PASS"
        ((pass+=1))
    fi
    curl -s -I "$1"
}

if [[ $# -ne 1 ]]; then
    print_usage
    exit 1
fi

# /domain
query "$1/domain/google.com" 302
query "$1/domain/google.foo" 302
query "$1/domain/xn--flw351e" 302
query "$1/domain/2.in-addr.arpa" 302
query "$1/domain/15.in-addr.arpa" 302
query "$1/domain/0.0.e.0.1.0.0.2.ip6.arpa" 302

# /nameserver
query "$1/nameserver/ns1.cnn.com" 302
query "$1/nameserver/ns1.15.in-addr.arpa" 404

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
query "$1/entity/ARINN-ARIN" 302
query "$1/entity/IRT-APNIC-AP" 302

echo "PASS=$pass FAIL=$fail"

#!/usr/bin/env bash

set -e

queries=()

# /domain
queries+=("$1/domain/google.com")
queries+=("$1//domain/google.foo")
queries+=("$1/domain/xn--flw351e")
queries+=("$1/domain/2.in-addr.arpa")
queries+=("$1/domain/15.in-addr.arpa")
queries+=("$1/domain/0.0.e.0.1.0.0.2.ip6.arpa")

# /nameserver
queries+=("$1/nameserver/cnn.com")
queries+=("$1/nameserver/15.in-addr.arpa")

# /ip
queries+=("$1/ip/2.0.0.0/8")
queries+=("$1/ip/15.0.0.0/8")
queries+=("$1/ip/2c00::/12")
queries+=("$1/ip/2c00::/13")
queries+=("$1/ip/3c00::/12")

# /autnum
queries+=("$1/autnum/1")
queries+=("$1/autnum/272796")
queries+=("$1/autnum/272797")

# /entity
queries+=("$1/entity/ODIN19-ARIN")
queries+=("$1/entity/IRT-APNIC-AP")

for query in "${queries[@]}"; do
    echo
    echo "$query"
    curl -s -I "${query}"
done

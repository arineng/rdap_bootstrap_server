# RDAP Bootstrap Server

The Registration Data Access Protocol (RDAP) defines a bootstrapping process in
[RFC 7484](https://tools.ietf.org/html/rfc7484). A bootstrap server aids clients by reading the
[bootstrapping information published by IANA](https://data.iana.org/rdap/) and using it to send HTTP redirects to RDAP
queries. Clients utilizing a bootstrap server will not need to conduct their own bootstrapping.

## Versioning, Building, and Runtime Requirements

Any version containing the word `SNAPSHOT` is a development version. Versions are:

* `1000.0-SNAPSHOT` reflects the `master` branch development version. It is set high at a major version number of `1000`
so that the `master` branch is not re-versioned for every release.
* [1.0.0](https://github.com/arineng/rdap_bootstrap_server/releases/tag/1.0.0) - First release. At the time of this
release, the IANA bootstrap files are available but contain no usable content, and the embedded bootstrap files point to
our best known locations for servers. Minor point release may occur to update the embedded files until the IANA files
become populated with useful data.
* [1.1.0](https://github.com/arineng/rdap_bootstrap_server/releases/tag/1.1.0) - A few small things:
    - IANA and AFRINIC have been taken out of the bootstrap files as they do not have servers ready yet.
    - `/help` now works instead of giving a `500`.
    - `/help` shows the dates of load and dates of the bootstrap files.
    - `/help` limits the URLs in the statistics to 100 per category.
* [1.1.1](https://github.com/arineng/rdap_bootstrap_server/releases/tag/1.1.1) - Bugfixes to `/help` and to parsing of
IANA files.
* 1.2.0 - Added `match_scheme_on_redirect` option.
* 1.2.1 - Fix to using IANA bootstrapping files according to current RFCs.
* 1.3.0
    - Upgraded to build against Java 11 or higher.
    - Replaced default redirection with a `404` response for entries that do not exist in any of the bootstrap files.
    - Fixed IPv6 redirection for non-existent space.
    - Updated the default bootstrap files to the latest IANA files.
    - Can run as a Spring Boot application.
    - Can build a Docker image.
    - [A built-in timer to download IANA files](https://github.com/arineng/rdap_bootstrap_server/issues/1).

This server is written as a Java servlet and should run in any Java Servlet 3.0 container or higher, as a Spring Boot
application, or as a Docker container. It should build against Java 11 or higher.

To build using Gradle:

    ./gradlew clean build test

This will produce a WAR file in `build/libs` after running the unit tests. The WAR can be directly run from the command
line using either the `java` command or the Gradle `bootRun` command:

    java -jar build/libs/rdap_bootstrap_server-1000.0-SNAPSHOT.war
    ./gradlew bootRun

Beside a WAR, build a JAR using the Gradle `bootJar` task and run it using the `java` command:

    ./gradlew bootJar
    java -jar build/libs/rdap_bootstrap_server-1000.0-SNAPSHOT.jar

System properties can be passed in as `-D` options and/or environment variables.

## Deploying and Testing

Deploying the WAR file depends on the Servlet container/server you are using. It is usually as simple as copying the WAR
file to a particular directory. The WAR file comes bundled with some bootstrap files so testing can take place before
fully configuring the bootstrap.

The URL path for querying the servlet will depend on your Servlet container and the configuration you have given to the
container for this servlet. It defaults to `/rdapbootstrap`.

To test the bootstrap server, issue an RDAP query such as `ip/1.1.1.1`. You should see a redirect to APNIC's RDAP
server.

```
$ wget http://localhost:8080/rdapbootstrap/ip/1.1.1.1
--2015-04-24 16:24:39--  http://localhost:8080/rdapbootstrap/ip/1.1.1.1
Resolving localhost... ::1, 127.0.0.1
Connecting to localhost|::1|:8080... connected.
HTTP request sent, awaiting response... 302 Moved Temporarily
Location: http://rdap.apnic.net/ip/1.1.1.1 [following]
--2015-04-24 16:24:39--  http://rdap.apnic.net/ip/1.1.1.1
Resolving rdap.apnic.net... 2001:dd8:9:2::101:43, 203.119.101.43
Connecting to rdap.apnic.net|2001:dd8:9:2::101:43|:80... connected.
HTTP request sent, awaiting response... 200 OK
```

## Docker

Build a docker image using the Gradle `bootBuildImage` command:

    ./gradlew bootBuildImage --imageName=NAME[:TAG]

Run the docker image:

    docker run -p 8080:8080 -ti NAME[:TAG]

## Getting Help

If you have questions or need help with this software, you may use the issue tracker on
[GitHub](https://github.com/arineng/rdap_bootstrap_server/issues) or you may use the
[ARIN Technical Discussions ](http://lists.arin.net/mailman/listinfo/arin-tech-discuss) mailing list (it is a very low
volume list).

## Properties and Bootstrap Files

Bootstrap files may either be listed in a properties file pointed to by the system property
`arin.rdapbootstrap.resource_files` or they may be listed using system properties directly. Here is an example of a
properties file pointed to by `arin.rdapbootstrap.resource_files`:

    default_bootstrap = /default_bootstrap.json
    as_bootstrap = /as_bootstrap.json
    domain_bootstrap = /domain_bootstrap.json
    v4_bootstrap = /v4_bootstrap.json
    v6_bootstrap = /v6_bootstrap.json
    entity_bootstrap = /entity_bootstrap.json

The system properties directly listing these are the keys of the properties file prefixed with
`arin.rdapbootstrap.bootfile.`. So the AS bootstrap would be `arin.rdapbootstrap.bootfile.as_bootstrap`, etc.

The server ships with a properties file that points to a set of built-in bootstrap files. These bootstrap files are
useful for getting the server up and running, but ultimately will need to be replaced with files that are updated
periodically from the IANA.

So there are three types of configuration.

### Configuration Setup Type 1 Example

Do nothing and let the server use the bootstrap files that ship with it.

### Configuration Setup Type 2 Example

Set the Java system property `arin.rdapbootstrap.resource_files` to be `/var/rdap/resource_files.properties`.

In the `/var/rdap/resource_files.properties` file have the following:

    default_bootstrap = /var/rdap/default_bootstrap.json
    as_bootstrap = /var/rdap/as_bootstrap.json
    domain_bootstrap = /var/rdap/domain_bootstrap.json
    v4_bootstrap = /var/rdap/v4_bootstrap.json
    v6_bootstrap = /var/rdap/v6_bootstrap.json
    entity_bootstrap = /var/rdap/entity_bootstrap.json

### Configuration Setup Type 3 Example

Have the following Java system properties:

    arin.rdapbootstrap.bootfile.default_bootstrap = /var/rdap/default_bootstrap.json
    arin.rdapbootstrap.bootfile.as_bootstrap = /var/rdap/as_bootstrap.json
    arin.rdapbootstrap.bootfile.domain_bootstrap = /var/rdap/domain_bootstrap.json
    arin.rdapbootstrap.bootfile.v4_bootstrap = /var/rdap/v4_bootstrap.json
    arin.rdapbootstrap.bootfile.v6_bootstrap = /var/rdap/v6_bootstrap.json
    arin.rdapbootstrap.bootfile.entity_bootstrap = /var/rdap/entity_bootstrap.json
    
## Updating Bootstrap Files

The server checks every minute to see if a file has been modified, and if any of them have it will automatically reload
all of them.

The AS, v4, v6, and domain files are published periodically by IANA. You can set a cron or system process to fetch them,
perhaps once a week, from the following places:

    https://data.iana.org/rdap/asn.json
    https://data.iana.org/rdap/ipv4.json
    https://data.iana.org/rdap/ipv6.json
    https://data.iana.org/rdap/dns.json

The other bootstrap files take the form of the IANA files but are custom to your particular installation of the
bootstrap server.

### Entity Bootstrap File

The entity bootstrap file is used to redirect queries for entities based on the last component of the entity handle or
identifier. Some registries, most notably all of the RIRs, append a registry signifier such as `-ARIN`. While entity
bootstrapping is not officially part of the IETF specification, this server attempts to issue redirects based on those
signifiers if present. Here is an example of an entity bootstrap file:

```json
{
  "version": "1.0",
  "publication": "2014-09-09T15:39:03-0400",
  "services": [
    [
      [
        "ARIN"
      ],
      [
        "https://rdap.arin.net/registry",
        "http://rdap.arin.net/registry"
      ]
    ],
    [
      [
        "AP"
      ],
      [
        "https://rdap.apnic.net/"
      ]
    ],
    [
      [
        "RIPE"
      ],
      [
        "https://rdap.db.ripe.net/",
        "http://rdap.db.ripe.net/"
      ]
    ],
    [
      [
        "LACNIC"
      ],
      [
        "https://rdap.lacnic.net/rdap/"
      ]
    ]
  ]
}
```

### Non-existent Entries

The server responds with a `404` for entries that do not exist in any of the bootstrap files.

## Redirect Scheme Matching

By default, this server will always attempt to issue HTTPS redirects. This should not be a problem because all RDAP
clients are _REQUIRED_ to support both HTTP and HTTPS. However, if it is necessary to try to keep the scheme (HTTP or
HTTPS) for the redirect that was given in the query, this behavior can be set with the system property
`arin.rdapbootstrap.match_scheme_on_redirect=true`. Note that this is a system property and is not part of the
`resouce_files.properties` file.

## Environment Variables / System Properties Mapping

    Environment Variable: RDAPBOOTSTRAP_MATCH_SCHEME_ON_REDIRECT
    System Property: arin.rdapbootstrap.match_scheme_on_redirect
    Description: Keep the scheme (HTTP or HTTPS) for the redirect that was given in the query or not
    Type: BOOLEAN
    Required: No
    Default Value: false
    Possible Values: false | true

    Environment Variable: RDAPBOOTSTRAP_DOWNLOAD_BOOTSTRAP_FILES
    System Property: arin.rdapbootstrap.download_bootstrap_files
    Description: Download bootstrap files from IANA or not
    Type: BOOLEAN
    Required: No
    Default Value: false
    Possible Values: false | true

    Environment Variable: RDAPBOOTSTRAP_DOWNLOAD_ASN_FILE_URL
    System Property: arin.rdapbootstrap.download_asn_file_url
    Description: Download URL for the AS file
    Type: URL
    Required: Only if arin.rdapbootstrap.download_bootstrap_files is set to true
    Default Value: https://data.iana.org/rdap/asn.json

    Environment Variable: RDAPBOOTSTRAP_DOWNLOAD_DOMAIN_FILE_URL
    System Property: arin.rdapbootstrap.download_domain_file_url
    Description: Download URL for the domain file
    Type: URL
    Required: Only if arin.rdapbootstrap.download_bootstrap_files is set to true
    Default Value: https://data.iana.org/rdap/dns.json

    Environment Variable: RDAPBOOTSTRAP_DOWNLOAD_IPV4_FILE_URL
    System Property: arin.rdapbootstrap.download_ipv4_file_url
    Description: Download URL for the v4 file
    Type: URL
    Required: Only if arin.rdapbootstrap.download_bootstrap_files is set to true
    Default Value: https://data.iana.org/rdap/ipv4.json

    Environment Variable: RDAPBOOTSTRAP_DOWNLOAD_IPV6_FILE_URL
    System Property: arin.rdapbootstrap.download_ipv6_file_url
    Description: Download URL for the v6 file
    Type: URL
    Required: Only if arin.rdapbootstrap.download_bootstrap_files is set to true
    Default Value: https://data.iana.org/rdap/ipv6.json

    Environment Variable: RDAPBOOTSTRAP_DOWNLOAD_DIRECTORY
    System Property: arin.rdapbootstrap.download_directory
    Description: Directory to download IANA files into
    Type: DIRECTORY_PATH
    Required: Only if arin.rdapbootstrap.download_bootstrap_files is set to true

    Environment Variable: RDAPBOOTSTRAP_DOWNLOAD_INTERVAL
    System Property: arin.rdapbootstrap.download_interval
    Description: Download interval in seconds
    Type: POSITIVE_LONG
    Required: No
    Default Value: 86400 (a day)

    Environment Variable: RDAPBOOTSTRAP_BOOTFILE_DOMAIN_BOOTSTRAP
    System Property: arin.rdapbootstrap.bootfile.domain_bootstrap
    Description: Location of the domain file
    Type: FILE_PATH
    Required: Only if configuration setup type 3

    Environment Variable: RDAPBOOTSTRAP_BOOTFILE_V4_BOOTSTRAP
    System Property: arin.rdapbootstrap.bootfile.v4_bootstrap
    Description: Location of the v4 file
    Type: FILE_PATH
    Required: Only if configuration setup type 3

    Environment Variable: RDAPBOOTSTRAP_BOOTFILE_V6_BOOTSTRAP
    System Property: arin.rdapbootstrap.bootfile.v6_bootstrap
    Description: Location of the v6 file
    Type: FILE_PATH
    Required: Only if configuration setup type 3

    Environment Variable: RDAPBOOTSTRAP_BOOTFILE_AS_BOOTSTRAP
    System Property: arin.rdapbootstrap.bootfile.as_bootstrap
    Description: Location of the AS file
    Type: FILE_PATH
    Required: Only if configuration setup type 3

    Environment Variable: RDAPBOOTSTRAP_BOOTFILE_ENTITY_BOOTSTRAP
    System Property: arin.rdapbootstrap.bootfile.entity_bootstrap
    Description: Location of the entity file
    Type: FILE_PATH
    Required: Only if configuration setup type 3

    Environment Variable: RDAPBOOTSTRAP_LOG_LEVEL
    System Properties: logging.level.org.springframework.web, logging.level.net.arin.rdap_bootstrap
    Description:
    Type: LOG_LEVEL
    Required: No
    Default Value: INFO
    Possible Values: TRACE | DEBUG | INFO | WARN | ERROR | FATAL

[ FOR ARIN INTERNAL EYES ONLY ]

## Spring Boot Application

### Build, Test, and Run

Checkout the code:

    git clone https://github.com/arineng/rdap_bootstrap_server.git
    cd rdap_bootstrap_server
    git checkout springboot

Build a WAR and run it using either the Gradle `bootRun` command or the `java` command:

    ./gradlew clean build test --info
    [export env=value; ...] ./gradlew bootRun [-Dproperty=value ...] --info
    [export env=value; ...] java -jar [-Dproperty=value ...] build/libs/rdap_bootstrap_server-1000.0-SNAPSHOT.war

Beside a WAR, build a JAR using the Gradle `bootJar` task and run it using the `java` command:

    ./gradlew clean build test bootJar --info
    [export env=value; ...] java -jar [-Dproperty=value ...] build/libs/rdap_bootstrap_server-1000.0-SNAPSHOT.jar

For a system property, the `-D` option takes precedence over setting its environment variable.

### Docker

Build a docker image using the Gradle `bootBuildImage` command:

    ./gradlew clean build test bootBuildImage --imageName=harbor.arin.net/k8s/rdap-bootstrap-server --info

Run the docker image:

    docker rm -f rdap-bootstrap-server;
    docker run -e "RDAPBOOTSTRAP_DOWNLOAD_BOOTSTRAP_FILES=true" \
               -e "RDAPBOOTSTRAP_DOWNLOAD_ASN_FILE_URL=https://data.iana.org/rdap/asn.json" \
               -e "RDAPBOOTSTRAP_DOWNLOAD_DOMAIN_FILE_URL=https://data.iana.org/rdap/dns.json" \
               -e "RDAPBOOTSTRAP_DOWNLOAD_IPV4_FILE_URL=https://data.iana.org/rdap/ipv4.json" \
               -e "RDAPBOOTSTRAP_DOWNLOAD_IPV6_FILE_URL=https://data.iana.org/rdap/ipv6.json" \
               -e "RDAPBOOTSTRAP_DOWNLOAD_DIRECTORY=/tmp/rdapbootstrap" \
               -e "RDAPBOOTSTRAP_DOWNLOAD_INTERVAL=120" \
               -e "TZ=America/New_York" \
               -p 8080:8080 \
               --name rdap-bootstrap-server \
               -ti harbor.arin.net/k8s/rdap-bootstrap-server

Test the docker container:

    curl -v http://localhost:8080/rdapbootstrap/autnum/1

Log into the docker container:

     docker exec -ti --env COLUMNS=`tput cols` --env LINES=`tput lines` rdap-bootstrap-server /bin/bash

### Sample Queries

This section covers the RDAP Bootstrap queries as per [RFC 7484](https://tools.ietf.org/html/rfc7484). The
[IANA RDAP Bootstrap Registry](https://data.iana.org/rdap/) publishes JSON files for domains, IP addresses, and AS
numbers which respectively map to the `/domain`, `/ip`, and `/autnum` queries. The `/nameserver` queries only work for
forward domains and leverage IANA's domain bootstrap data. The `/entity` queries only support redirections for RIR
entities. The `/help` query returns statistics for ARIN RDAP Bootstrap service.

#### /domain

    http://localhost:8080/rdapbootstrap/domain/google.com (302 to https://rdap.verisign.com/com/v1)
    http://localhost:8080/rdapbootstrap/domain/google.foo (302 to https://www.registry.google/rdap)
    http://localhost:8080/rdapbootstrap/domain/xn--flw351e (302 to https://www.registry.google/rdap)
    http://localhost:8080/rdapbootstrap/domain/2.in-addr.arpa (302 to https://rdap.db.ripe.net)
    http://localhost:8080/rdapbootstrap/domain/15.in-addr.arpa (302 to https://rdap.arin.net/registry)
    http://localhost:8080/rdapbootstrap/domain/0.0.e.0.1.0.0.2.ip6.arpa (302 to https://rdap.apnic.net)

#### /nameserver

    http://localhost:8080/rdapbootstrap/nameserver/cnn.com (302 to https://rdap.verisign.com/com/v1)
    http://localhost:8080/rdapbootstrap/nameserver/15.in-addr.arpa (404 because only for forward domains)

#### /ip

    http://localhost:8080/rdapbootstrap/ip/2.0.0.0/8 (302 to https://rdap.db.ripe.net)
    http://localhost:8080/rdapbootstrap/ip/15.0.0.0/8 (302 to https://rdap.arin.net/registry)
    http://localhost:8080/rdapbootstrap/ip/2c00::/12 (302 to https://rdap.afrinic.net/rdap)
    http://localhost:8080/rdapbootstrap/ip/2c00::/13 (302 to https://rdap.afrinic.net/rdap)
    http://localhost:8080/rdapbootstrap/ip/3c00::/12 (404 because non-existent in the IANA RDAP Bootstrap registry)

#### /autnum

    http://localhost:8080/rdapbootstrap/autnum/1 (302 to https://rdap.arin.net/registry)
    http://localhost:8080/rdapbootstrap/autnum/272796 (302 to https://rdap.lacnic.net/rdap)
    http://localhost:8080/rdapbootstrap/autnum/272797 (404 because non-existent in the IANA RDAP Bootstrap registry)

#### /entity

    http://localhost:8080/rdapbootstrap/entity/ODIN19-ARIN (302 to https://rdap.arin.net/registry)
    http://localhost:8080/rdapbootstrap/entity/IRT-APNIC-AP (302 to https://rdap.apnic.net)

#### /help

    http://localhost:8080/rdapbootstrap/help (200 returning ARIN RDAP Bootstrap service statistics)

### System Properties

    arin.rdapbootstrap.match_scheme_on_redirect=false (default) | true
    arin.rdapbootstrap.download_bootstrap_files=false (default) | true
    arin.rdapbootstrap.download_asn_file_url=URL
    arin.rdapbootstrap.download_domain_file_url=URL
    arin.rdapbootstrap.download_ipv4_file_url=URL
    arin.rdapbootstrap.download_ipv6_file_url=URL
    arin.rdapbootstrap.download_directory=FULL_DIRECTORY_PATH
    arin.rdapbootstrap.download_interval=86400 (default) | positive long (seconds)
    arin.rdapbootstrap.bootfile.domain_bootstrap=FULL_FILE_PATH
    arin.rdapbootstrap.bootfile.v4_bootstrap=FULL_FILE_PATH
    arin.rdapbootstrap.bootfile.v6_bootstrap=FULL_FILE_PATH
    arin.rdapbootstrap.bootfile.as_bootstrap=FULL_FILE_PATH
    arin.rdapbootstrap.bootfile.entity_bootstrap=FULL_FILE_PATH

### Environment Variables

    RDAPBOOTSTRAP_MATCH_SCHEME_ON_REDIRECT=false | true
    RDAPBOOTSTRAP_DOWNLOAD_BOOTSTRAP_FILES=false | true
    RDAPBOOTSTRAP_DOWNLOAD_ASN_FILE_URL=URL
    RDAPBOOTSTRAP_DOWNLOAD_DOMAIN_FILE_URL=URL
    RDAPBOOTSTRAP_DOWNLOAD_IPV4_FILE_URL=URL
    RDAPBOOTSTRAP_DOWNLOAD_IPV6_FILE_URL=URL
    RDAPBOOTSTRAP_DOWNLOAD_DIRECTORY=FULL_DIRECTORY_PATH
    RDAPBOOTSTRAP_DOWNLOAD_INTERVAL=positive long (seconds)
    RDAPBOOTSTRAP_BOOTFILE_DOMAIN_BOOTSTRAP=FULL_FILE_PATH
    RDAPBOOTSTRAP_BOOTFILE_V4_BOOTSTRAP=FULL_FILE_PATH
    RDAPBOOTSTRAP_BOOTFILE_V6_BOOTSTRAP=FULL_FILE_PATH
    RDAPBOOTSTRAP_BOOTFILE_AS_BOOTSTRAP=FULL_FILE_PATH
    RDAPBOOTSTRAP_BOOTFILE_ENTITY_BOOTSTRAP=FULL_FILE_PATH
    RDAPBOOTSTRAP_LOG_LEVEL=TRACE | DEBUG | INFO | WARN | ERROR | FATAL

[ / FOR ARIN INTERNAL EYES ONLY ]

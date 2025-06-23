# RDAP Bootstrap Server

The Registration Data Access Protocol (RDAP) defines a bootstrapping process in
[RFC 9224](https://tools.ietf.org/html/rfc9224). A bootstrap server aids clients by reading the
[bootstrapping information published by IANA](https://data.iana.org/rdap/) and using it to send HTTP redirects to RDAP
queries. Clients utilizing a bootstrap server will not need to conduct their own bootstrapping.

## Versioning, Building, and Runtime Requirements

Any version containing the word `SNAPSHOT` is a development version. Versions are:

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
* 2.0.1
    - Upgraded to build against Java 11 or higher.
    - Fixed IPv6 redirection for non-existent space.
    - Updated the default bootstrap files to the latest IANA files.
    - Can run as a Spring Boot application.
    - Can build a Docker image.
    - [A built-in timer to download IANA files](https://github.com/arineng/rdap_bootstrap_server/issues/1).
    - A new [rdap_bootstrap_checker](./scripts/rdap_bootstrap_checker.sh) script to check the correctness of an RDAP
      Bootstrap service as per [RFC 7484](https://tools.ietf.org/html/rfc7484).
* 2.0.2
    - Updated the `default_bootstrap.json` file for the domain entry.
    - Updated the default bootstrap files to the latest IANA files.
    - Upgraded Gradle, Spring Boot, and JUnit.
* 2.0.3
    - Use GitHub actions to perform builds.

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
`arin.rdapbootstrap.resource_files` or they may be listed using system properties directly or indirectly. Here is an
example of a properties file pointed to by `arin.rdapbootstrap.resource_files`:

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

So there are four types of configuration.

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

### Configuration Setup Type 4 Example

Set the following Java system properties for the scheduler to periodically download bootstrap files from IANA:

    arin.rdapbootstrap.download_bootstrap_files=true
    arin.rdapbootstrap.download_directory=/var/rdap

There are additional Java system properties with defaults that if needed could be tweaked for the scheduler:

     arin.rdapbootstrap.download_interval=86400
     arin.rdapbootstrap.download_max_attempts=1
     arin.rdapbootstrap.download_asn_file_url=https://data.iana.org/rdap/asn.json
     arin.rdapbootstrap.download_domain_file_url=https://data.iana.org/rdap/dns.json
     arin.rdapbootstrap.download_ipv4_file_url=https://data.iana.org/rdap/ipv4.json
     arin.rdapbootstrap.download_ipv6_file_url=https://data.iana.org/rdap/ipv6.json
    
## Updating Bootstrap Files

The server checks every minute to see if a file has been modified, and if any of them have it will automatically reload
all of them.

The AS, v4, v6, and domain files are published periodically by IANA. You can set a cron or system process (see
Configuration Setup Type 4 Example) to fetch them, perhaps once a week, from the following places:

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

### Default Bootstrap File

The default bootstrap file is consulted when all the other bootstrap files have failed. It takes the following form:

```json
{
  "version": "1.0",
  "publication": "2024-07-02T12:15:00-0400",
  "services": [
    [
      [
        "ip",
        "autnum",
        "domain",
        "nameserver",
        "entity"
      ],
      [
        "https://rdap.arin.net/registry/",
        "http://rdap.arin.net/registry/"
      ]
    ]
  ]
}
```

## Redirect Scheme Matching

By default, this server will always attempt to issue HTTPS redirects. This should not be a problem because all RDAP
clients are _REQUIRED_ to support both HTTP and HTTPS. However, if it is necessary to try to keep the scheme (HTTP or
HTTPS) for the redirect that was given in the query, this behavior can be set with the system property
`arin.rdapbootstrap.match_scheme_on_redirect=true`. Note that this is a system property and is not part of the
`resouce_files.properties` file.

## Environment Variables / System Properties Mapping

The bootstrap server can be configured using environment variables and/or system properties. Here is how they map:

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
    Description: Download URL for the ASN file
    Type: URL
    Required: No
    Default Value: https://data.iana.org/rdap/asn.json

    Environment Variable: RDAPBOOTSTRAP_DOWNLOAD_DOMAIN_FILE_URL
    System Property: arin.rdapbootstrap.download_domain_file_url
    Description: Download URL for the domain file
    Type: URL
    Required: No
    Default Value: https://data.iana.org/rdap/dns.json

    Environment Variable: RDAPBOOTSTRAP_DOWNLOAD_IPV4_FILE_URL
    System Property: arin.rdapbootstrap.download_ipv4_file_url
    Description: Download URL for the IPv4 file
    Type: URL
    Required: No
    Default Value: https://data.iana.org/rdap/ipv4.json

    Environment Variable: RDAPBOOTSTRAP_DOWNLOAD_IPV6_FILE_URL
    System Property: arin.rdapbootstrap.download_ipv6_file_url
    Description: Download URL for the IPv6 file
    Type: URL
    Required: No
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
    Default Value: 86400

    Environment Variable: RDAPBOOTSTRAP_DOWNLOAD_MAX_ATTEMPTS
    System Property: arin.rdapbootstrap.download_max_attempts
    Description: Maximum number of attempts when downloading a file
    Type: POSITIVE_INTEGER
    Required: No
    Default Value: 1

    Environment Variable: RDAPBOOTSTRAP_BOOTFILE_DEFAULT_BOOTSTRAP
    System Property: arin.rdapbootstrap.bootfile.default_bootstrap
    Description: Location of the default bootstrap file
    Type: FILE_PATH
    Required: Only if configuration setup type 3

    Environment Variable: RDAPBOOTSTRAP_BOOTFILE_AS_BOOTSTRAP
    System Property: arin.rdapbootstrap.bootfile.as_bootstrap
    Description: Location of the ASN file
    Type: FILE_PATH
    Required: Only if configuration setup type 3

    Environment Variable: RDAPBOOTSTRAP_BOOTFILE_DOMAIN_BOOTSTRAP
    System Property: arin.rdapbootstrap.bootfile.domain_bootstrap
    Description: Location of the domain file
    Type: FILE_PATH
    Required: Only if configuration setup type 3

    Environment Variable: RDAPBOOTSTRAP_BOOTFILE_V4_BOOTSTRAP
    System Property: arin.rdapbootstrap.bootfile.v4_bootstrap
    Description: Location of the IPv4 file
    Type: FILE_PATH
    Required: Only if configuration setup type 3

    Environment Variable: RDAPBOOTSTRAP_BOOTFILE_V6_BOOTSTRAP
    System Property: arin.rdapbootstrap.bootfile.v6_bootstrap
    Description: Location of the IPv6 file
    Type: FILE_PATH
    Required: Only if configuration setup type 3

    Environment Variable: RDAPBOOTSTRAP_BOOTFILE_ENTITY_BOOTSTRAP
    System Property: arin.rdapbootstrap.bootfile.entity_bootstrap
    Description: Location of the entity file
    Type: FILE_PATH
    Required: Only if configuration setup type 3

    Environment Variable: RDAPBOOTSTRAP_LOG_LEVEL
    System Properties: logging.level.org.springframework.web, logging.level.net.arin.rdap_bootstrap
    Description: Adjust the server log level
    Type: LOG_LEVEL
    Required: No
    Default Value: INFO
    Possible Values: TRACE | DEBUG | INFO | WARN | ERROR | FATAL

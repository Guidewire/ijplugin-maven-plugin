#!/bin/bash

SDK=$1
PLUGIN=$2

REPO=http://nexus/content/repositories/thirdparty/
REPOID=gw.thirdparty

if [ -z "${SDK}" ] ; then
    echo "Script to upload IntelliJ SDK to Nexus repository."
    echo "Use <plugin> to deploy particular plugin JARs."
    echo "Otherwise, deploys core IntelliJ JARs."
    echo "Usage: deploy.sh <path to SDK> [<plugin>]"
    exit 1
fi

if [ ! -f "${SDK}/build.txt" ] ; then
    echo "'${SDK}' does not look like IntelliJ installation (no build.txt file found)"
    exit 1
fi

VERSION=`cat ${SDK}/build.txt`

echo "Detected IntelliJ version: ${VERSION}"

if [ -n "${PLUGIN}" ] ; then
    GROUPID=com.jetbrains.intellij.plugins.${PLUGIN}
    ROOT="${SDK}/plugins/${PLUGIN}"
    DESC="Intellij ${PLUGIN} plugin SDK"
    echo "Deploying IntelliJ plugin '${PLUGIN}' at '${SDK}/plugins/${PLUGIN}' to repository '${REPO}'"
else
    GROUPID=com.jetbrains.intellij.platform
    ROOT="${SDK}"
    DESC="Intellij SDK"
    echo "Deploying SDK at '${SDK}' to repository '${REPO}'"
fi

SDKPOM=`tempfile` || exit 1

cat > ${SDKPOM} <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.jetbrains.intellij.platform</groupId>
  <artifactId>sdk</artifactId>
  <version>${VERSION}</version>
  <description>${DESC}</description>

  <dependencies>
EOF

for JAR in `find "${ROOT}/lib/" -iname "*.jar" | sort` ; do
    FILENAME=`basename ${JAR}`
    FILENAME=${FILENAME%.*}
    FILENAME=${FILENAME/%-SNAPSHOT/-snapshot}

    echo "Deploying ${GROUPID}:${FILENAME}:${VERSION}"
    mvn deploy:deploy-file -q -Durl="${REPO}" \
                              -DrepositoryId="${REPOID}" \
                              -Dfile=${JAR} \
                              -DgroupId=${GROUPID} \
                              -DartifactId="${FILENAME}" \
                              -Dversion="${VERSION}" \
                              -Dpackaging=jar \
                              -DgeneratePom=true \
                              -DgeneratePom.description="IntelliJ SDK JARs"

    cat >> ${SDKPOM} <<EOF
    <dependency>
      <groupId>${GROUPID}</groupId>
      <artifactId>${FILENAME}</artifactId>
      <version>${VERSION}</version>
    </dependency>
EOF

done

cat >> ${SDKPOM} <<EOF
  </dependencies>
</project>
EOF

echo "Deploying ${GROUPID}:sdk:${VERSION}"
mvn deploy:deploy-file -q -Durl="${REPO}" \
                          -DrepositoryId="${REPOID}" \
                          -Dfile=${SDKPOM} \
                          -DgroupId=${GROUPID} \
                          -DartifactId=sdk \
                          -Dversion="${VERSION}" \
                          -Dpackaging=pom \
                          -DgeneratePom=false

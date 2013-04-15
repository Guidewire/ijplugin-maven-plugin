#!/bin/sh
# You need to run this script inside IDEA sources repository
# You can clone it from here: git://git.jetbrains.org/idea/community.git
# Usage: deploy-native.sh IC-123.155

set -e

REPO=http://nexus/content/repositories/thirdparty/
REPOID=gw.thirdparty
GROUPID="com.jetbrains.intellij.ideace"
ARTIFACTID="native"
VERSION="${1}"

if [ -z "${VERSION}" ] ; then
    echo "Script to upload IntelliJ CE native libraries to the Nexus repository."
    echo "You need to run this script in the IDEA CE git repository (git://git.jetbrains.org/idea/community.git)!"
    echo "Usage: deploy-native.sh <version>"
    echo "Version is IDEA CA version (as displayed in the build.txt)"
    exit 1
fi

#TAG="${VERSION/IC-/idea/}"
#git fetch
#git checkout "${TAG}"

# Create archives with binaries
for platform in linux mac win ; do
  pushd "bin/${platform}"

  FILENAME="bin=${VERSION}-${platform}.zip"
  rm -f "${FILENAME}" && zip -r "${FILENAME}" .

  
  CLASSIFIER="${platform}"

  mvn deploy:deploy-file -q -Durl="${REPO}" \
                            -DrepositoryId="${REPOID}" \
                            -Dfile=${FILENAME} \
                            -DgroupId=${GROUPID} \
                            -DartifactId="${ARTIFACTID}" \
                            -Dclassifier="${CLASSIFIER}" \
                            -Dversion="${VERSION}" \
                            -Dpackaging=zip \
                            -DgeneratePom=false
  popd
done

POM=`mktemp`
cat > ${POM} <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd" xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <modelVersion>4.0.0</modelVersion>
  <groupId>${GROUPID}</groupId>
  <artifactId>${ARTIFACTID}</artifactId>
  <version>${VERSION}</version>
  <description>IDEA CE native libraries </description>

</project>
EOF

mvn deploy:deploy-file -q -Durl="${REPO}" \
                          -DrepositoryId="${REPOID}" \
                          -Dfile=${POM} \
                          -DgroupId=${GROUPID} \
                          -DartifactId="${ARTIFACTID}" \
                          -Dversion="${VERSION}" \
                          -Dpackaging=pom \
                          -DgeneratePom=false
rm -f "${POM}"


#!/bin/bash

MCV=$(grep "minecraft_version" gradle.properties)
MCVS=(${MCV//=/ })
MINECRAFTVERSION=${MCVS[1]}

JV=$(grep "java_version" gradle.properties)
JVS=(${JV//=/ })
JAVAVERSION=${JVS[1]}

VC=$(grep "version_channel" gradle.properties)
VCS=(${VC//=/ })
VERSIONCHANNEL=${VCS[1]}

MCVERSIONS=$(curl -H "Accept: application/json" -H "Content-Type: application/json" --header "X-Api-Token: $CURSEFORGEACCESSTOKEN" https://minecraft.curseforge.com/api/game/versions)
echo $MCVERSIONS>gameversions.json

GAMEVERSIONS=$(groovy gameversions.groovy $MINECRAFTVERSION $JAVAVERSION)
rm gameversions.json

CHANGELOG=$(git log --format=%B -n 1 $TRAVIS_COMMIT)
CHANGELOG=$(sed 's/\\/\\\\/g' <<< "$CHANGELOG")
CHANGELOG=$(sed ':a;N;$!ba;s/\n/\\n/g' <<< "$CHANGELOG")
CHANGELOG=$(sed 's/\"/\\\"/g' <<< "$CHANGELOG")

OBFFILE=$(ls build/libs/*-obf.jar)
curl --header "X-Api-Token: $CURSEFORGEACCESSTOKEN" --form "file=@$OBFFILE;filename=$CURSEFORGE_NAME-$TRAVIS_BRANCH.jar" --form metadata="{\"displayName\": \"$CURSEFORGE_NAME-$TRAVIS_BRANCH\", \"changelog\":\"$CHANGELOG\", \"changelogType\": \"markdown\", \"gameVersions\": $GAMEVERSIONS, \"releaseType\": \"$VERSIONCHANNEL\"}" https://minecraft.curseforge.com/api/projects/$CURSEFORGE_PROJECTID/upload-file
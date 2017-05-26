#!/bin/bash

git config --global user.email "$GITWEBPAGES_USER@users.noreply.github.com"
git config --global user.name "Travis-Maven"

git clone https://$GITWEBPAGES_USER:$GITACCESSTOKEN@github.com/$GITWEBPAGES_USER/$GITWEBPAGES_USER.github.io.git

mkdir -p "$GITWEBPAGES_USER.github.io/maven2/$GITWEBPAGES_GROUP/$GITWEBPAGES_NAME/$TRAVIS_BRANCH/"
cp -r "./build/libs/." "./$GITWEBPAGES_USER.github.io/maven2/$GITWEBPAGES_GROUP/$GITWEBPAGES_NAME/$TRAVIS_BRANCH/"

cd "$GITWEBPAGES_USER.github.io"

git add *
git commit -m "Uploading maven artifacts for $GITWEBPAGES_NAME for $TRAVIS_BRANCH"
git push

cd ./..
rm -rf "$GITWEBPAGES_USER.github.io"
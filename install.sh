# $1=URL Base
# $2=Zip File
# $3=Root Zip Dir

REPO=$PWD
wget "$1$2"
cd ./../
unzip -q "$REPO/$2"
echo "$PWD/$3"
cd $REPO
rm $2
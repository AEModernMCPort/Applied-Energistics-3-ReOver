#!/bin/bash

if [[ $1 = "obfuscate" ]]
then
	for f in $(ls build/libs/*obf.jar)
	do mv $f $(echo $f | sed s/obf.jar/jar.obf/)
	done
else
	for f in $(ls build/libs/*jar.obf)
	do mv $f $(echo $f | sed s/jar.obf/obf.jar/)
	done
fi

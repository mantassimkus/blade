#!/bin/sh

mkdir bin

javac -verbose -d bin ./src/org/semanticweb/clipperrules/*.java

cd ./bin

jar -cvf clipper-rules.jar * 

mv clipper-rules.jar ../



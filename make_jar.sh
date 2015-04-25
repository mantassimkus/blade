#!/bin/sh

mkdir bin

javac -verbose -d bin ./src/org/semanticweb/blade/*.java

cd ./bin

jar -cvf blade.jar *

mv blade.jar ../



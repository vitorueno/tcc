#!/bin/bash

dir="experimento_refactor/"
mkdir -p $dir
for project in $(cat ~/repos); do
  echo $project
  destino=$(basename ${project})
  mvn -q exec:java -Dexec.mainClass="br.edu.ifc.blumenau.analyzer.Main" -Dexec.args="$project" > "$dir/$destino.log"
done

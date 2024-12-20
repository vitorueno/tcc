#!/bin/bash

dir="experimento_open_source/"
mkdir -p $dir
for project in $(cat ~/repos); do
  echo $project
  destino=$(basename ${project})
  mvn -q exec:java -Dexec.mainClass="br.edu.ifc.blumenau.analyzer.Main" -Dexec.args="$project true true" >> "$dir/$destino.log"
done

#!/bin/bash

mkdir -p experimento/
for project in $(cat ~/repos); do
  echo $project
  destino=$(basename ${project})
  mvn -q exec:java -Dexec.mainClass="br.edu.ifc.blumenau.analyzer.Main" -Dexec.args="$project" > "experimento/$destino.log"
done

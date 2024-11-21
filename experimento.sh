#!/bin/bash

mkdir -p experimento2/
for project in $(cat ~/repos); do
  echo $project
  destino=$(basename ${project})
  mvn -q exec:java -Dexec.mainClass="br.edu.ifc.blumenau.analyzer.Main" -Dexec.args="$project" > "experimento2/$destino.log"
done

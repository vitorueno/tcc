#!/bin/bash

dir="experimento_reducao_assertion_roulette/"
mkdir -p $dir
for project in $(cat ~/repos); do
  echo $project
  destino=$(basename ${project})
  echo -e "Antes do refactor: \n" >> "$dir/$destino.log"
  mvn -q exec:java -Dexec.mainClass="br.edu.ifc.blumenau.analyzer.Main" -Dexec.args="$project false false" >> "$dir/$destino.log"
  echo -e "\nApós o refactor: \n" >> "$dir/$destino.log"
  mvn -q exec:java -Dexec.mainClass="br.edu.ifc.blumenau.analyzer.Main" -Dexec.args="$project true false" >> "$dir/$destino.log"
done

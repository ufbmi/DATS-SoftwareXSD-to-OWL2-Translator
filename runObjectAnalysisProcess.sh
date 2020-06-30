#!/bin/sh

dttm=`date "+%Y-%m-%dT%H%M"`
dt=`date "+%Y-%m-%d"`

echo $dttm
echo $dt

mvn exec:java -Dexec.mainClass="edu.ufl.bmi.misc.AnalyzeMdcObjectStructure" -Dexec.arguments="src/main/resources/process-config.txt, $dt" -Dexec.cleanupDaemonThreads=false > output-analysis-$dttm.txt 2> output-analysis-$dttm.err

echo "finished processing...done.\n"

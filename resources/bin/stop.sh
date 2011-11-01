#!/bin/bash

d=`dirname $0`/..
export TK_HOME=`pwd $d`

echo -n "Stopping Apache Tomcat 6.0.33..."
cd ${TK_HOME}/apache-tomcat-6.0.33/bin
./shutdown.sh &>/dev/null

if [ $? == 0 ]; then
	echo " done."
fi

exit 0
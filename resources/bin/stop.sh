#!/bin/bash

echo -n "Stopping Apache Tomcat 6.0.33..."
cd ~/tk/apache-tomcat-6.0.33/bin
./shutdown.sh &>/dev/null

if [ $? == 0 ]; then
	echo " done."
fi

exit 0
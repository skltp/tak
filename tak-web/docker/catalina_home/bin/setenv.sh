#export LANG=sv_SE.utf8
#export LC_ALL=sv_SE.utf8

#CATALINA_OPTS="-Xms1024m -Xmx1024m -XX:NewSize=256m -XX:PermSize=128m -XX:MaxPermSize=512m -server -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8866 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.rmi.port=8866 -Dlogback.configurationFile=/etc/tomcat/tak/conf/logback.xml"

# set environment variables, beginning with TAK_DATABASE, as properties to tomcat
CATALINA_PROPERTIES=$CATALINA_HOME/conf/catalina.properties
echo >> $CATALINA_PROPERTIES
echo '#  - specific tak application properties' >> $CATALINA_PROPERTIES
for v in $(env|grep -o '^TAK_DATABASE[^=]*')
do
    echo $(tr '_[:upper:]' '.[:lower:]' <<< $v)=$(eval echo \$$v) >> $CATALINA_PROPERTIES
done
echo '#  ! specific tak application properties' >> $CATALINA_PROPERTIES
echo >> $CATALINA_PROPERTIES

# redirect log to stdout
ln -s /dev/stdout $CATALINA_HOME/logs/$APP_NAME.log
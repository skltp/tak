#!/bin/bash

# This is a program that can be called by another script (see example.sh in this folder),
# or fed with the parameters on the command-line (then you will be promted for values only).
# The arguments in the calling script (no specific order) should be:
# 1. URL to the server: url=<url:port>
# 2. username: un=<username>
# 3. password: pw=<password>
# 4. filename to be processed (inclusive path): file=<filename>
# The content of the file (if valid json) will be added/deleted to/from the database on the server.
# This is done via a request to a REST interface at tak-web.
# The program returns a list of the processed items if the json file was ok, or, in the opposite case, a list of errors.

for arg in "$@"; do
if [[ $arg == url=* ]]
then
urlstring=${arg:4}
fi
if [[ $arg == un=* ]]
then
username=${arg:3}
fi
if [[ $arg == pw=* ]]
then
password=${arg:3}
fi
if [[ $arg == file=* ]]
then
filename=${arg:5}
fi
done

if [ -z $urlstring ]
then
echo URL:
read urlstring
fi
if [ -z $username ]
then
echo Username:
read username
fi
if [ -z $password ]
then
read -sp $'Password:\n' password
fi
if [ -z $filename ]
then
echo Filename:
read filename
fi

#Check input...
if [ -z "$urlstring" ]
then
echo "NOT OK No URL..."

elif [ -z "$username" ]
then
echo "NOT OK No username..."

elif [ -z "$password" ]
then
echo "NOT OK No password..."

elif [ -z "$filename" ]
then
echo "NOT OK No filename present..."

else
#Call login to check credentials..
TEST=`curl -s --max-time 15 --cookie nada --location --user-agent "Chrome/69.0.3497.100" --data "username=$username&password=$password" ${urlstring}/tak-web/auth/login`

substring="Ogiltigt"
if [ "${TEST/$substring}" = "$TEST" ] ; then
echo LOGIN OK

DATE=$(date +"%d-%m-%Y--%H-%M")

URL=`curl --write-out "\nRESPONSE CODE=%{http_code}\n" -s --max-time 15 --cookie nada --location --user-agent "Chrome/69.0.3497.100" --data "username=$username&password=$password" ${urlstring}/auth/login --next -s --cookie nada --location --user-agent "Chrome/69.0.3497.100" --data-urlencode @$filename ${urlstring}/rest/create`

echo "Resultatet har sparats till fil: bestallning_$DATE.txt"

echo OUTPUT BEGIN --------------------------------------
echo "${URL##*</html>}" >> "bestallning_$DATE.txt"
echo "${URL##*</html>}"
echo OUTPUT END ----------------------------------------
else
echo NOT OK
echo Det gick inte att logga in. Kontrollera username och password.
fi
fi
exit 0;

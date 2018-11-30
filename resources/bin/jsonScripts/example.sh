#!/bin/bash
# This is an example of a shell-script using the script jsonbestallning.sh contained in this folder.
# That program can be called with the arguments below, as name=value pairs.
# The names are url, un (username), pw (password) and file.
# The order of the arguments is insignificant and if any is missing, you will be prompted to enter it.
# So you can, for example, leave out your password and write it when you have called the script.
# The command below presumes that jsonbestallning.sh and the json file is in the same directory as this script.
# Note that the url should also have portnumber. Also note that the jsonfile extension is irrelevant (.json or .txt).
# The functionality to handle this script is present in tak-web versions 2.3.4 and above.
./jsonbestallning.sh url=http://ine-dit-app01.sth.basefarm.net:8085 un=username file=jsonfile.txt pw=password

#!/bin/bash
# This is an example of a shellscript using the script jsonbestallning.sh contained in this folder.
# That program can be called with the arguments below, as name=value pairs.
# The names are url, un (username), pw (password) and file.
# The order of the arguments is insignificant and if any is missing, you will be prompted to enter it.
# So you can, for example, leave out your password and write it when you call the script.
# The command below presumes that jsonbestallning.sh and the json file is in the same directory as this script.
# Note that the url should also have portnumber.
# The functionality to handle this script is present in Tak versions 2.3.4 and above.
# The command below requires that all three (this file, jsonbestallning.sh and vagval.txt) files are in the same directory.
./jsonbestallning.sh url=http://ine-dit-app01.sth.basefarm.net:8085 un=hbragman file=vagval.txt

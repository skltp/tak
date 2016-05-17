============================================================
README.txt
============================================================
Scripts to export TAK-data as file and upload the file to an SFTP-server where it can be
consumed by other parties, for example the TAK-API.

Ref:
a) file format, https://skl-tp.atlassian.net/wiki/display/NTJP/TAK+versionshantering
b) overview pictures, https://skl-tp.atlassian.net/browse/SKLTP-815


Install and configure
------------------------------
1. Put these scripts in the same directory on the server where they should run:
	tak-export-env-setup.sh
	tak-export.sh
	TakExport.groovy

2. Configure environment variables in:
	tak-export-env-setup.sh

3. Schedule script to be run by cron (at 01.00 every night):
	tak-export.sh

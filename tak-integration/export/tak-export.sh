#!/bin/bash
#=============================================================================
# tak-export.sh
#
# Export TAK-data to file from TAK-database and send the file to an
# SFTP-server.
#=============================================================================

#-----------------------------------------------------------
# Simple check to avoid running multiple instances via cron
#-----------------------------------------------------------
lock=/tmp/tak-export-lockdir

if mkdir ${lock}; then
  trap 'rmdir ${lock}' EXIT
else
  exit 1
fi

printlog(){
	level=$1
	message=$2
	printf "{\"@timestamp\":\"$(date '+%Y-%m-%dT%T.%3N')\",\"level\":\"%s\",\"message\":\"%s\"}\n" "$level" "$message"
}

# break on error
set -e

cd `dirname $0`
#-----------------------------------------------------------
# Configure per environment.
#-----------------------------------------------------------
# setup environment
. tak-export-env-setup.sh

#-----------------------------------------------------------
# Check environment.
#-----------------------------------------------------------
mkdir -p ${tmpDir}
rm ${logFile}

printlog "INFO" "Begin: ny tak-export" >> ${logFile}
printlog "INFO" "Begin: environment check" >> ${logFile}
# check that groovy is available
groovyVesion=$(groovy -version)
printlog "INFO" "$groovyVesion" >> ${logFile} 2>&1
printlog "INFO" "Done: environment check" >> ${logFile}

#-----------------------------------------------------------
# Export TAK data.
#-----------------------------------------------------------
printlog "INFO" "Begin: TAK-export: `date`" >> ${logFile}
# TAK export file name - MUST be named like this for the cooperation-app to
# parse TAK-site and TAK-environment from filename
exportFile=${tmpDir}/takdump_${takSite}_${takEnvironment}.json
groovy -Dgroovy.grape.report.downloads=true -Dgrape.root=./grape_repo TakExport.groovy \
	-u ${takDbUser} -p ${takDbPassword} -d ${takDbName} -s ${takDbHost} -f ${exportFile}

printlog "INFO" "Done: TAK-export: `date`" >> ${logFile}

#-----------------------------------------------------------
# Upload TAK data.
#-----------------------------------------------------------
printlog "INFO"  "Begin: SFTP-upload: `date`" >> ${logFile}

printlog "INFO"  "Will do: sftp -o IdentityFile=${sshIdentityFile} ${sftpUser}@${sftpHost}" >> ${logFile}

sftp -o IdentityFile=${sshIdentityFile} ${sftpUser}@${sftpHost} <<EOF
put ${exportFile} ${sftpRemotePath}
EOF

printlog "INFO"  "Done: SFTP-upload: `date`" >> ${logFile}

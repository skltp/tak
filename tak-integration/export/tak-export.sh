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
logFile=${tmpDir}/tak-export.log
# clear logfile
date > ${logFile}
echo "Begin: environment check" >> ${logFile}
# check that groovy is available
groovy -version >> ${logFile} 2>&1
# check locale
locale >> ${logFile} 2>&1
echo "Done: environment check" >> ${logFile}

#-----------------------------------------------------------
# Export TAK data.
#-----------------------------------------------------------
echo "Begin: TAK-export: `date`" >> ${logFile}
# TAK export file name - MUST be named like this for the cooperation-app to
# parse TAK-site and TAK-environment from filename
exportFile=${tmpDir}/takdump_${takSite}_${takEnvironment}.json
groovy -Dgroovy.grape.report.downloads=true -Dgrape.root=./grape_repo TakExport.groovy \
	-u ${takDbUser} -p ${takDbPassword} -d ${takDbName} -s ${takDbHost} \
    > ${exportFile} 2>> ${logFile}
echo "Done: TAK-export: `date`" >> ${logFile}

#-----------------------------------------------------------
# Upload TAK data.
#-----------------------------------------------------------
echo "Begin: SFTP-upload: `date`" >> ${logFile}

echo "Will do: sftp -o IdentityFile=${sshIdentityFile} ${sftpUser}@${sftpHost}" >> ${logFile}

sftp -o IdentityFile=${sshIdentityFile} ${sftpUser}@${sftpHost} <<EOF
put ${exportFile} ${sftpRemotePath}
EOF

echo "Done: SFTP-upload: `date`" >> ${logFile}

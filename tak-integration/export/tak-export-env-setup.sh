#=============================================================================
# Configure per environment.
#=============================================================================

#-----------------------------
# TAK config
#-----------------------------
# takSite: [ntjp | sll]
takSite=${TAK_EXPORT_SITE}
# takEnvironment: [prod | qa]
takEnvironment=${TAK_EXPORT_ENVIRONMENT}
takDbUser=${TAK_EXPORT_DB_USER}
takDbPassword=${TAK_EXPORT_DB_PASSWORD}
takDbName=${TAK_EXPORT_DB_NAME}
takDbHost=${TAK_EXPORT_DB_HOST}

#-----------------------------
# SFTP-server config
#-----------------------------
sftpHost=${TAK_EXPORT_SFTP_HOST}
sftpUser=${TAK_EXPORT_SFTP_USER}
sftpRemotePath=${TAK_EXPORT_SFTP_PATH}
sshIdentityFile=${TAK_EXPORT_SFTP_KEYFILE}

#-----------------------------
# Script environment config
#-----------------------------
logFile=/dev/stdout
tmpDir=/tmp/tak-export
# set charset for java/groovy output
#   on Redhat EL 6: en_US.utf8
#   on MacOSX: en_US.UTF-8
export LC_CTYPE=en_US.utf8
# groovy must be in path
export PATH=${PATH}:/local/java/groovy/bin

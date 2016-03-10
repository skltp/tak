#=============================================================================
# Configure per environment.
#=============================================================================

#-----------------------------
# TAK config
#-----------------------------
# takSite: [ntjp | sll]
takSite=ntjp
# takEnvironment: [prod | qa]
takEnvironment=qa
takDbUser=ntjpstat
takDbPassword=XXX
takDbName=takv2
takDbHost=ine-sit-db-vip.sth.basefarm.net

#-----------------------------
# SFTP-server config
#-----------------------------
# Sjunet IP for: ine-pib-misc01.sth.basefarm.net
#   is: 82.136.149.50
sftpHost=ine-pib-misc01.sth.basefarm.net
sftpUser=ntjpqa
sftpRemotePath="/upload/"
sshIdentityFile=~/.ssh/ntjpqa

#-----------------------------
# Script environment config
#-----------------------------
tmpDir=/tmp/tak-export
# set charset for java/groovy output
#   on Redhat EL 6: en_US.utf8
#   on MacOSX: en_US.UTF-8
export LC_CTYPE=en_US.utf8
# groovy must be in path
export PATH=${PATH}:/local/java/groovy/bin

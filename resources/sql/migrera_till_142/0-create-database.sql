
#############################################################
# Skapa databas TAK V2
#############################################################

# Inloggad som root
# mysql -u root -p

# SKLTP där tp_admin är användare
# create database takv2 character set utf8;
# grant usage on takv2.* to tp_adminuser@localhost identified by '<ett lösenord>';
# grant all privileges on takv2.* to  'tp_adminuser'@'localhost';
# flush privileges;

# SKLTP-BOX där tkuser är användare
create database takv2 character set utf8;
grant usage on takv2.* to tkuser@localhost identified by 'secret';
grant all privileges on takv2.* to  'tkuser'@'%';
flush privileges;

# Logga ut användaren root

#############################################################
# 1: Skapa tabeller i TAK V2
#############################################################

# Logga in med användare tkuser (SKLTP-BOX) eller tp_admin (SKLTP)
# mysql -u tkuser -p
# mysql -u tp_admin -p

# Välj databas
# use takv2;

# Kör tak-142-ddl.sql
# source /home/skltp/1-create-tak-142-ddl.sql

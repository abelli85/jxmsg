SET KAFKA_HEAP_OPTS=-Djava.security.auth.login.config=zk_server_jaas.conf
rem start "zookeeper-3.5.5" cmd /c "bin\windows\zookeeper-server-start.bat config\zookeeper.properties"
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
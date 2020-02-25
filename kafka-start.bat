SET KAFKA_HEAP_OPTS=-Djava.security.auth.login.config=kafka_server_jaas.conf
rem start "kafka_2.12-2.4.0" cmd /c "bin\windows\kafka-server-start.bat config\server.properties"
bin\windows\kafka-server-start.bat config\server.properties

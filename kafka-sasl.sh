export KAFKA_HEAP_OPTS="-Djava.security.auth.login.config=/home/abel/kafka_server_jaas.conf"
kafka-server-start.sh "$@"

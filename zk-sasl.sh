export KAFKA_HEAP_OPTS="-Djava.security.auth.login.config=/home/abel/zk_server_jaas.conf"
zookeeper-server-start.sh "$@"

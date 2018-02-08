java -jar ASL-worker-threads.jar -l 127.0.0.1 -p 11212 -t 20 -s true -m 127.0.0.1:11213 127.0.0.1:11214 127.0.0.1:11215 &
experimentID=$!
sleep 5
memtier_benchmark --protocol=memcache_text -t 4 -c 5 -s 127.0.0.1 -p 11212
kill $experimentID

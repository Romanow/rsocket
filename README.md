# RSocket protocol

```shell
$ ./gradlew clean build

$ curl http://localhost:8080/api/v1/generate

$ brew install making/tap/rsc

$ rsc --route generate --debug tcp://localhost:7090
2021-12-24 00:46:34.099 DEBUG 9089 --- [actor-tcp-nio-2] io.rsocket.FrameLogger                   : sending -> 
Frame => Stream ID: 0 Type: SETUP Flags: 0b0 Length: 75
Data:

2021-12-24 00:46:34.100 DEBUG 9089 --- [actor-tcp-nio-2] io.rsocket.FrameLogger                   : sending -> 
Frame => Stream ID: 1 Type: REQUEST_RESPONSE Flags: 0b100000000 Length: 22
Metadata:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| fe 00 00 09 08 67 65 6e 65 72 61 74 65          |.....generate   |
+--------+-------------------------------------------------+----------------+
Data:

2021-12-24 00:46:34.227 DEBUG 9089 --- [actor-tcp-nio-2] io.rsocket.FrameLogger                   : receiving -> 
Frame => Stream ID: 1 Type: NEXT_COMPLETE Flags: 0b1100000 Length: 194
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| d1 82 d1 8b 20 d1 87 d1 83 d0 b5 d1 88 d1 8c 3f |.... ..........?|
|00000010| 20 d0 b2 d0 ba d1 83 d1 81 d0 bd d0 be d0 b5 20 | .............. |
|00000020| d1 80 d0 be d0 b6 d0 b4 d0 b5 d0 bd d1 8c d0 b5 |................|
|00000030| 20 d0 bf d0 b5 d1 80 d0 b5 d0 b4 d0 be d0 bc d0 | ...............|
|00000040| be d0 bd d0 be d0 b9 20 d1 81 d1 82 d0 b8 d1 85 |....... ........|
|00000050| d0 be d0 b2 20 d0 bb d0 b8 d1 81 d1 82 d1 8b 20 |.... .......... |
|00000060| d0 ba d0 b0 d0 ba 20 d0 b4 d0 b5 d1 82 d0 be d1 |...... .........|
|00000070| 80 d0 be d0 b4 d0 bd d0 be d0 b5 20 d0 bf d0 b0 |........... ....|
|00000080| d1 80 d0 b5 d0 bd d1 8c d0 b5 20 d0 ba d0 b0 d0 |.......... .....|
|00000090| ba 20 d1 81 d0 bf d0 b0 d1 80 d1 82 d0 b0 20 d0 |. ............ .|
|000000a0| b3 d1 80 d1 8f d0 b7 d0 bd d0 be d0 b9 20 d1 81 |............. ..|
|000000b0| d1 80 d0 b0 d0 bc d0 be d1 82 d1 8b             |............    |
+--------+-------------------------------------------------+----------------+
```

Для просмотра используемых потоков использовать параметры запуска:

```shell
-Djava.rmi.server.hostname=localhost \
  -Dcom.sun.management.jmxremote.port=1199 \
  -Dcom.sun.management.jmxremote.authenticate=false 
```

#### Stress testing

```shell
$ brew install k6

$ k6 run --out influxdb=http://localhost:8086/k6 scripts/k6.js
```
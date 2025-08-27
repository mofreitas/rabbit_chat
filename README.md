# Projeto de CHAT usando RabbitMQ

Projeto exemplo com o objetivo de aprender a utilizar o RabbitMQ.

O sistema permite que o usuário converse com todos os membros das salas, mas também permite que possa enviar para usuários de determinados grupos, usando #, ou para usuários individuais, usando o @. Para sair, pode-se usar o ctrl+c ou digitar \Sair.

Nesse caso, foram utilizados exchange do tipo Topic com CC para a transmissão de mensagens diretas e exchange do tipo fanout para a transmissão de mensagens para todos os usuários

Além disso, para apresentar os dados na tela, foi utilizada a bilioteca JLINE. 

## Build e execução

Primeiramente é necessário executar o docker com o servidor do RabbitMQ

```sh
# sudo docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4-management
```
Em seguida, pode construir e rodar o projeto usando o gradle

```sh
# ./gradlew build 
# ./gradlew run --args="nome_usuario grupo1 grupo2 ..." --console=plain
```

Porém, para que o formatador do terminal funcione adequadamente, é necessário rodar a aplicação usando o java

```sh
# /usr/lib/jvm/jdk-21.0.6-oracle-x64/bin/java -Dfile.encoding=UTF-8 -Duser.country=BR -Duser.language=pt -Duser.variant -cp /media/matheus/Dados1/MessageApp/MessageApp/lib/build/classes/java/main:/media/matheus/Dados1/MessageApp/MessageApp/lib/build/resources/main:/home/matheus/.gradle/caches/modules-2/files-2.1/org.apache.commons/commons-math3/3.6.1/e4ba98f1d4b3c80ec46392f25e094a6a2e58fcbf/commons-math3-3.6.1.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/com.google.guava/guava/33.1.0-jre/9b7ed39143d59e8eabcc6f91ffe4d23db2efe558/guava-33.1.0-jre.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-simple/2.0.17/9872a3fd794ffe7b18d17747926a64d61526ca96/slf4j-simple-2.0.17.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/com.rabbitmq/amqp-client/5.26.0/e2bb667eb2878704f1bd82d069f38530727e45a2/amqp-client-5.26.0.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/2.0.17/d9e58ac9c7779ba3bf8142aff6c830617a7fe60f/slf4j-api-2.0.17.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/org.jline/jline/3.30.0/2d6ea422bc99ff05770ca80cae3d8381c65e70bf/jline-3.30.0.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/org.jline/jline-terminal-jni/3.25.1/88a7bc9c0a9d896afe176440122618cf07ed8e12/jline-terminal-jni-3.25.1.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/com.google.guava/failureaccess/1.0.2/c4a06a64e650562f30b7bf9aaec1bfed43aca12b/failureaccess-1.0.2.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/com.google.guava/listenablefuture/9999.0-empty-to-avoid-conflict-with-guava/b421526c5f297295adef1c886e5246c39d4ac629/listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/com.google.code.findbugs/jsr305/3.0.2/25ea2e8b0c338a877313bd4672d3fe056ea78f0d/jsr305-3.0.2.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/org.checkerframework/checker-qual/3.42.0/638ec33f363a94d41a4f03c3e7d3dcfba64e402d/checker-qual-3.42.0.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/com.google.errorprone/error_prone_annotations/2.26.1/c1fde57694bdc14e8618899aaa6e857d9465d7de/error_prone_annotations-2.26.1.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/org.jline/jline-terminal/3.25.1/44e0e53397c39d01c525bad3735097596a7d889a/jline-terminal-3.25.1.jar:/home/matheus/.gradle/caches/modules-2/files-2.1/org.jline/jline-native/3.25.1/7f58e474f7d94db5bf87b1fddf4fa646475779f2/jline-native-3.25.1.jar br.com.mathe.MessageApp.MessageApp nome_usuario grupo1 grupo2
```


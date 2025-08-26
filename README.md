# Projeto de CHAT usando RabbitMQ

Projeto exemplo com o objetivo de aprender a utilizar o RabbitMQ.

O sistema permite que o usuário converse com todos os membros das salas, mas também permite que possa enviar para usuários de determinados grupos, usando #, ou para usuários individuais, usando o @

Nesse caso, foram utilizados exchange do tipo Topic com CC para a transmissão de mensagens diretas e exchange do tipo fanout para a transmissão de mensagens para todos os usuários

Além disso, para apresentar os dados na tela, foi utilizada a bilioteca JLINE. 

## Build e execução

Primeiramente é necessário executar o docker com o servidor do RabbitMQ

```sh
# . /gradlew build 
# . /gradlew run --args="nome_usuario grupo1 grupo2 ..." --console=plain
```
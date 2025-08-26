package br.com.mathe.MessageApp;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class ConsumerRunner implements Runnable {
	private static final String EXCHANGE_GERAL = "msgs_geral"; 
	private static final String EXCHANGE_DIRECT = "msgs_diretas";
	
	private Connection connection;
	private Channel channel;
	private String nomeUsuario;
	private List<String> tagsUsuario;
	private CloseEvent closeEvent;
	private BlockingQueue<String> queueMensagensRecebidas;
	
	public ConsumerRunner(ConnectionFactory connectionFactory, String nomeUsuario, List<String> tagsUsuario, CloseEvent closeEvent, BlockingQueue<String> queueMensagensRecebidas) throws IOException, TimeoutException {
		this.connection = connectionFactory.newConnection();
		this.channel = connection.createChannel();
		this.nomeUsuario = nomeUsuario;
		this.tagsUsuario = tagsUsuario;
		this.closeEvent = closeEvent;
		this.queueMensagensRecebidas = queueMensagensRecebidas;
	}
	
	@Override
	public void run() {		
		try {
			String queueName = channel.queueDeclare().getQueue();
			String bindingNomeusuario = "*." + nomeUsuario;
			channel.exchangeDeclare(EXCHANGE_DIRECT, "topic");
			channel.exchangeDeclare(EXCHANGE_GERAL, "fanout");
			
			channel.queueBind(queueName, EXCHANGE_DIRECT, bindingNomeusuario);
			channel.queueBind(queueName, EXCHANGE_GERAL, "");
			
			for (String bindingKey : tagsUsuario) {
				String bindingGrupoUsuario = bindingKey + ".#";
		        channel.queueBind(queueName, EXCHANGE_DIRECT, bindingGrupoUsuario);
		    }
			
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String mensagemTexto = new String(delivery.getBody(), "UTF-8");
				
				Mensagem mensagem = new Mensagem(mensagemTexto);
				if(mensagem.getNomeUsuario() == null || !mensagem.getNomeUsuario().equals(nomeUsuario))
					queueMensagensRecebidas.add(mensagemTexto);
				
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			};
			
			channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });
			
			this.closeEvent.setRunnable(() -> {
				try {
					this.channel.close();
					this.connection.close();
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}

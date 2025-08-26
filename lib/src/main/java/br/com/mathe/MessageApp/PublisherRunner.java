package br.com.mathe.MessageApp;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import javax.xml.datatype.Duration;
import javax.xml.datatype.DatatypeConstants.Field;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class PublisherRunner implements Runnable {

	private static final String MENSAGEM_SAIR = "/Sair";
	private static final String EXCHANGE_GERAL = "msgs_geral"; 
	private static final String EXCHANGE_DIRECT = "msgs_diretas";
	
	private Connection connection;
	private Channel channel;
	private String nomeUsuario;
	private CloseEvent closeEvent;
	private BlockingQueue<Mensagem> queueMensagensDigitadas;
		
	public PublisherRunner(ConnectionFactory connectionFactory, String nomeUsuario, CloseEvent closeEvent, BlockingQueue<Mensagem> queueMensagensDigitadas) throws IOException, TimeoutException {
		this.connection = connectionFactory.newConnection();
		this.channel = connection.createChannel();
		this.nomeUsuario = nomeUsuario;
		this.closeEvent = closeEvent;
		this.queueMensagensDigitadas = queueMensagensDigitadas;
	}
	
	@Override
	public void run() {		
		try {
			channel.exchangeDeclare(EXCHANGE_DIRECT, "topic");
			channel.exchangeDeclare(EXCHANGE_GERAL, "fanout");
			
			while(true) {	
				Mensagem mensagem = queueMensagensDigitadas.take();
			
			    BasicProperties props = null;
			    if(mensagem.getTipoMensagem() == TipoMensagem.DIRETO) {
			    	props = new BasicProperties.Builder()
				        .headers(mensagem.getCC())
				        .build();
			    	
			    	channel.basicPublish(EXCHANGE_DIRECT, "", props, mensagem.getMensagem().getBytes());
			    }
			    else {
			    	channel.basicPublish(EXCHANGE_GERAL, "", null, mensagem.getMensagem().getBytes());
			    }
			    
			    if(mensagem.tipoMensagem == TipoMensagem.FIM) break;
			}
			
			closeEvent.getRunnable().run();
			
			channel.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}

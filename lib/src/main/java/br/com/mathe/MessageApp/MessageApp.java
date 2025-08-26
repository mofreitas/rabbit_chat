package br.com.mathe.MessageApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.ConnectionFactory;

public class MessageApp {

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		
		String nomeUsuario = args[0];
		List<String> tagsUsuario = new ArrayList<>();
		if(args != null && args.length > 1) {
			tagsUsuario = Arrays.asList(args).subList(1, args.length);
		}
		
//		Terminal terminal = TerminalBuilder.builder().system(true).build();
//		LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).build();
//		
//		terminal.puts(Capability.enter_ca_mode);
//		
//		while (true) {
//			terminal.puts(Capability.cursor_address, terminal.getHeight() - 1,0);
//			terminal.puts(Capability.clr_eos);
//			terminal.flush();
//			
//            String line = lineReader.readLine("> ");
//            
//            lineReader.getTerminal().writer().flush();
//
//            // Exit if requested
//            if ("exit".equalsIgnoreCase(line)) {
//                break;
//            }
//
//            // Echo the line back to the user
//            lineReader.printAbove("void " + line);
//            terminal.flush();
//        }
//
//        terminal.writer().println("Goodbye!");
//        terminal.close();

		BlockingQueue<String> queueMensagensRecebidas = new ArrayBlockingQueue<>(10);
		BlockingQueue<Mensagem> queueMensagensDigitadas = new ArrayBlockingQueue<>(10);
		TelaMensagens tela = new TelaMensagens(nomeUsuario, tagsUsuario, queueMensagensDigitadas, queueMensagensRecebidas);
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		
		CloseEvent closeEvent = new CloseEvent();
		Thread t1 = new Thread(new PublisherRunner(factory, nomeUsuario, closeEvent, queueMensagensDigitadas));
		Thread t2 = new Thread(new ConsumerRunner(factory, nomeUsuario, tagsUsuario, closeEvent, queueMensagensRecebidas));
		
		t1.start();
		t2.start();
		
		tela.init();
		
		t1.join();
		t2.join();		
	}

}

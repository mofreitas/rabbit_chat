package br.com.mathe.MessageApp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;

public class TelaMensagens {
	private List<String> mensagens = new ArrayList<>();
	private Terminal terminal;
	private LineReader lineReader;
	private ExecutorService executorWriter;
	private BlockingQueue<String> queueMensagensRecebidas;
	private BlockingQueue<Mensagem> queueMensagensDigitadas;
	
	private String nomeUsuario;
	private List<String> tagsUsuario;
	
	private Object lock = new Object();
	
	public TelaMensagens(String nomeUsuario, List<String> tagsUsuario, BlockingQueue<Mensagem> queueMensagensDigitadas, BlockingQueue<String> queueMensagensRecebidas) throws IOException {
		terminal = TerminalBuilder.builder().system(true).build();
		lineReader = LineReaderBuilder.builder().terminal(terminal).build();
		
		terminal.puts(Capability.enter_ca_mode);
		
		executorWriter = Executors.newSingleThreadExecutor();
		
		this.queueMensagensRecebidas = queueMensagensRecebidas;
		this.queueMensagensDigitadas = queueMensagensDigitadas;
		
		this.nomeUsuario = nomeUsuario;
		this.tagsUsuario = tagsUsuario;
	}
	
	private void redesenhaTela() {
		terminal.puts(Capability.clear_screen);
		desenhaCabecalho();
		desenhaChat();
		desenhaLinha();
	}
	
	private void desenhaCabecalho() {
		terminal.writer().println("USUÁRIO: " + nomeUsuario + ". TAGS: " + (tagsUsuario.isEmpty() ? "Nenhuma" : String.join(", ", tagsUsuario)));
	}
	
	private void desenhaLinha() {
		String linha = "";
		for(int i = 0; i < terminal.getWidth(); i++) {
			linha += "-";
		}
		terminal.writer().println(linha);
	}
	
	private void desenhaChat() {
		int tamanhoTela = terminal.getHeight() - 3;
		int larguraTela = terminal.getWidth();
		synchronized (lock) {
			int tamanhoListaMensagens = 0;
			int indexInicial = mensagens.size() - 1;
			
			for(; indexInicial >= 0; --indexInicial) {
				tamanhoListaMensagens += Math.ceil(mensagens.get(indexInicial).length() / (float) larguraTela);
				if(tamanhoListaMensagens > tamanhoTela) {
					indexInicial = indexInicial + 1;
					break;
				}				
			}
			
			if(indexInicial < 0) indexInicial = 0;
			
			if(tamanhoListaMensagens < tamanhoTela) {				
				for(int i = 0; i < mensagens.size(); i++) {
					terminal.writer().println(mensagens.get(i));
				}
				
				for(int i = tamanhoListaMensagens; i < tamanhoTela; i++) {
					terminal.writer().println("");
				}
			}
			else {
				for(int i = indexInicial; i < mensagens.size(); i++) {			
					terminal.writer().println(mensagens.get(i));
				}
			}
		}
	}
	
	public void init() {
		redesenhaTela();
		terminal.flush();
		
		executorWriter.submit(() -> {
			while(true) {
				String mensagem = queueMensagensRecebidas.take();
				mensagens.add(mensagem);
				terminal.puts(Capability.save_cursor);
				redesenhaTela();
				lineReader.callWidget(LineReader.REDRAW_LINE);
				lineReader.callWidget(LineReader.REDISPLAY);
				terminal.puts(Capability.restore_cursor);
				lineReader.getTerminal().writer().flush();
			}
		});
		
		Mensagem mensagemBoasVindas = new Mensagem(null, "CHEGOU " + nomeUsuario + "!!! TAGS: " + (tagsUsuario.isEmpty() ? "Nenhuma" : String.join(", ", tagsUsuario)));
		queueMensagensDigitadas.add(mensagemBoasVindas);
		
		while(true) {
			try {
				String line = lineReader.readLine("> ");
				
				if(line.equals("/Sair")) {
					executorWriter.shutdownNow();
					queueMensagensDigitadas.add(new Mensagem(nomeUsuario, line));
					break;
				}
				
				synchronized (lock) {
					mensagens.add("Você" + ": " + line);
				}
				
				redesenhaTela();
				queueMensagensDigitadas.add(new Mensagem(nomeUsuario, line));
				terminal.writer().flush();
			}
			catch(UserInterruptException e) {
				executorWriter.shutdownNow();
				queueMensagensDigitadas.add(new Mensagem(nomeUsuario, "/Sair"));
				break;
			}
		}
				
	}
	
	
}

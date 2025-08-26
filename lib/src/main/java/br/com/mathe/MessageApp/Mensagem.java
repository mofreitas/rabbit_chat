package br.com.mathe.MessageApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mensagem {
	TipoMensagem tipoMensagem;

	private static final Pattern patternUsuario = Pattern.compile("(?<!\\w)@\\w+");
	private static final Pattern patternGrupo = Pattern.compile("(?<!\\w)#\\w+");
	
	List<String> routingKeys; 
	String mensagemBruta;
	String nomeUsuario;
	
	private List<String> separaUsuarios(String mensagemBruta){
		List<String> usuarios = new ArrayList<>();
		
		Matcher matcher = patternUsuario.matcher(mensagemBruta);
		
		usuarios = matcher.results().map((matchResult) -> {
			return matchResult.group(0).substring(1);
		}).collect(Collectors.toList());
	
		return usuarios;
	}
	
	private List<String> separaGrupos(String mensagemBruta){
		List<String> grupos = new ArrayList<>();
		
		Matcher matcher = patternGrupo.matcher(mensagemBruta);
		
		grupos = matcher.results().map((matchResult) -> {
			return matchResult.group(0).substring(1);
		}).collect(Collectors.toList());
		
		return grupos;
	}
	
	public Mensagem(String mensagemBruta) {
		String[] partesMensagem = mensagemBruta.split(": ", 2);
		
		if(partesMensagem.length == 1) {
			this.mensagemBruta = partesMensagem[0];
		}
		else {
			this.nomeUsuario = partesMensagem[0];
			this.mensagemBruta = partesMensagem[1];			
		}
		
		List<String> usuarios = separaUsuarios(mensagemBruta);
		List<String> gruposUsuarios = separaGrupos(mensagemBruta);
		
		routingKeys = new ArrayList<String>();
		for(String usuario : usuarios) {
			routingKeys.add("*."+usuario);
		}
		for(String grupoUsuario : gruposUsuarios) {
			routingKeys.add(grupoUsuario+".#");
		}
		
		if(mensagemBruta.equals("/Sair")) {
			tipoMensagem = TipoMensagem.FIM;
		}
		else if(nomeUsuario == null) {
			tipoMensagem = TipoMensagem.BOASVINDAS;
		}
		else if(routingKeys.size() > 0) {
			tipoMensagem = TipoMensagem.DIRETO;
		}
		else {
			tipoMensagem = TipoMensagem.GERAL;			
		}
	}
		
	public Mensagem(String nomeUsuario, String mensagemBruta) {
		this.nomeUsuario = nomeUsuario;
		this.mensagemBruta = mensagemBruta;
		
		List<String> usuarios = separaUsuarios(mensagemBruta);
		List<String> gruposUsuarios = separaGrupos(mensagemBruta);
		
		routingKeys = new ArrayList<String>();
		for(String usuario : usuarios) {
			routingKeys.add("*."+usuario);
		}
		for(String grupoUsuario : gruposUsuarios) {
			routingKeys.add(grupoUsuario+".#");
		}
		
		if(mensagemBruta.equals("/Sair")) {
			tipoMensagem = TipoMensagem.FIM;
		}
		else if(nomeUsuario == null) {
			tipoMensagem = TipoMensagem.BOASVINDAS;
		}
		else if(routingKeys.size() > 0) {
			tipoMensagem = TipoMensagem.DIRETO;
		}
		else {
			tipoMensagem = TipoMensagem.GERAL;			
		}	
	}
	
	public String getRoutingKey(){
		return routingKeys.isEmpty() ? "" : routingKeys.getFirst();
	}
	
	public Map<String, Object> getCC(){
		Map<String, Object> headers = new HashMap<>();
		headers.put("CC", routingKeys);
		return headers;
	}
	
	public String getMensagem() {
		if(tipoMensagem == TipoMensagem.FIM) {
			return nomeUsuario + " SAIU! ";
		}
		else if(nomeUsuario == null) {
			return mensagemBruta;
		}
		else {
			return nomeUsuario + ": " + mensagemBruta;
		}
	}
}

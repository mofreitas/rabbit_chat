package br.com.mathe.MessageApp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloseEvent {
	public Runnable runnable;
}

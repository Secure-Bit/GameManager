package lib.securebit.game.impl;

import java.util.ArrayList;
import java.util.List;

import lib.securebit.game.JoinHandler;
import lib.securebit.game.JoinListener;

public abstract class CraftJoinHandler implements JoinHandler {
	
	private List<JoinListener> listeners;
	
	public CraftJoinHandler() {
		this.listeners = new ArrayList<>();
	}
	
	@Override
	public void registerListener(JoinListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public List<JoinListener> getListeners() {
		return this.listeners;
	}

}

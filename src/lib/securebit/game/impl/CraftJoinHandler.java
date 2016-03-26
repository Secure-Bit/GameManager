package lib.securebit.game.impl;

import java.util.List;

import lib.securebit.game.JoinHandler;
import lib.securebit.game.JoinListener;

public abstract class CraftJoinHandler implements JoinHandler {
	
	private List<JoinListener> listeners;
	
	@Override
	public void registerListener(JoinListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public List<JoinListener> getListeners() {
		return this.listeners;
	}

}

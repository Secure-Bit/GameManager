package lib.securebit.game.defaults.joinhandler;

import org.bukkit.entity.Player;

import lib.securebit.game.impl.CraftJoinHandler;

public class PreventedJoin extends CraftJoinHandler {

	private String msg;
	
	public PreventedJoin(String message) {
		this.msg = message;
	}
	
	@Override
	public void onJoin(Player player) {
		
	}
	
	@Override
	public void onQuit(Player player) {
		
	}

	@Override
	public String onLogin(Player player) {
		return this.msg;
	}

}

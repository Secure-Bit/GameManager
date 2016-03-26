package lib.securebit.game.defaults.joinhandler;

import org.bukkit.entity.Player;

import lib.securebit.game.JoinListener;
import lib.securebit.game.impl.CraftJoinHandler;

public class PermitedJoin extends CraftJoinHandler {
	
	private String permJoin;
	private String msg;
	
	public PermitedJoin(String permJoin, String message) {
		this.permJoin = permJoin;
		this.msg = message;
	}
	
	@Override
	public void onJoin(Player player) {
		for (JoinListener listener : this.getListeners()) {
			listener.onJoin(player);
		}
	}

	@Override
	public void onQuit(Player player) {
		for (JoinListener listener : this.getListeners()) {
			listener.onQuit(player);
		}
	}

	@Override
	public String onLogin(Player player) {
		String result = null;
		
		if (!this.hasPermission(player, this.permJoin)) {
			result = this.msg;
		}
		
		for (JoinListener listener : this.getListeners()) {
			listener.onLogin(player, result);
		}
		
		return result;
	}
	
	protected boolean hasPermission(Player player, String permission) {
		return player.hasPermission(permission);
	}
	
}

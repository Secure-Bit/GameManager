package lib.securebit.game.defaults.joinhandler;

import org.bukkit.entity.Player;

import lib.securebit.game.JoinListener;

public class ServerModifier implements JoinListener {

	private Runnable onJoin;
	private Runnable onQuit;
	
	public ServerModifier(Runnable onJoin, Runnable onQuit) {
		this.onJoin = onJoin;
		this.onQuit = onQuit;
	}
	
	@Override
	public void onLogin(Player player, String result) {
		
	}

	@Override
	public void onJoin(Player player) {
		this.onJoin.run();
	}

	@Override
	public void onQuit(Player player) {
		this.onQuit.run();
	}
	
}

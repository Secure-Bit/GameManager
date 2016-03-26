package lib.securebit.game.defaults.joinhandler;

import org.bukkit.entity.Player;

import lib.securebit.game.Game;
import lib.securebit.game.JoinListener;

public class PlayerReseter implements JoinListener {
	
	private Game<?> game;
	
	public PlayerReseter(Game<?> game) {
		this.game = game;
	}
	
	@Override
	public void onLogin(Player player, String result) {
		
	}
	
	@Override
	public void onJoin(Player player) {
		this.game.resetPlayer(player);
	}

	@Override
	public void onQuit(Player player) {
		
	}

}

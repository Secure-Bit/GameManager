package lib.securebit.game.defaults.joinhandler;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

import lib.securebit.game.JoinListener;

public class PlayerModifier implements JoinListener {
	
	private Consumer<Player> onJoin;
	private Consumer<Player> onQuit;
	
	public PlayerModifier(Consumer<Player> onJoin, Consumer<Player> onQuit) {
		this.onJoin = onJoin;
		this.onQuit = onQuit;
	}
	
	@Override
	public void onLogin(Player player, String result) {
		
	}

	@Override
	public void onJoin(Player player) {
		this.onJoin.accept(player);
	}

	@Override
	public void onQuit(Player player) {
		this.onQuit.accept(player);
	}

}

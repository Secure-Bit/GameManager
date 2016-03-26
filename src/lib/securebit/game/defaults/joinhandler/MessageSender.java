package lib.securebit.game.defaults.joinhandler;

import org.bukkit.entity.Player;

import lib.securebit.game.Game;
import lib.securebit.game.JoinListener;

public class MessageSender implements JoinListener {
	
	private String msgJoin;
	private String msgQuit;
	
	private Game<?> game;
	
	public MessageSender(Game<?> game, String msgJoin, String msgQuit) {
		this.game = game;
		
		this.msgJoin = msgJoin;
		this.msgQuit = msgQuit;
	}
	
	@Override
	public void onLogin(Player player, String result) {
		
	}

	@Override
	public void onJoin(Player player) {
		this.game.broadcastMessage(this.msgJoin.
				replace("${player}" , player.getDisplayName()).
				replace("${max}", Integer.toString(this.game.getSize())).
				replace("${current}", Integer.toString(this.game.getPlayers().size())));
	}

	@Override
	public void onQuit(Player player) {
		this.game.broadcastMessage(this.msgQuit.
				replace("${player}" , player.getDisplayName()).
				replace("${max}", Integer.toString(this.game.getSize())).
				replace("${current}", Integer.toString(this.game.getPlayers().size())));
	}

}

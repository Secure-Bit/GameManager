package lib.securebit.game;

import org.bukkit.entity.Player;

public interface JoinListener {
	
	public abstract void onLogin(Player player, String result);
	
	public abstract void onJoin(Player player);
	
	public abstract void onQuit(Player player);
	
}

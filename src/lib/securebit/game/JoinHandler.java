package lib.securebit.game;

import java.util.List;

import org.bukkit.entity.Player;

public interface JoinHandler {
	
	public abstract void registerListener(JoinListener listener);
	
	public abstract void onJoin(Player player);
	
	public abstract void onQuit(Player player);
	
	public abstract String onLogin(Player player);
	
	public abstract List<JoinListener> getListeners();
	
}

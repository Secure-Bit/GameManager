package lib.securebit.game.defaults.joinhandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import lib.securebit.game.Game;
import lib.securebit.game.GamePlayer;
import lib.securebit.game.JoinListener;
import lib.securebit.game.impl.CraftJoinHandler;

public class PremiumJoin extends CraftJoinHandler {
	
	private boolean premiumKick;
	private int premiumSlots;
	
	private String permPremium;
	private String permStaff;
	
	private String messageServerFull;
	private String messageKick;
	
	private Game<?> game;
	
	public PremiumJoin(Game<?> game, String permPremium, String permStaff) {
		this.game = game;
		this.permPremium = permPremium;
		this.permStaff = permStaff;
		this.premiumKick = true;
		this.premiumSlots = 0;
		
		this.messageServerFull = "§cThe server is full!";
		this.messageKick = "§cYou was kicked by a premium or staff member!";
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
		String result;
		
		if (this.canUserJoin()) {
			result = null;
		} else {
			if (this.game.getPlayers().size() < this.game.getSize()) {
				return null;
			} else {
				System.out.println("cannot join");
				
				if (this.kickPlayer(this.getPermissionLevel(player))) {
					result = null;
				} else {
					result = this.messageServerFull;
				}
			}
		}
		
		for (JoinListener listener : this.getListeners()) {
			listener.onLogin(player, result);
		}
		
		return result;
	}
	
	public void setMessageKick(String messageKick) {
		this.messageKick = messageKick;
	}
	
	public void setMessageServerFull(String messageServerFull) {
		this.messageServerFull = messageServerFull;
	}
	
	public void setPremiumKick(boolean premiumKick) {
		this.premiumKick = premiumKick;
	}
	
	public void setPremiumSlots(int premiumSlots) {
		this.premiumSlots = premiumSlots;
	}
	
	public boolean canUserJoin() {
		List<GamePlayer> players = this.game.getPlayers().stream().filter((player) -> 
				!(this.hasPermission(player.getHandle(), this.permPremium) || this.hasPermission(player.getHandle(), this.permStaff))
		).collect(Collectors.toList());
		
		return players.size() < (this.game.getSize() - this.premiumSlots);
	}
	
	public boolean canPremiumJoin() {
		if (this.game.getPlayers().size() < this.game.getSize()) {
			return true;
		} else {
			return this.premiumKick;
		}
	}
	
	public int getPermissionLevel(Player player) {
		int level = 0;
		
		if (this.hasPermission(player, this.permPremium)) {
			level = 1;
		}
		
		if (this.hasPermission(player, this.permStaff)) {
			level = 2;
		}
		
		return level;
	}
	
	protected boolean hasPermission(Player player, String permission) {
		return player.hasPermission(permission);
	}
	
	private boolean kickPlayer(int levelPermitted) {
		List<Player> kickable = new ArrayList<>();
		
		for (GamePlayer target : this.game.getPlayers()) {
			int levelVictim = this.getPermissionLevel(target.getHandle());
			
			if (levelPermitted > levelVictim) {
				kickable.add(target.getHandle());
			}
		}
		
		Collections.shuffle(kickable);
		
		if (kickable.size() == 0) {
			return false;
		}
		
		this.game.kickPlayer(kickable.get(0), this.messageKick);
		return true;
	}

}

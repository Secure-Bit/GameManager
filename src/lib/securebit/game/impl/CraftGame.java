package lib.securebit.game.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import lib.securebit.InfoLayout;
import lib.securebit.game.Game;
import lib.securebit.game.GamePlayer;
import lib.securebit.game.GameStateManager;
import lib.securebit.game.GameStateManager.GameStateException;
import lib.securebit.game.events.PlayerGameJoinEvent;
import lib.securebit.game.events.PlayerGameQuitEvent;
import lib.securebit.game.mapreset.MapReset;
import lib.securebit.game.mapreset.SimpleMapReset;
import lib.securebit.game.util.PingResult;

public abstract class CraftGame<P extends GamePlayer> implements Game<P> {
	
	private List<P> players;
	private List<World> worlds;
	
	private MapReset mapReset;
	
	private Plugin plugin;
	
	private GameStateManager<?> manager;
	
	private boolean muted;
	private boolean debug;
	
	private String name;
	
	public CraftGame(Plugin plugin, String name) {
		this.plugin = plugin;
		this.name = name;
		
		this.manager = null;
		this.players = new ArrayList<>();
		this.worlds = new ArrayList<>();
		
		this.mapReset = new SimpleMapReset();
	}
	
	@Override
	public List<P> getPlayers() {
		return this.players;
	}

	@Override
	public List<World> getWorlds() {
		return this.worlds;
	}

	@Override
	public String loginPlayer(Player player) {
		if (this.manager.isCreated()) {
			return ((CraftGameState<?>) this.manager.getCurrent()).getJoinHandler().onLogin(player);
		} else {
			throw new GameStateException("The manager has to be created!");
		}
	}
	
	@Override
	public void mute(boolean mute) {
		this.muted = mute;
	}
	
	@Override
	public void setDebugMode(boolean mode) {
		this.debug = mode;
	}
	
	@Override
	public void playConsoleMessage(String msg) {
		if (!this.isMuted()) {
			if (msg.matches("^.+\\[.+\\] .+")) {
				msg = msg.replaceFirst("^.+\\[.+\\] ", "");
			}
			
			Bukkit.getConsoleSender().sendMessage(this.name + ": " + msg);
		}
	}
	
	@Override
	public void playConsoleDebugMessage(String msg, InfoLayout layout) {
		if (this.debug && !this.muted) {
			Bukkit.getConsoleSender().sendMessage(layout.format(this.name + ": " + layout.colorPrimary + "{" + layout.colorSecondary + msg + layout.colorPrimary + "}"));
		}
	}
	
	@Override
	public void joinPlayer(P player) {
		this.players.add(player);
		
		Bukkit.getPluginManager().callEvent(new PlayerGameJoinEvent(player.getHandle(), this));
		
		if (this.manager.isCreated()) {
			((CraftGameState<?>) this.manager.getCurrent()).getJoinHandler().onJoin(player.getHandle());
		}
		
		Player handle = player.getHandle();
		
		for (Player target : Bukkit.getOnlinePlayers()) {
			handle.hidePlayer(target);
			target.hidePlayer(handle);
		}
		
		for (GamePlayer target : this.getPlayers()) {
			handle.showPlayer(target.getHandle());
			target.getHandle().showPlayer(handle);
		}
	}

	@Override
	public void quitPlayer(Player player) {
		Bukkit.getPluginManager().callEvent(new PlayerGameQuitEvent(player, this));
		
		this.resetPlayer(player);
		
		if (this.manager.isCreated()) {
			((CraftGameState<?>) this.manager.getCurrent()).getJoinHandler().onQuit(player);
		}
		
		Iterator<P> iterator = this.players.iterator();
		
		while (iterator.hasNext()) {
			if (iterator.next().getHandle().getName().equals(player.getName())) {
				iterator.remove();
			}
		}
		
		for (Player target : Bukkit.getOnlinePlayers()) {
			player.showPlayer(target);
			target.showPlayer(player);
		}
		
		for (GamePlayer target : this.getPlayers()) {
			player.hidePlayer(target.getHandle());
			target.getHandle().hidePlayer(player);
		}
	}
	
	@Override
	public void broadcastMessage(String msg) {
		if (msg == null) {
			return;
		}
		
		this.playConsoleMessage(msg);
		
		for (P player : this.players) {
			player.getHandle().sendMessage(msg);
		}
	}
	
	@Override
	public void resetPlayer(Player player) {
		player.setGameMode(GameMode.SURVIVAL);
		player.setHealth(20.0);
		player.setVelocity(new Vector(0, 0, 0));
		player.setFoodLevel(20);
		player.setExp(0.0F);
		player.setLevel(0);
		player.setFireTicks(0);
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[] { null, null, null, null });
		
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
	}

	@Override
	public void registerWorld(World world) {
		this.worlds.add(world);
		this.mapReset.add(world);
	}

	@Override
	public void unregisterWorld(World world) {
		this.worlds.remove(world);
		this.mapReset.remove(world);
	}

	@Override
	public void unregisterWorld(String world) {
		Iterator<World> iterator = this.worlds.iterator();
		
		while (iterator.hasNext()) {
			if (iterator.next().getName().equals(world)) {
				iterator.remove();
			}
		}
	}
	
	@Override
	public boolean isMuted() {
		return this.muted;
	}
	
	@Override
	public boolean isDebugMode() {
		return this.debug;
	}

	@Override
	public boolean containsWorld(World world) {
		return this.worlds.contains(world);
	}

	@Override
	public boolean containsWorld(String world) {
		Iterator<World> iterator = this.worlds.iterator();
		
		while (iterator.hasNext()) {
			if (iterator.next().getName().equals(world)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean containsPlayer(Player player) {
		Iterator<P> iterator = this.players.iterator();
		
		while (iterator.hasNext()) {
			if (iterator.next().getHandle().getName().equals(player.getName())) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}
	
	@Override
	public P getPlayer(Player player) {
		for (P target : this.players) {
			if (target.getHandle().equals(player)) {
				return target;
			}
		}
		
		return null;
	}
	
	@Override
	public GameStateManager<?> getManager() {
		return this.manager;
	}
	
	@Override
	public PingResult pingGame() {
		return new PingResult(this.manager.getCurrent().getMotD(), this.manager.getCurrent().getName(), this.players.size(), this.getSize());
	}
	
	public void setManager(GameStateManager<?> manager) {
		if (this.manager != null) {
			throw new GameStateException("The manager is already set!");
		}
		
		this.manager = manager;
	}
	
	public MapReset getMapReset() {
		return this.mapReset;
	}
}

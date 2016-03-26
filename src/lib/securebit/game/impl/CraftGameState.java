package lib.securebit.game.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import lib.securebit.game.Game;
import lib.securebit.game.GamePlayer;
import lib.securebit.game.GameState;
import lib.securebit.game.JoinHandler;
import lib.securebit.game.Settings;
import lib.securebit.game.Settings.StateSettings;

public abstract class CraftGameState<G extends Game<? extends GamePlayer>> implements GameState {
	
	private List<Listener> listeners;
	
	private JoinHandler joinHandler;
	
	private Settings settings;
	
	private G game;
	
	public CraftGameState(G game) {
		this.listeners = new ArrayList<>();
		this.settings = new CraftSettings();
		this.game = game;
		
		this.getSettings().setValue(StateSettings.BLOCK_BREAK, Arrays.asList());
		this.getSettings().setValue(StateSettings.BLOCK_PLACE, Arrays.asList());
		this.getSettings().setValue(StateSettings.DAY_CYCLE, false);
		this.getSettings().setValue(StateSettings.ITEM_DROP, false);
		this.getSettings().setValue(StateSettings.ITEM_PICKUP, false);
		this.getSettings().setValue(StateSettings.ITEM_MOVE, false);
		this.getSettings().setValue(StateSettings.PLAYER_DAMAGE_FALL, false);
		this.getSettings().setValue(StateSettings.PLAYER_DAMAGE_FIGHT, false);
		this.getSettings().setValue(StateSettings.PLAYER_DAMAGE_NATURAL, false);
		this.getSettings().setValue(StateSettings.PLAYER_FOODLEVEL_CHANGE, false);
		this.getSettings().setValue(StateSettings.TIME, 0);
		this.getSettings().setValue(StateSettings.WEATHER, 0);
		this.getSettings().setValue(StateSettings.DIFFICULTY, Difficulty.PEACEFUL);
		this.getSettings().setValue(StateSettings.FIRE_SPREAD, false);
		this.getSettings().setValue(StateSettings.MAP_RESET, false);
	}
	
	@Override
	public void load() {
		this.game.getWorlds().forEach(world -> {
			world.setStorm((this.settings.getValue(StateSettings.WEATHER) & 0x01) != 0);
			world.setThundering((this.settings.getValue(StateSettings.WEATHER) & 0x02) != 0);
			world.setFullTime((long) this.settings.getValue(StateSettings.TIME));
			world.setDifficulty(this.settings.getValue(StateSettings.DIFFICULTY));
			
			if (!this.settings.getValue(StateSettings.DAY_CYCLE)) {
				world.setGameRuleValue("doDaylightCycle", "false");
			}
		});
		
		if (this.getSettings().getValue(StateSettings.MAP_RESET)) {
			this.game.getMapReset().startRecord();
		}
	}
	
	@Override
	public void unload() {
		this.game.getWorlds().forEach(world -> {
			world.setGameRuleValue("doDaylightCycle", "true");
		});
		
		
		if (this.getSettings().getValue(StateSettings.MAP_RESET)) {
			this.game.getMapReset().stopRecord();
		}
	}
	
	@Override
	public final void registerListener(Plugin plugin) {
		this.listeners.forEach((listener) -> {
			Bukkit.getPluginManager().registerEvents(listener, plugin);
		});
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public final void unregisterListener(Plugin plugin) {
		this.listeners.forEach((listener) -> {
			HandlerList.unregisterAll(listener);
		});
		
		HandlerList.unregisterAll(this);
	}
	
	@Override
	public final void setJoinHandler(JoinHandler handler) {
		this.joinHandler = handler;
	}
	
	@Override
	public final List<Listener> getListeners() {
		return this.listeners;
	}
	
	@Override
	public final JoinHandler getJoinHandler() {
		return this.joinHandler;
	}
	
	@Override
	public Settings getSettings() {
		return this.settings;
	}
	
	public G getGame() {
		return this.game;
	}
	
	protected void teleportPlayer(Player player) {
		
	}
	
	protected void onBlockPlace(Block block, Player player, boolean allowed) {
		
	}
	
	protected void onBlockBreak(Block block, Player player, boolean allowed) {
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public final void onFoodLevelChange(FoodLevelChangeEvent event) {
		this.game.getPlayers().forEach((player) -> {
			if (player.getHandle().equals(event.getEntity())) {
				event.setCancelled(!this.settings.getValue(StateSettings.PLAYER_FOODLEVEL_CHANGE));
				return;
			}
		});
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public final void onBlockPlace(BlockPlaceEvent event) {
		this.game.getPlayers().forEach((player) -> {
			if (player.getHandle().equals(event.getPlayer())) {
				List<Material> list = this.settings.getValue(StateSettings.BLOCK_PLACE);
				
				if (!list.contains(event.getBlock().getType())) {
					event.setCancelled(true);
					return;
				}
				
				this.onBlockPlace(event.getBlock(), player.getHandle(), !event.isCancelled());
			}
		});
		
		if (!event.isCancelled() && this.getSettings().getValue(StateSettings.MAP_RESET)) {
			this.game.getMapReset().placeBlock(event.getBlock().getLocation());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public final void onBlockBreak(BlockBreakEvent event) {
		this.game.getPlayers().forEach((player) -> {
			if (player.getHandle().equals(event.getPlayer())) {
				List<Material> list = this.settings.getValue(StateSettings.BLOCK_BREAK);
				
				if (!list.contains(event.getBlock().getType())) {
					event.setCancelled(true);
					return;
				}
				
				this.onBlockBreak(event.getBlock(), player.getHandle(), !event.isCancelled());
			}
		});
		
		if (!event.isCancelled() && this.getSettings().getValue(StateSettings.MAP_RESET)) {
			this.game.getMapReset().breakBlock(event.getBlock().getLocation(), event.getBlock());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public final void onWeatherChange(WeatherChangeEvent event) {
		this.game.getWorlds().forEach((world) -> {
			if (world.equals(event.getWorld())) {
				event.setCancelled(true);
				return;
			}
		});
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public final void onThunderChange(ThunderChangeEvent event) {
		this.game.getWorlds().forEach((world) -> {
			if (world.equals(event.getWorld())) {
				event.setCancelled(true);
				return;
			}
		});
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public final void onItemDrop(PlayerDropItemEvent event) {
		this.game.getPlayers().forEach((player) -> {
			if (player.getHandle().equals(event.getPlayer())) {
				event.setCancelled(!this.settings.getValue(StateSettings.ITEM_DROP));
				return;
			}
		});
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public final void onItemPickup(PlayerPickupItemEvent event) {
		this.game.getPlayers().forEach((player) -> {
			if (player.getHandle().equals(event.getPlayer())) {
				event.setCancelled(!this.settings.getValue(StateSettings.ITEM_PICKUP));
				return;
			}
		});
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public final void onDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			this.game.getPlayers().forEach((player) -> {
				if (player.getHandle().equals(event.getEntity())) {
					event.setCancelled(!this.settings.getValue(StateSettings.PLAYER_DAMAGE_FIGHT));
					return;
				}
			});
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public final void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			this.game.getPlayers().forEach((player) -> {
				if (player.getHandle().equals(event.getEntity())) {
					if (event.getCause() == DamageCause.FALL) {
						event.setCancelled(!this.settings.getValue(StateSettings.PLAYER_DAMAGE_FALL));
						return;
					} else if (event.getCause() != DamageCause.ENTITY_ATTACK) {
						if (!this.settings.getValue(StateSettings.PLAYER_DAMAGE_NATURAL)) {
							event.setCancelled(true);
							
							if (event.getCause() == DamageCause.POISON) {
								player.getHandle().removePotionEffect(PotionEffectType.POISON);
							} else if (event.getCause() == DamageCause.WITHER) {
								player.getHandle().removePotionEffect(PotionEffectType.WITHER);
							} else if (event.getCause() == DamageCause.VOID ||
									event.getCause() == DamageCause.SUFFOCATION) {
								this.teleportPlayer(player.getHandle());
							} else if (event.getCause() == DamageCause.FIRE ||
									event.getCause() == DamageCause.FIRE_TICK) {
								player.getHandle().setFireTicks(0);
							}
						}
						
						return;
					}
				}
			});
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockIgnite(BlockIgniteEvent event) {
		this.game.getWorlds().forEach((world) -> {
			if (world.equals(event.getBlock().getWorld())) {
				if (event.getCause() == IgniteCause.SPREAD) {
					event.setCancelled(!this.settings.getValue(StateSettings.FIRE_SPREAD));
					return;
				}
			}
		});
	}
	
}

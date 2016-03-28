package lib.securebit.game.defaults.states;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import lib.securebit.countdown.Countdown;
import lib.securebit.countdown.DefaultCountdown;
import lib.securebit.countdown.TimeListener;
import lib.securebit.game.Game;
import lib.securebit.game.GamePlayer;
import lib.securebit.game.defaults.joinhandler.PlayerModifier;
import lib.securebit.game.defaults.joinhandler.PlayerReseter;
import lib.securebit.game.defaults.joinhandler.PremiumJoin;
import lib.securebit.game.defaults.joinhandler.ServerModifier;
import lib.securebit.game.impl.CraftGameStateLobby;
import lib.securebit.game.util.HotbarItem;

public abstract class DefaultGameStateLobby<G extends Game<? extends GamePlayer>> extends CraftGameStateLobby<G> {
	
	private String permPremium;
	private String permStaff;
	
	private int minPl;
	private int premiumSlots;
	
	private boolean premiumKick;
	
	private Countdown countdown;
	
	private List<HotbarItem> items;
	
	public DefaultGameStateLobby(G game, Location lobby, String permPremium,
			String permStaff, int maxPl, int minPl, int countdownLength, int premiumSlots, boolean premiumKick) {
		super(game, lobby);
		
		this.items = new ArrayList<>();
		
		this.minPl = minPl;
		this.premiumSlots = premiumSlots;
		this.permPremium = permPremium;
		this.permStaff = permStaff;
		this.premiumKick = premiumKick;
		
		this.countdown = new DefaultCountdown(this.getGame().getPlugin(), countdownLength) {
			
			@Override
			public void onAnnounceTime(int secondsLeft) {
				String msg = DefaultGameStateLobby.this.getMessageCountdown(secondsLeft);
				
				if (msg != null) {
					DefaultGameStateLobby.this.getGame().broadcastMessage(msg);
				}
			}
			
			@Override
			public void onStop(int secondsSkipped) {
				DefaultGameStateLobby.this.onCountdownStop();
			}
			
			@Override
			public boolean isAnnounceTime(int secondsLeft) {
				return DefaultGameStateLobby.this.isCountdownAnnounceTime(secondsLeft);
			}
		};
		
		PremiumJoin handler = new PremiumJoin(this.getGame(), this.permPremium, this.permStaff);
		handler.setMessageKick(this.getKickMessage());
		handler.setMessageServerFull(this.getMessageServerFull());
		handler.setPremiumKick(this.premiumKick);
		handler.setPremiumSlots(this.premiumSlots);
		handler.registerListener(new PlayerReseter(this.getGame()));
		handler.registerListener(new PlayerModifier((player) -> {
			player.teleport(this.getLobby());
			
			for (HotbarItem item : this.items) {
				player.getInventory().setItem(item.getSlot(), item.getHandle());
			}
		}, (player) -> {}));
		handler.registerListener(new ServerModifier(() -> {
			if (this.minPl <= this.getGame().getPlayers().size()) {
				if (!this.countdown.isRunning()) {
					this.countdown.start();
				}
			}
		}, () -> {}));
		
		this.setJoinHandler(handler);
	}
	
	@Override
	public void start() {
		
	}

	@Override
	public void stop() {
		if (this.countdown.isRunning()) {
			this.countdown.stop(false);
		}
	}
	
	@Override
	public String getName() {
		return "lobby";
	}
	
	@Override
	public void load() {
		super.load();
		
		for (GamePlayer player : this.getGame().getPlayers()) {
			this.getGame().resetPlayer(player.getHandle());
			player.getHandle().teleport(this.getLobby());
		}
	}
	
	public Countdown getCountdown() {
		return this.countdown;
	}
	
	protected boolean isCountdownAnnounceTime(int secondsLeft) {
		return TimeListener.defaultAnnounceTime(secondsLeft);
	}
	
	protected void onCountdownStop() {
		if (this.minPl > this.getGame().getPlayers().size()) {
			this.getGame().broadcastMessage(this.getMessageCountdownCancle());
		} else {
			this.getGame().getManager().next();
		}
	}
	
	protected abstract String getKickMessage();
	
	protected abstract String getMessageServerFull();
	
	protected abstract String getMessageNotJoinable();

	protected abstract String getMessageCountdown(int secondsLeft);
	
	protected abstract String getMessageCountdownCancle();
	
	@EventHandler
	public final void onInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			this.getGame().getPlayers().forEach((gameplayer) -> {
				Player player = event.getPlayer();
				
				if (player.equals(gameplayer.getHandle())) {
					for (HotbarItem item : this.items) {
						if (item.getSlot() == player.getInventory().getHeldItemSlot()) {
							event.setCancelled(true);
							item.getAction().accept(player);
							return;
						}
					}
					
					return;
				}
			});
		}
	}
	
}
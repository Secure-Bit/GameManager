package lib.securebit.game.defaults.states;

import org.bukkit.Location;

import lib.securebit.InfoLayout;
import lib.securebit.countdown.Countdown;
import lib.securebit.countdown.DefaultCountdown;
import lib.securebit.countdown.TimeListener;
import lib.securebit.game.Game;
import lib.securebit.game.GamePlayer;
import lib.securebit.game.defaults.joinhandler.PreventedJoin;
import lib.securebit.game.impl.CraftGameStateLobby;

public abstract class DefaultGameStateEnd<G extends Game<? extends GamePlayer>> extends CraftGameStateLobby<G> {
	
	private InfoLayout layout;
	private Countdown countdown;
	
	public DefaultGameStateEnd(G game, Location lobby, int countdownLength, InfoLayout layout) {
		super(game, lobby);
		
		this.layout = layout;
		this.countdown = new DefaultCountdown(this.getGame().getPlugin(), countdownLength) {
			
			@Override
			public void onAnnounceTime(int secondsLeft) {
				String msg = DefaultGameStateEnd.this.getMessageCountdown(secondsLeft);
				
				if (msg != null) {
					DefaultGameStateEnd.this.getGame().broadcastMessage(msg);
				}
			}
			
			@Override
			public void onStop(int secondsSkipped) {
				DefaultGameStateEnd.this.onCountdownStop();
			}
			
			@Override
			public boolean isAnnounceTime(int secondsLeft) {
				return DefaultGameStateEnd.this.isCountdownAnnounceTime(secondsLeft);
			}
		};
		
		this.setJoinHandler(new PreventedJoin(this.getMessageNotJoinable()));
	}
	
	@Override
	public void load() {
		super.load();
		
		for (GamePlayer player : this.getGame().getPlayers()) {
			this.getGame().resetPlayer(player.getHandle());
			player.getHandle().teleport(this.getLobby());
		}
	}
	
	@Override
	public void unload() {
		int result = this.getGame().getMapReset().rollback();
		
		this.getGame().playConsoleDebugMessage("MapReset complete, " + result + " blocks restored.", this.layout);
	}
	
	@Override
	public void start() {
		this.countdown.start();
	}

	@Override
	public void stop() {
		if (this.countdown.isRunning()) {
			this.countdown.stop(false);
		}
	}
	
	@Override
	public String getName() {
		return "end";
	}
	
	public Countdown getCountdown() {
		return this.countdown;
	}
	
	protected boolean isCountdownAnnounceTime(int secondsLeft) {
		return TimeListener.defaultAnnounceTime(secondsLeft);
	}
	
	protected void onCountdownStop() {
		this.getGame().getManager().destroy();
	}
	
	protected abstract String getMessageNotJoinable();
	
	protected abstract String getMessageCountdown(int secondsLeft);

}

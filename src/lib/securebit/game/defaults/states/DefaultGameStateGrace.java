package lib.securebit.game.defaults.states;

import lib.securebit.countdown.Countdown;
import lib.securebit.countdown.DefaultCountdown;
import lib.securebit.countdown.TimeListener;
import lib.securebit.game.Game;
import lib.securebit.game.GamePlayer;
import lib.securebit.game.Settings.StateSettings;
import lib.securebit.game.impl.CraftGameStateArena;

public abstract class DefaultGameStateGrace<G extends Game<? extends GamePlayer>> extends CraftGameStateArena<G> {

	private Countdown countdown;
	
	public DefaultGameStateGrace(G game, int countdownLength) {
		super(game);
		
		this.countdown = new DefaultCountdown(this.getGame().getPlugin(), countdownLength) {
			
			@Override
			public void onAnnounceTime(int secondsLeft) {
				String msg = DefaultGameStateGrace.this.getMessageCountdown(secondsLeft);
				
				if (msg != null) {
					DefaultGameStateGrace.this.getGame().broadcastMessage(msg);
				}
			}
			
			@Override
			public void onStop(int secondsSkipped) {
				DefaultGameStateGrace.this.getGame().getManager().next();
			}
			
			@Override
			public boolean isAnnounceTime(int secondsLeft) {
				return DefaultGameStateGrace.this.isCountdownAnnounceTime(secondsLeft);
			}
			
		};
		
		this.getSettings().setValue(StateSettings.PLAYER_DAMAGE_FIGHT, false);
		this.getSettings().setValue(StateSettings.PLAYER_DAMAGE_NATURAL, false);
		this.getSettings().setValue(StateSettings.PLAYER_FOODLEVEL_CHANGE, false);
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
		return "grace";
	}
	
	public Countdown getCountdown() {
		return this.countdown;
	}
	
	protected boolean isCountdownAnnounceTime(int secondsLeft) {
		return TimeListener.defaultAnnounceTime(secondsLeft);
	}
	
	protected abstract String getMessageCountdown(int secondsleft);

}

package lib.securebit.game.defaults.states;

import lib.securebit.game.Game;
import lib.securebit.game.GamePlayer;
import lib.securebit.game.Settings.StateSettings;
import lib.securebit.game.impl.CraftGameStateArena;

public abstract class DefaultGameStateIngame<G extends Game<? extends GamePlayer>> extends CraftGameStateArena<G> {

	public DefaultGameStateIngame(G game) {
		super(game);
		
		this.getSettings().setValue(StateSettings.PLAYER_DAMAGE_FIGHT, true);
		this.getSettings().setValue(StateSettings.PLAYER_DAMAGE_NATURAL, true);
		this.getSettings().setValue(StateSettings.PLAYER_FOODLEVEL_CHANGE, true);
	}
	
	@Override
	public String getName() {
		return "ingame";
	}
	
	@Override
	public void start() {
		
	}
	
	@Override
	public void stop() {
		
	}
	
}

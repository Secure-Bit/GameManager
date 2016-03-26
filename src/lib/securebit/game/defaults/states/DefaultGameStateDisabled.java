package lib.securebit.game.defaults.states;

import java.util.Arrays;

import org.bukkit.GameMode;
import org.bukkit.Material;

import lib.securebit.game.Game;
import lib.securebit.game.GamePlayer;
import lib.securebit.game.JoinHandler;
import lib.securebit.game.Settings.StateSettings;
import lib.securebit.game.defaults.joinhandler.PermitedJoin;
import lib.securebit.game.defaults.joinhandler.PlayerModifier;
import lib.securebit.game.impl.CraftGameState;

public abstract class DefaultGameStateDisabled<G extends Game<? extends GamePlayer>> extends CraftGameState<G> {

	public DefaultGameStateDisabled(G game) {
		super(game);
		this.getSettings().setValue(StateSettings.BLOCK_BREAK, Arrays.asList(Material.values()));
		this.getSettings().setValue(StateSettings.BLOCK_PLACE, Arrays.asList(Material.values()));
		this.getSettings().setValue(StateSettings.DAY_CYCLE, true);
		this.getSettings().setValue(StateSettings.ITEM_DROP, true);
		this.getSettings().setValue(StateSettings.ITEM_MOVE, true);
		this.getSettings().setValue(StateSettings.ITEM_PICKUP, true);
		this.getSettings().setValue(StateSettings.PLAYER_DAMAGE_FALL, true);
		this.getSettings().setValue(StateSettings.PLAYER_DAMAGE_FIGHT, true);
		this.getSettings().setValue(StateSettings.PLAYER_DAMAGE_NATURAL, true);
		this.getSettings().setValue(StateSettings.PLAYER_FOODLEVEL_CHANGE, true);
		this.getSettings().setValue(StateSettings.TIME, 0);
		this.getSettings().setValue(StateSettings.WEATHER, 0);
		
		JoinHandler handler = new PermitedJoin(this.getStaffPermission(), this.getMaintenanceKickMessage());
		handler.registerListener(new PlayerModifier((player) -> {
			player.setGameMode(GameMode.CREATIVE);
			player.sendMessage(this.getMaintenanceAdminMessage());
		}, (player) -> {}));
		
		this.setJoinHandler(handler);
	}

	@Override
	public void start() {
		this.getGame().getPlayers().forEach((player) -> {
			player.getHandle().setGameMode(GameMode.CREATIVE);
		});
	}

	@Override
	public void stop() {
		
	}
	
	@Override
	public String getName() {
		return "edit";
	}
	
	protected abstract String getStaffPermission();

	protected abstract String getMaintenanceKickMessage();
	
	protected abstract String getMaintenanceAdminMessage();
}

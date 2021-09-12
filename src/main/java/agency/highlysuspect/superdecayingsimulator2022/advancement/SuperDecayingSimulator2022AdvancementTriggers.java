package agency.highlysuspect.superdecayingsimulator2022.advancement;

import agency.highlysuspect.superdecayingsimulator2022.stats.ManaStatsWsd;
import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022Config;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class SuperDecayingSimulator2022AdvancementTriggers {
	public static final GeneratedManaTrigger GENERATED_MANA = new GeneratedManaTrigger();
	
	private static ForgeConfigSpec.IntValue checkInterval;
	
	public static void onInitialize() {
		CriteriaTriggers.register(GENERATED_MANA);
		
		checkInterval = SuperDecayingSimulator2022Config.CONFIG.advancementCheckInterval;
		
		MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent e) -> {
			if(e.phase == TickEvent.Phase.START) {
				//Leave it to Forge to not provide the ticking server in question, in the ServerTickEvent
				MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
				
				ServerWorld overworld = server.getWorld(World.OVERWORLD);
				assert overworld != null; //Sure hope so!
				if(overworld.getGameTime() % checkInterval.get() != 0) return;
				
				ManaStatsWsd wsd = ManaStatsWsd.getFor(overworld);
				
				for(ServerPlayerEntity player : server.getPlayerList().getPlayers()) {
					GENERATED_MANA.tryTrigger(player, wsd);
				}
			}
		});
	}
}

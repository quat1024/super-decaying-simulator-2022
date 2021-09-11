package agency.highlysuspect.superdecayingsimulator2022.client;

import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022NetworkHandler;
import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.botania.common.lib.LibMisc;

public class SuperDecayingSimulator2022ClientProxy implements SuperDecayingSimulator2022Proxy {
	public static final KeyBinding OPEN_STATS_GUI = new KeyBinding(
		"key.super-decaying-simulator-2022.open-stats-gui",
		KeyConflictContext.IN_GAME,
		InputMappings.INPUT_INVALID, //Unbound by default
		LibMisc.MOD_NAME
	);
	
	@Override
	public void initalize() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent e) ->
			ClientRegistry.registerKeyBinding(OPEN_STATS_GUI));
		
		//Can someone with more experience wht forge keybindings help me out here lol
		MinecraftForge.EVENT_BUS.addListener((InputEvent.KeyInputEvent e) -> {
			Minecraft mc = Minecraft.getInstance();
			if(mc.getConnection() == null) return;
			
			if(mc.currentScreen == null && OPEN_STATS_GUI.isPressed()) {
				mc.displayGuiScreen(new ManaStatsGui(null));
				//Tell the server that I just opened the GUI, so it'll send me a status update asap.
				SuperDecayingSimulator2022NetworkHandler.CHANNEL.sendToServer(
					new SuperDecayingSimulator2022NetworkHandler.C2SSetGuiStatus(true));
			}
		});
	}
}

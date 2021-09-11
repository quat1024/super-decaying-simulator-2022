package agency.highlysuspect.superdecayingsimulator2022;

import agency.highlysuspect.superdecayingsimulator2022.advancement.SuperDecayingSimulator2022AdvancementTriggers;
import agency.highlysuspect.superdecayingsimulator2022.client.SuperDecayingSimulator2022ClientProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import vazkii.botania.common.block.ModSubtiles;

import java.util.function.Consumer;

@Mod(SuperDecayingSimulator2022.MODID)
public class SuperDecayingSimulator2022 {
	public static final String MODID = "super-decaying-simulator-2022";
	
	//god i fucking hate proxies
	public static final SuperDecayingSimulator2022Proxy PROXY = DistExecutor.safeRunForDist(
		() -> SuperDecayingSimulator2022ClientProxy::new,
		() -> SuperDecayingSimulator2022Proxy.Server::new);
	
	public SuperDecayingSimulator2022() {
		registerGeneratingFlowers(GeneratingFlowerType::register);
		GeneratingFlowerType.allDoneRegistering();
		
		//Can't do this in common setup because the config just.. doesnt.... show up???
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SuperDecayingSimulator2022Config.buildSpecAndSetInstance());
		
		MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> SuperDecayingSimulator2022Commands.register(e.getDispatcher()));
		SuperDecayingSimulator2022NetworkHandler.onInitialize();
		SuperDecayingSimulator2022AdvancementTriggers.onInitialize();
		PROXY.initalize();
	}
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static void registerGeneratingFlowers(Consumer<GeneratingFlowerType> c) {
		c.accept(new GeneratingFlowerType("rosa_arcana", ModSubtiles.rosaArcana, ModSubtiles.ROSA_ARCANA));
		c.accept(new GeneratingFlowerType("dandelifeon", ModSubtiles.dandelifeon, ModSubtiles.DANDELIFEON));
		c.accept(new GeneratingFlowerType("endoflame", ModSubtiles.endoflame, ModSubtiles.ENDOFLAME));
		c.accept(new GeneratingFlowerType("entropinnyum", ModSubtiles.entropinnyum, ModSubtiles.ENTROPINNYUM));
		c.accept(new GeneratingFlowerType("gourmaryllis", ModSubtiles.gourmaryllis, ModSubtiles.GOURMARYLLIS));
		c.accept(new GeneratingFlowerType("hydroangeas", ModSubtiles.hydroangeas, ModSubtiles.HYDROANGEAS).passive());
		c.accept(new GeneratingFlowerType("kekimurus", ModSubtiles.kekimurus, ModSubtiles.KEKIMURUS));
		c.accept(new GeneratingFlowerType("munchdew", ModSubtiles.munchdew, ModSubtiles.MUNCHDEW));
		c.accept(new GeneratingFlowerType("narslimmus", ModSubtiles.narslimmus, ModSubtiles.NARSLIMMUS));
		c.accept(new GeneratingFlowerType("rafflowsia", ModSubtiles.rafflowsia, ModSubtiles.RAFFLOWSIA));
		c.accept(new GeneratingFlowerType("shulk_me_not", ModSubtiles.shulkMeNot, ModSubtiles.SHULK_ME_NOT));
		c.accept(new GeneratingFlowerType("spectrolus", ModSubtiles.spectrolus, ModSubtiles.SPECTROLUS));
		c.accept(new GeneratingFlowerType("thermalily", ModSubtiles.thermalily, ModSubtiles.THERMALILY));
	}
}

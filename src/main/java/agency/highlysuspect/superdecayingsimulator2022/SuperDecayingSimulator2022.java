package agency.highlysuspect.superdecayingsimulator2022;

import agency.highlysuspect.superdecayingsimulator2022.advancement.SuperDecayingSimulator2022AdvancementTriggers;
import agency.highlysuspect.superdecayingsimulator2022.client.SuperDecayingSimulator2022ClientProxy;
import agency.highlysuspect.superdecayingsimulator2022.stats.SuperDecayingSimulator2022NetworkHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
		
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(GlobalLootModifierSerializer.class, (RegistryEvent.Register<GlobalLootModifierSerializer<?>> e) -> {
			e.getRegistry().register(new PassiveDropGlobalLootModifier.Serializer().setRegistryName(id("passive_drop_modifier")));
		});
		
		PROXY.initalize();
	}
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static void registerGeneratingFlowers(Consumer<GeneratingFlowerType> c) {
		c.accept(new GeneratingFlowerType("rosa_arcana")
			.blocks(ModSubtiles.rosaArcana, ModSubtiles.rosaArcanaFloating)
			.tileTypes(ModSubtiles.ROSA_ARCANA));
		
		c.accept(new GeneratingFlowerType("dandelifeon")
			.blocks(ModSubtiles.dandelifeon, ModSubtiles.dandelifeonFloating)
			.tileTypes(ModSubtiles.DANDELIFEON));
		
		c.accept(new GeneratingFlowerType("endoflame")
			.blocks(ModSubtiles.endoflame, ModSubtiles.endoflameFloating)
			.tileTypes(ModSubtiles.ENDOFLAME));
		
		c.accept(new GeneratingFlowerType("entropinnyum")
			.blocks(ModSubtiles.entropinnyum, ModSubtiles.entropinnyumFloating)
			.tileTypes(ModSubtiles.ENTROPINNYUM));
		
		c.accept(new GeneratingFlowerType("gourmaryllis")
			.blocks(ModSubtiles.gourmaryllis, ModSubtiles.gourmaryllisFloating)
			.tileTypes(ModSubtiles.GOURMARYLLIS));
		
		c.accept(new GeneratingFlowerType("hydroangeas")
			.blocks(ModSubtiles.hydroangeas, ModSubtiles.hydroangeasFloating)
			.tileTypes(ModSubtiles.HYDROANGEAS)
			.passive());
		
		c.accept(new GeneratingFlowerType("kekimurus")
			.blocks(ModSubtiles.kekimurus, ModSubtiles.kekimurusFloating)
			.tileTypes(ModSubtiles.KEKIMURUS));
		
		c.accept(new GeneratingFlowerType("munchdew")
			.blocks(ModSubtiles.munchdew, ModSubtiles.munchdewFloating)
			.tileTypes(ModSubtiles.MUNCHDEW));
		
		c.accept(new GeneratingFlowerType("narslimmus")
			.blocks(ModSubtiles.narslimmus, ModSubtiles.narslimmusFloating)
			.tileTypes(ModSubtiles.NARSLIMMUS));
		
		c.accept(new GeneratingFlowerType("rafflowsia")
			.blocks(ModSubtiles.rafflowsia, ModSubtiles.rafflowsiaFloating)
			.tileTypes(ModSubtiles.RAFFLOWSIA));
		
		c.accept(new GeneratingFlowerType("shulk_me_not")
			.blocks(ModSubtiles.shulkMeNot, ModSubtiles.shulkMeNotFloating)
			.tileTypes(ModSubtiles.SHULK_ME_NOT));
		
		c.accept(new GeneratingFlowerType("spectrolus")
			.blocks(ModSubtiles.spectrolus, ModSubtiles.spectrolusFloating)
			.tileTypes(ModSubtiles.SPECTROLUS));
		
		c.accept(new GeneratingFlowerType("thermalily")
			.blocks(ModSubtiles.thermalily, ModSubtiles.thermalilyFloating)
			.tileTypes(ModSubtiles.THERMALILY));
		
	}
}

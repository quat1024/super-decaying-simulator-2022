package agency.highlysuspect.superdecayingsimulator2022;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;
import vazkii.botania.common.block.ModSubtiles;

import java.util.Collections;
import java.util.function.Consumer;

@Mod(SuperDecayingSimulator2022.MODID)
public class SuperDecayingSimulator2022 {
	public static final String MODID = "super-decaying-simulator-2022";
	
	public SuperDecayingSimulator2022() {
		registerGeneratingFlowers(generatingFlowerType -> {
			GeneratingFlowerType.ALL_TYPES.add(generatingFlowerType);
			
			for(TileEntityType<? extends TileEntityGeneratingFlower> tileType : generatingFlowerType.types) {
				GeneratingFlowerType.TYPE_LOOKUP.put(tileType, generatingFlowerType);
			}
		});
		
		Collections.sort(GeneratingFlowerType.ALL_TYPES);
		
		//Can't do this in common setup because the config just.. doesnt.... show up???
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SuperDecayingSimulator2022Config.buildSpecAndSetInstance());
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
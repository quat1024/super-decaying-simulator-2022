package agency.highlysuspect.superdecayingsimulator2022;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.botania.api.BotaniaAPI;

import java.util.HashMap;
import java.util.Map;

public class SuperDecayingSimulator2022Config {
	public final Map<GeneratingFlowerType, ForgeConfigSpec.BooleanValue> passiveOverride = new HashMap<>();
	public final Map<GeneratingFlowerType, ForgeConfigSpec.IntValue> decayTimeOverride = new HashMap<>();
	
	public final ForgeConfigSpec.IntValue advancementCheckInterval;
	
	public static SuperDecayingSimulator2022Config CONFIG;
	public static ForgeConfigSpec buildSpecAndSetInstance() {
		Pair<SuperDecayingSimulator2022Config, ForgeConfigSpec> bepis = new ForgeConfigSpec.Builder().configure(SuperDecayingSimulator2022Config::new);
		CONFIG = bepis.getLeft();
		return bepis.getRight();
	}
	
	public SuperDecayingSimulator2022Config(ForgeConfigSpec.Builder builder) {
		builder.comment("If 'true', these flowers will experience passive decay.").push("Passivity");
		
		for(GeneratingFlowerType type : GeneratingFlowerType.allTypes()) {
			if(!type.passiveByDefault) {
				if(type == GeneratingFlowerType.UNKNOWN_FLOWER)
					builder.comment("Lumps together all unrecognized generating flowers from addons.", "I know this isn't the best thing in the world, sorry about that.");
				
				passiveOverride.put(type, builder.define(type.name, false));
			}
		}
		
		builder.pop();
		builder.comment("If they're passive, how many ticks does it take for each flower to decay?").push("DecayTime");
		
		int defaultDecayTime = BotaniaAPI.instance().getPassiveFlowerDecay();
		
		for(GeneratingFlowerType type : GeneratingFlowerType.allTypes()) {
			decayTimeOverride.put(type, builder
				.defineInRange(type.name, defaultDecayTime, 0, type.passiveByDefault ? defaultDecayTime : Integer.MAX_VALUE));
		}
		
		builder.pop();
		
		builder.push("Advancements");
		advancementCheckInterval = builder.comment("How often should the `super-decaying-simulator-2022:generated_mana` criterion be checked?")
			.defineInRange("check_interval", 100, 1, Integer.MAX_VALUE);
		builder.pop();
	}
}

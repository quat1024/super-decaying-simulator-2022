package agency.highlysuspect.superdecayingsimulator2022;

import agency.highlysuspect.superdecayingsimulator2022.mixin.ItemLootEntryAccessor;
import agency.highlysuspect.superdecayingsimulator2022.mixin.LootPoolMixin;
import agency.highlysuspect.superdecayingsimulator2022.mixin.LootTableAccessor;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class SuperDecayingSimulator2022LootHandler {
	public static LootConditionType IS_PASSIVE_OVERRIDE_ENABLED;
	
	public interface PoolExt {
		List<LootEntry> sds2022$entries();
		void sds2022$addFunction(ILootFunction func);
	}
	
	public static void onInitialize() {
		registerLootFunctionType();
	}
	
	public static void registerLootFunctionType() {
		//This will probably break with forge's multithreaded modloading gunk. Wee.
		//Is there anywhere i can enqueueWork or something
		IS_PASSIVE_OVERRIDE_ENABLED = Registry.register(Registry.LOOT_CONDITION_TYPE,
			SuperDecayingSimulator2022.id("is_passive_override_enabled"),
			new LootConditionType(new IsPassiveOverrideEnabled.Serializer())
		);
	}
	
	//Not using LootTableLoadEvent because `for some godforsaken reason` Forge does not fire the event for non-vanilla loot tables.
	//Yeah dude, I dont fucking know anymore. I hate this loader
	public static void modifyLootTable(LootTable table) {
		
		//Inspect the loot table in question, look for loot pools that yield generating flowers.
		boolean did = false;
		out: for(LootPool pool : ((LootTableAccessor) table).sds2022$pools()) {
			for(LootEntry entry : ((PoolExt) pool).sds2022$entries()) {
				if(entry instanceof ItemLootEntry) {
					Item item = ((ItemLootEntryAccessor) entry).sdss2022$item();
					GeneratingFlowerType type = GeneratingFlowerType.byItem(item);
					if(type != null) {
						//This loot table drops a generating flower.
						//Append a new loot function.
						((PoolExt) pool).sds2022$addFunction(CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
							.addOperation("passiveDecayTicks", "BlockEntityTag.passiveDecayTicks", CopyNbt.Action.REPLACE)
							.acceptCondition(() -> new IsPassiveOverrideEnabled(type))
							.build());
						did = true;
						break out;
					}
				}
			}
		}
		
		if(did) {
			LogManager.getLogger("kasjdksjd").info(LootSerializers.func_237388_c_().setPrettyPrinting().disableHtmlEscaping().create().toJson(table));
			
			System.out.println(table);
		}
	}
	
	public static class IsPassiveOverrideEnabled implements ILootCondition {
		public IsPassiveOverrideEnabled(GeneratingFlowerType type) {
			this.type = type;
			this.opt = SuperDecayingSimulator2022Config.CONFIG.passiveOverride.get(type);
		}
		
		private final GeneratingFlowerType type;
		private final ForgeConfigSpec.BooleanValue opt;
		
		@Override
		public LootConditionType func_230419_b_() {
			return IS_PASSIVE_OVERRIDE_ENABLED;
		}
		
		@Override
		public boolean test(LootContext lootContext) {
			return opt.get();
		}
		
		//Probably won't ever get used, but hey
		public static class Serializer implements ILootSerializer<IsPassiveOverrideEnabled> {
			@Override
			public void serialize(JsonObject json, IsPassiveOverrideEnabled condition, JsonSerializationContext ctx) {
				json.addProperty("flower", condition.type.name);
			}
			
			@Override
			public IsPassiveOverrideEnabled deserialize(JsonObject json, JsonDeserializationContext ctx) {
				String typeName = JSONUtils.getString(json, "flower");
				GeneratingFlowerType type = GeneratingFlowerType.byName(typeName);
				if(type == null)
					throw new JsonSyntaxException("No flower type named " + typeName + ". Available names are: " + String.join(", ", GeneratingFlowerType.allNames()));
				
				return new IsPassiveOverrideEnabled(type);
			}
		}
	}
}

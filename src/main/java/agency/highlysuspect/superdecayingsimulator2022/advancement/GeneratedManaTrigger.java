package agency.highlysuspect.superdecayingsimulator2022.advancement;

import agency.highlysuspect.superdecayingsimulator2022.GeneratingFlowerType;
import agency.highlysuspect.superdecayingsimulator2022.stats.ManaStatsWsd;
import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class GeneratedManaTrigger extends AbstractCriterionTrigger<GeneratedManaTrigger.Inst> {
	private static final ResourceLocation ID = SuperDecayingSimulator2022.id("generated_mana");
	
	@Override
	public ResourceLocation getId() {
		return ID;
	}
	
	@Override
	protected Inst deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
		GeneratingFlowerType type = null;
		
		//try to parse out the type, if it exists
		if(json.has("flower")) {
			String typeName = JSONUtils.getString(json, "flower");
			type = GeneratingFlowerType.byName(typeName);
			if(type == null)
				throw new JsonSyntaxException("No flower type named " + typeName + ". Available names are: " + String.join(", ", GeneratingFlowerType.allNames()));
		}
		
		//parse out the amount of mana
		long mana = 0;
		if(json.has("pools")) mana += JSONUtils.getLong(json, "pools") * 1_000_000;
		if(json.has("mana")) mana += JSONUtils.getLong(json, "mana");
		
		if(mana <= 0) throw new JsonSyntaxException("A positive, nonzero amount of mana is required");
		
		return new Inst(entityPredicate, type, mana);
	}
	
	public void tryTrigger(ServerPlayerEntity player, ManaStatsWsd stats) {
		triggerListeners(player, inst -> inst.test(stats));
	}
	
	public static class Inst extends CriterionInstance {
		public Inst(EntityPredicate.AndPredicate playerCondition, @Nullable GeneratingFlowerType type, long minMana) {
			super(ID, playerCondition);
			this.type = type;
			this.minMana = minMana;
		}
		
		private final @Nullable GeneratingFlowerType type;
		private final long minMana;
		
		boolean test(ManaStatsWsd stats) {
			return (type == null ? stats.total() : stats.get(type)) >= minMana;
		}
		
		@Override
		public JsonObject serialize(ConditionArraySerializer conditions) {
			JsonObject yes = super.serialize(conditions);
			
			if(type != null) yes.addProperty("flower", type.name);
			
			//Pretty-print by using `pools` if it makes sense.
			if(minMana % 1_000_000 == 0) yes.addProperty("pools", minMana / 1_000_000);
			else yes.addProperty("mana", minMana);
			
			return yes;
		}
	}
}

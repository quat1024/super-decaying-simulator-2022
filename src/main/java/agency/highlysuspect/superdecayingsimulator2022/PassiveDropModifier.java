package agency.highlysuspect.superdecayingsimulator2022;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class PassiveDropModifier extends LootModifier {
	public PassiveDropModifier(ILootCondition[] conditions) {
		super(conditions);
	}
	
	private static final ILootFunction copyNbt = CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY)
		.addOperation("passiveDecayTicks", "BlockEntityTag.passiveDecayTicks", CopyNbt.Action.REPLACE)
		.build();
	
	@Nonnull
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
		TileEntity wow = context.get(LootParameters.BLOCK_ENTITY);
		if(wow == null) return generatedLoot;
		
		GeneratingFlowerType type = GeneratingFlowerType.byType(wow.getType());
		if(type == null) return generatedLoot;
		
		ForgeConfigSpec.BooleanValue value = SuperDecayingSimulator2022Config.CONFIG.passiveOverride.get(type);
		if(value == null || !value.get()) return generatedLoot;
		
		for(int i = 0; i < generatedLoot.size(); i++) {
			ItemStack stack = generatedLoot.get(i);
			if(type == GeneratingFlowerType.byItem(stack.getItem())) {
				generatedLoot.set(i, copyNbt.apply(stack, context));
			}
		}
		
		return generatedLoot;
	}
	
	public static class Serializer extends GlobalLootModifierSerializer<PassiveDropModifier> {
		@Override
		public PassiveDropModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
			return new PassiveDropModifier(conditions);
		}
		
		@Override
		public JsonObject write(PassiveDropModifier instance) {
			return new JsonObject();
		}
	}
}

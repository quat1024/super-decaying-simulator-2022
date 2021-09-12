package agency.highlysuspect.superdecayingsimulator2022.mixin;

import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022LootHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.LootFunctionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.BiFunction;

@Mixin(LootPool.class)
public class LootPoolMixin implements SuperDecayingSimulator2022LootHandler.PoolExt {
	@Shadow @Final private List<LootEntry> lootEntries;
	@Shadow @Final @Mutable private ILootFunction[] functions;
	@Shadow @Final @Mutable private BiFunction<ItemStack, LootContext, ItemStack> combinedFunctions;
	
	@Override
	public List<LootEntry> sds2022$entries() {
		return lootEntries;
	}
	
	@Override
	public void sds2022$addFunction(ILootFunction newFunc) {
		ILootFunction[] newArray = new ILootFunction[functions.length + 1];
		System.arraycopy(functions, 0, newArray, 0, functions.length);
		newArray[newArray.length - 1] = newFunc;
		
		functions = newArray;
		combinedFunctions = LootFunctionManager.combine(functions);
	}
}

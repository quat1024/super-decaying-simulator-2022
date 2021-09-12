package agency.highlysuspect.superdecayingsimulator2022.mixin;

import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022LootHandler;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Deque;

@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {
	@Inject(
		method = "loadLootTable",
		remap = false,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/loot/LootTable;setLootTableId(Lnet/minecraft/util/ResourceLocation;)V",
			remap = false //Forge extension method
		),
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private static void modifyLootTableActually(Gson gson, ResourceLocation name, JsonElement data, boolean custom, LootTableManager lootTableManager, CallbackInfoReturnable<LootTable> cir, Deque<?> whatever, LootTable table) {
		SuperDecayingSimulator2022LootHandler.modifyLootTable(table);
	}
}

package agency.highlysuspect.superdecayingsimulator2022.mixin;

import agency.highlysuspect.superdecayingsimulator2022.GeneratingFlowerType;
import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022Config;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

@Mixin(ItemBlockSpecialFlower.class)
public class ItemBlockSpecialFlowerMixin {
	@Redirect(
		method = "getDurabilityForDisplay",
		remap = false, //from IForgeItem
		at = @At(
			value = "INVOKE",
			target = "Lvazkii/botania/api/BotaniaAPI;getPassiveFlowerDecay()I",
			remap = false
		)
	)
	private int getPassiveDecayRedirect(BotaniaAPI api, ItemStack stack) {
		GeneratingFlowerType type = GeneratingFlowerType.byItem(stack.getItem());
		if(type == null) return api.getPassiveFlowerDecay();
		
		ForgeConfigSpec.BooleanValue override = SuperDecayingSimulator2022Config.CONFIG.passiveOverride.get(type);
		if(override == null || !override.get()) return api.getPassiveFlowerDecay();
		
		return SuperDecayingSimulator2022Config.CONFIG.decayTimeOverride.get(type).get();
	}
}

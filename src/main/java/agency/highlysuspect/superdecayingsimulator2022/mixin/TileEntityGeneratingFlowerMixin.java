package agency.highlysuspect.superdecayingsimulator2022.mixin;

import agency.highlysuspect.superdecayingsimulator2022.GeneratingFlowerType;
import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022Config;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;

@Mixin(TileEntityGeneratingFlower.class)
public class TileEntityGeneratingFlowerMixin {
	@Shadow(remap = false) private int mana;
	@Unique private int manaBeforeAdding;
	
	@Inject(
		method = "addMana",
		at = @At("HEAD"),
		remap = false
	)
	private void addManaStart(int x, CallbackInfo ci) {
		if(x > 0)	manaBeforeAdding = mana;
	}
	
	@Inject(
		method = "addMana",
		at = @At("TAIL"),
		remap = false
	)
	private void addManaEnd(int x, CallbackInfo ci) {
		if(x > 0) {
			int howMuch = mana - manaBeforeAdding;
			System.out.println(generatingFlowerType() + " generated " + howMuch + " mana");
		}
	}
	
	@Inject(
		method = "isPassiveFlower",
		at = @At("HEAD"),
		remap = false,
		cancellable = true
	)
	private void butIsItReallyPassiveTho(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(SuperDecayingSimulator2022Config.CONFIG.passiveOverride.get(generatingFlowerType()).get());
	}
	//Doesnt work
//	@ModifyVariable(
//		method = "tickFlower",
//		at = @At(
//			value = "INVOKE_ASSIGN",
//			target = "Lvazkii/botania/api/BotaniaAPI;getPassiveFlowerDecay()I",
//			remap = false
//		),
//		remap = false
//	)
//	private int muhBalance(int oldDecay) {
//		return SuperDecayingSimulator2022Config.CONFIG.decayTimeOverride.get(generatingFlowerType()).get();
//	}
	
	//this is a weird injection, sorry about it
	@Redirect(
		method = "tickFlower",
		remap = false,
		at = @At(
			value = "INVOKE",
			target = "Lvazkii/botania/api/BotaniaAPI;getPassiveFlowerDecay()I",
			remap = false
		)
	)
	private int getEpicFlowerDecay(BotaniaAPI what) {
		return SuperDecayingSimulator2022Config.CONFIG.decayTimeOverride.get(generatingFlowerType()).get();
	}
	
	@Unique
	private GeneratingFlowerType generatingFlowerType() {
		TileEntityGeneratingFlower thiss = (TileEntityGeneratingFlower) (Object) this;
		//noinspection unchecked
		TileEntityType<? extends TileEntityGeneratingFlower> tileType = (TileEntityType<? extends TileEntityGeneratingFlower>) thiss.getType();
		return GeneratingFlowerType.TYPE_LOOKUP.get(tileType);
	}
}

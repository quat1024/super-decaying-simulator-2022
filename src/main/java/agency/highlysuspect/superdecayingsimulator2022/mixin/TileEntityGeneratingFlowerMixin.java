package agency.highlysuspect.superdecayingsimulator2022.mixin;

import agency.highlysuspect.superdecayingsimulator2022.GeneratingFlowerType;
import agency.highlysuspect.superdecayingsimulator2022.ManaStatsWsd;
import agency.highlysuspect.superdecayingsimulator2022.SuperDecayingSimulator2022Config;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;

@Mixin(TileEntityGeneratingFlower.class)
public class TileEntityGeneratingFlowerMixin {
	@Shadow(remap = false) private int mana;
	@Unique private int manaBeforeAdding;
	
	@Inject(
		method = "addMana",
		remap = false,
		at = @At("HEAD")
	)
	private void addManaStart(int x, CallbackInfo ci) {
		if(x > 0)	manaBeforeAdding = mana;
	}
	
	@Inject(
		method = "addMana",
		remap = false,
		at = @At("TAIL") //Oh hey i have one of those
	)
	private void addManaEnd(int x, CallbackInfo ci) {
		int howMuch = mana - manaBeforeAdding;
		if(x > 0 && howMuch > 0 && SuperDecayingSimulator2022Config.CONFIG.trackStatistics.get()) {
			World world = ((TileEntityGeneratingFlower) (Object) this).getWorld();
			if(world instanceof ServerWorld) {
				ManaStatsWsd.getFor((ServerWorld) world).track(generatingFlowerType(), howMuch);
			}
		}
	}
	
	//Redirect the original call, instead of editing the isPassiveFlower method itself
	//This is so things like the thermalily (which override the method to return *false*) can work
	@Redirect(
		method = "tickFlower",
		remap = false,
		at = @At(
			value = "INVOKE",
			target = "Lvazkii/botania/api/subtile/TileEntityGeneratingFlower;isPassiveFlower()Z",
			remap = false
		)
	)
	private boolean isPassiveFlowerRedirect(TileEntityGeneratingFlower tile) {
		ForgeConfigSpec.BooleanValue b = SuperDecayingSimulator2022Config.CONFIG.passiveOverride.get(generatingFlowerType());
		if(b != null && b.get()) return true;
		
		else return tile.isPassiveFlower();
	}
	
	@Redirect(
		method = "tickFlower",
		remap = false,
		at = @At(
			value = "INVOKE",
			target = "Lvazkii/botania/api/BotaniaAPI;getPassiveFlowerDecay()I",
			remap = false
		)
	)
	private int getPassiveFlowerDecayRedirect(BotaniaAPI api) {
		ForgeConfigSpec.IntValue time = SuperDecayingSimulator2022Config.CONFIG.decayTimeOverride.get(generatingFlowerType());
		if(time != null) return MathHelper.clamp(time.get(), 0, api.getPassiveFlowerDecay());
		
		else return api.getPassiveFlowerDecay();
	}
	
	@Unique
	private GeneratingFlowerType generatingFlowerType() {
		TileEntityGeneratingFlower thiss = (TileEntityGeneratingFlower) (Object) this;
		//noinspection unchecked
		TileEntityType<? extends TileEntityGeneratingFlower> tileType = (TileEntityType<? extends TileEntityGeneratingFlower>) thiss.getType();
		return GeneratingFlowerType.TYPE_LOOKUP.get(tileType);
	}
}

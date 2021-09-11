package agency.highlysuspect.superdecayingsimulator2022.mixin;

import agency.highlysuspect.superdecayingsimulator2022.ServerPlayNetHandlerExt;
import net.minecraft.network.play.ServerPlayNetHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayNetHandler.class)
public class ServerPlayNetHandlerMixin implements ServerPlayNetHandlerExt {
	@Unique private boolean statsGuiOpen;
	
	@Override
	public void sds2022$markStatsGui(boolean isOpen) {
		statsGuiOpen = isOpen;
	}
	
	@Override
	public boolean sds2022$hasStatsGuiOpened() {
		return statsGuiOpen;
	}
}

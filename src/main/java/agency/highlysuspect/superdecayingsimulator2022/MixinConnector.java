package agency.highlysuspect.superdecayingsimulator2022;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {
	@Override
	public void connect() {
		Mixins.addConfiguration("super-decaying-simulator-2022.mixins.json");
	}
}

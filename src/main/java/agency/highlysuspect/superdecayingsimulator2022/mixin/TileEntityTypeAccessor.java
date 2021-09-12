package agency.highlysuspect.superdecayingsimulator2022.mixin;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(TileEntityType.class)
public interface TileEntityTypeAccessor {
	@Accessor("validBlocks") Set<Block> sdss2022$validBlocks();
}

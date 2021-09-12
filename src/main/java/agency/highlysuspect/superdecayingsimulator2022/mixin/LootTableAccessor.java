package agency.highlysuspect.superdecayingsimulator2022.mixin;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LootTable.class)
public interface LootTableAccessor {
	@Accessor("pools") List<LootPool> sds2022$pools();
}

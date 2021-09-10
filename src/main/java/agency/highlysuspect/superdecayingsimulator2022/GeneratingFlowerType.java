package agency.highlysuspect.superdecayingsimulator2022;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;

import java.util.*;

public class GeneratingFlowerType implements Comparable<GeneratingFlowerType> {
	@SafeVarargs
	public GeneratingFlowerType(String name, Block representative, TileEntityType<? extends TileEntityGeneratingFlower>... types) {
		Preconditions.checkNotNull(representative, "must provide an icon");
		Preconditions.checkNotNull(types, "must provide non-null list of tile types");
		Preconditions.checkArgument(name != null && !name.isEmpty(), "must provide a name");
		Preconditions.checkArgument(types.length > 0, "must provide at least one tile type");
		
		this.name = name;
		this.representative = representative;
		this.types = types;
	}
	
	//Used in config file and stuff
	public final String name;
	
	//A block that represents this flower. currently not used
	public final Block representative;
	
	//Array of tile entity types that correspond to this flower. *Probably just 1 element long.
	public final TileEntityType<? extends TileEntityGeneratingFlower>[] types;
	
	//Whether this flower is usually passive even without this mod meddling with it, like the Hydroangeas.
	public boolean passiveByDefault = false;
	
	/**
	 * Mark this flower type as already "passive", like the Hydroangeas.
	 * An option to control the passivity of this flower will not be generated.
	 */
	public GeneratingFlowerType passive() {
		passiveByDefault = true;
		return this;
	}
	
	@Override
	public int compareTo(GeneratingFlowerType o) {
		return name.compareTo(o.name);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	//Please don't write to this map directly (especially in the Forge Funtime Land of parallel mod loading)
	public static final Map<TileEntityType<? extends TileEntityGeneratingFlower>, GeneratingFlowerType> TYPE_LOOKUP = new HashMap<>();
	public static final List<GeneratingFlowerType> ALL_TYPES = new ArrayList<>();
}

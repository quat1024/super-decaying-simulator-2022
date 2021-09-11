package agency.highlysuspect.superdecayingsimulator2022;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.botania.api.subtile.TileEntityGeneratingFlower;

import javax.annotation.Nullable;
import java.util.*;

public class GeneratingFlowerType implements Comparable<GeneratingFlowerType> {
	@SafeVarargs
	public GeneratingFlowerType(String name, @Nullable IItemProvider representative, TileEntityType<? extends TileEntityGeneratingFlower>... types) {
		Preconditions.checkArgument(name != null && !name.isEmpty(), "must provide a name");
		
		this.name = name;
		this.representative = representative;
		this.types = types;
	}
	
	private static final Map<TileEntityType<? extends TileEntityGeneratingFlower>, GeneratingFlowerType> TYPE_LOOKUP = new HashMap<>();
	private static final Map<String, GeneratingFlowerType> NAME_LOOKUP = new HashMap<>();
	private static final List<GeneratingFlowerType> ALL_TYPES = new ArrayList<>();
	
	public static final GeneratingFlowerType UNKNOWN_FLOWER = new GeneratingFlowerType("other_flowers", null).register(); //watch for field init order
	
	public static GeneratingFlowerType byType(TileEntityType<?> tileType) {
		return TYPE_LOOKUP.getOrDefault(tileType, UNKNOWN_FLOWER);
	}
	
	public static @Nullable GeneratingFlowerType byName(String name) {
		return NAME_LOOKUP.get(name);
	}
	
	public static Collection<String> allNames() {
		return NAME_LOOKUP.keySet();
	}
	
	public static Collection<GeneratingFlowerType> allTypes() {
		return ALL_TYPES;
	}
	
	//Used in config file and stuff
	public final String name;
	
	//A block that represents this flower. Used as the icon in the stats ui.
	public final @Nullable IItemProvider representative;
	
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
	
	public ITextComponent toText() {
		if(representative == null) {
			return new TranslationTextComponent("super-decaying-simulator-2022.unknown-flower");
		} else return new TranslationTextComponent(representative.asItem().getTranslationKey());
	}
	
	public ItemStack asItemStack() {
		return representative == null ? ItemStack.EMPTY : new ItemStack(representative);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int compareTo(GeneratingFlowerType o) {
		//Sort the unknown type to the bottom
		if(this == UNKNOWN_FLOWER && o != UNKNOWN_FLOWER) return -1;
		if(o == UNKNOWN_FLOWER && this != UNKNOWN_FLOWER) return 1;
		//Otherwise put them in alphabetical order
		return name.compareTo(o.name);
	}
	
	public GeneratingFlowerType register() {
		ALL_TYPES.add(this);
		NAME_LOOKUP.put(name, this);
		
		for(TileEntityType<? extends TileEntityGeneratingFlower> tileType : types) {
			TYPE_LOOKUP.put(tileType, this);
		}
		return this;
	}
	
	public static void allDoneRegistering() {
		Collections.sort(ALL_TYPES);
	}
}

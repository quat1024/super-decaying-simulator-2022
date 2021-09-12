package agency.highlysuspect.superdecayingsimulator2022;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import javax.annotation.Nullable;
import java.util.*;

public class GeneratingFlowerType implements Comparable<GeneratingFlowerType> {
	public GeneratingFlowerType(String name) {
		Preconditions.checkArgument(name != null && !name.isEmpty(), "must provide a name");
		
		this.name = name;
	}
	
	public final String name;
	
	public Block[] blocks = new Block[0];
	public IItemProvider icon = Items.AIR;
	public TileEntityType<?>[] types = new TileEntityType[0];
	public boolean passiveByDefault = false;
	
	public GeneratingFlowerType blocks(Block... blocks) {
		this.blocks = blocks;
		return icon(blocks[0]);
	}
	
	public GeneratingFlowerType icon(IItemProvider icon) {
		this.icon = icon;
		return this;
	}
	
	public GeneratingFlowerType tileTypes(TileEntityType<?>... types) {
		this.types = types;
		return this;
	}
	
	public GeneratingFlowerType passive() {
		passiveByDefault = true;
		return this;
	}
	
	public ITextComponent toText() {
		if(icon == null) {
			return new TranslationTextComponent("super-decaying-simulator-2022.unknown-flower");
		} else return icon.asItem().getDisplayName(new ItemStack(icon));
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int compareTo(GeneratingFlowerType o) {
		return name.compareTo(o.name);
	}
	
	public GeneratingFlowerType register() {
		ALL_TYPES.add(this);
		NAME_LOOKUP.put(name, this);
		
		for(TileEntityType<?> tileType : types) TYPE_LOOKUP.put(tileType, this);
		for(Block b : blocks) BLOCK_LOOKUP.put(b, this);
		
		return this;
	}
	
	public static void allDoneRegistering() {
		Collections.sort(ALL_TYPES);
	}
	
	private static final Map<TileEntityType<?>, GeneratingFlowerType> TYPE_LOOKUP = new HashMap<>();
	private static final Map<String, GeneratingFlowerType> NAME_LOOKUP = new HashMap<>();
	private static final Map<Block, GeneratingFlowerType> BLOCK_LOOKUP = new HashMap<>();
	private static final List<GeneratingFlowerType> ALL_TYPES = new ArrayList<>();
	
	public static @Nullable GeneratingFlowerType byType(TileEntityType<?> tileType) {
		return TYPE_LOOKUP.get(tileType);
	}
	
	public static @Nullable GeneratingFlowerType byName(String name) {
		return NAME_LOOKUP.get(name);
	}
	
	public static @Nullable GeneratingFlowerType byBlock(Block b) {
		return BLOCK_LOOKUP.get(b);
	}
	
	public static @Nullable GeneratingFlowerType byItem(Item i) {
		if(i instanceof ItemBlockSpecialFlower) return byBlock(((BlockItem) i).getBlock());
		else return null;
	}
	
	public static Collection<String> allNames() {
		return NAME_LOOKUP.keySet();
	}
	
	public static Collection<GeneratingFlowerType> allTypes() {
		return ALL_TYPES;
	}
	
	public static Collection<Block> allBlocks() {
		return BLOCK_LOOKUP.keySet();
	}
}

package agency.highlysuspect.superdecayingsimulator2022;

import com.mojang.brigadier.context.CommandContext;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

//N.B. this class is also used on the client through deserialization, might need to re-evaulate in 1.17 with the different worldsaveddata structure.
//like, is it still appropriate to use the same class for client purposes, like this. Pretty sure it is.
public class ManaStatsWsd extends WorldSavedData {
	public ManaStatsWsd() {
		super(NAME);
	}
	
	//Convenience
	public ManaStatsWsd(CompoundNBT nbt) {
		this();
		read(nbt);
	}
	
	public static final String NAME = "mana-generation-statistics";
	
	public static ManaStatsWsd getFor(ServerWorld world) {
		return getFor(world.getServer());
	}
	
	@SuppressWarnings("ConstantConditions") //Pretty sure the Overworld exists, although I mean with climate change these days, you never know
	public static ManaStatsWsd getFor(MinecraftServer server) {
		return server.getWorld(World.OVERWORLD).getSavedData().getOrCreate(ManaStatsWsd::new, NAME);
	}
	
	public static ManaStatsWsd getFor(CommandContext<CommandSource> ctx) {
		return getFor(ctx.getSource().getServer());
	}
	
	public final Object2LongOpenHashMap<GeneratingFlowerType> table = new Object2LongOpenHashMap<>();
	public long total = 0; //Redundant information
	
	public void track(GeneratingFlowerType type, int howMuch) {
		if(type == null) return;
		
		table.addTo(type, howMuch);
		total += howMuch;
		
		markDirty();
	}
	
	public long get(GeneratingFlowerType type) {
		if(type == null) return 0;
		else return table.getLong(type);
	}
	
	public void resetAll() {
		table.clear();
		total = 0;
		
		markDirty();
	}
	
	public void reset(GeneratingFlowerType type) {
		if(type == null) return;
		
		total -= table.removeLong(type);
		markDirty();
	}
	
	public long total() {
		return total;
	}
	
	@Override
	public CompoundNBT write(CompoundNBT cmp) {
		CompoundNBT statsBlock = new CompoundNBT();
		table.forEach((type, mana) -> statsBlock.putLong(type.name, mana));
		
		cmp.put("Stats", statsBlock);
		return cmp;
	}
	
	@Override
	public void read(CompoundNBT nbt) {
		CompoundNBT statsBlock = nbt.getCompound("Stats");
		
		table.clear();
		total = 0;
		for(String key : statsBlock.keySet()) {
			GeneratingFlowerType type = GeneratingFlowerType.byName(key);
			if(type != null) {
				long howMuch = statsBlock.getLong(key);
				table.put(type, howMuch);
				total += howMuch;
			}
		}
	}
}

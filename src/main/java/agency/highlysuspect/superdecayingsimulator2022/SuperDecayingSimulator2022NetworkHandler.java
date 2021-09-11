package agency.highlysuspect.superdecayingsimulator2022;

import agency.highlysuspect.superdecayingsimulator2022.client.ManaStatsGui;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.function.Supplier;

//styled a bit after patchouli's
public class SuperDecayingSimulator2022NetworkHandler {
	private static final String PROTOCOL = "ayy lmao";
	
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
		.named(SuperDecayingSimulator2022.id("channel"))
		.networkProtocolVersion(() -> PROTOCOL)
		.clientAcceptedVersions(PROTOCOL::equals)
		.serverAcceptedVersions(PROTOCOL::equals)
		.simpleChannel();
	
	@SuppressWarnings("ConstantConditions") //in the packet handler; pretty sure the Overworld exists.
	public static void initialize() {
		CHANNEL.messageBuilder(S2COpenOrUpdateGui.class, 0, NetworkDirection.PLAY_TO_CLIENT)
			.encoder(S2COpenOrUpdateGui::encode)
			.decoder(S2COpenOrUpdateGui::new)
			.consumer(S2COpenOrUpdateGui::handle)
			.add();
		
		CHANNEL.messageBuilder(C2SSetGuiStatus.class, 1, NetworkDirection.PLAY_TO_SERVER)
			.encoder(C2SSetGuiStatus::encode)
			.decoder(C2SSetGuiStatus::new)
			.consumer(C2SSetGuiStatus::handle)
			.add();
		
		MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent e) -> {
			if(e.phase == TickEvent.Phase.START) {
				//What the fuck, Forge's ServerTickEvent doesn't provide access to the fucking server, hahaha
				MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
				
				//Every half-second or so, if there are any players with the mana stats gui open, send them an update packet
				if(server.getWorld(World.OVERWORLD).getGameTime() % 10 != 0) return;
				for(ServerPlayerEntity player : server.getPlayerList().getPlayers())
					if(((ServerPlayNetHandlerExt) player.connection).sds2022$hasStatsGuiOpened())
						CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new S2COpenOrUpdateGui(ManaStatsWsd.getFor(player.getServerWorld()), false));
			}
		});
	}
	
	public static class S2COpenOrUpdateGui {
		public S2COpenOrUpdateGui(ManaStatsWsd stats, boolean openNew) {
			this.stats = stats;
			this.openNew = openNew;
		}
		
		public S2COpenOrUpdateGui(PacketBuffer buf) {
			this(new ManaStatsWsd(buf.readCompoundTag()), buf.readBoolean());
		}
		
		private final ManaStatsWsd stats;
		private final boolean openNew;
		
		public void encode(PacketBuffer buf) {
			buf.writeCompoundTag(stats.write(new CompoundNBT()));
			buf.writeBoolean(openNew);
		}
		
		@SuppressWarnings("Convert2Lambda")
		public void handle(Supplier<NetworkEvent.Context> ctx) {
			NetworkEvent.Context piss = ctx.get();
			piss.setPacketHandled(true);
			
			//due to forge's "SIMPLE" network handler forgetting that the client and server are different - what a concept -
			//i can't define the handler in a separate place reachable only from client initializer
			//so i need to manually prevent classloading blowup. Awesome
			//I love forge
			piss.enqueueWork(new Runnable() {
				@Override
				public void run() {
					boolean alreadyOpen = Minecraft.getInstance().currentScreen instanceof ManaStatsGui;
					
					if(alreadyOpen) {
						//Update the existing stats gui with this information.
						((ManaStatsGui) Minecraft.getInstance().currentScreen).setStats(stats);
					} else if(openNew) {
						//The server has requested to open a new stats gui. 
						Minecraft.getInstance().displayGuiScreen(new ManaStatsGui(stats));
					} else {
						//The server has sent an update-gui packet, but we don't have the gui open right now.
						//Remind the server that we closed the gui.
						CHANNEL.reply(new C2SSetGuiStatus(false), piss);
					}
				}
			});
		}
	}
	
	public static class C2SSetGuiStatus {
		public C2SSetGuiStatus(boolean isOpen) {
			this.isOpen = isOpen;
		}
		
		public C2SSetGuiStatus(PacketBuffer buf) {
			this(buf.readBoolean());
		}
		
		public void encode(PacketBuffer buf) {
			buf.writeBoolean(isOpen);
		}
		
		private final boolean isOpen;
		
		public void handle(Supplier<NetworkEvent.Context> ctx) {
			NetworkEvent.Context piss = ctx.get();
			piss.setPacketHandled(true);
			
			piss.enqueueWork(() -> {
				if(piss.getSender() != null) {
					ServerPlayNetHandlerExt ext = (ServerPlayNetHandlerExt) piss.getSender().connection;
					
					//If the client does not already have the stats gui open, send them an update right now.
					if(!ext.sds2022$hasStatsGuiOpened() && isOpen) {
						CHANNEL.reply(new S2COpenOrUpdateGui(ManaStatsWsd.getFor(piss.getSender().getServerWorld()), false), piss);
					}
					
					ext.sds2022$markStatsGui(isOpen);
				}
			});
		}
	}
}

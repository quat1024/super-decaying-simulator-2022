package agency.highlysuspect.superdecayingsimulator2022;

import agency.highlysuspect.superdecayingsimulator2022.client.ManaStatsGui;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class SuperDecayingSimulator2022Commands {
	public static void register(CommandDispatcher<CommandSource> disp) {
		disp.register(
			literal(SuperDecayingSimulator2022.MODID)
				.then(stats())
		);
	}
	
	private static final SuggestionProvider<CommandSource> SUGGEST_GENERATING_FLOWER_NAMES = (context, builder) -> ISuggestionProvider.suggest(GeneratingFlowerType.NAME_LOOKUP.keySet().stream(), builder);
	
	private static ArgumentBuilder<CommandSource, ?> stats() {
		return literal("stats")
			.then(statsShowAll(literal("show-all")))
			.then(statsShowOne(literal("show")))
			.then(statsResetAll(literal("reset-all").requires(src -> src.hasPermissionLevel(2))))
			.then(statsResetOne(literal("reset").requires(src -> src.hasPermissionLevel(2))))
			.then(statsGui(literal("gui")));
	}
	
	private static ArgumentBuilder<CommandSource, ?> statsShowAll(ArgumentBuilder<CommandSource, ?> a) {
		return a.executes(ctx -> {
			ManaStatsWsd stats = ManaStatsWsd.getFor(ctx);
			stats.table.forEach((type, mana) -> ctx.getSource().sendFeedback(new StringTextComponent(type.name + " - " + mana), true));
			return (int) stats.total();
		});
	}
	
	private static ArgumentBuilder<CommandSource, ?> statsShowOne(ArgumentBuilder<CommandSource, ?> a) {
		return a
			.then(argument("which", StringArgumentType.string()).suggests(SUGGEST_GENERATING_FLOWER_NAMES))
			.executes(ctx -> {
				ManaStatsWsd stats = ManaStatsWsd.getFor(ctx);
				String yeah = StringArgumentType.getString(ctx, "which");
				GeneratingFlowerType type = GeneratingFlowerType.byName(yeah);
				if(type == null) throw new SimpleCommandExceptionType(() -> "No flower named " + yeah).create();
				
				long howMuch = stats.get(type);
				ctx.getSource().sendFeedback(new StringTextComponent(type.name + " - " + howMuch), true);
				return (int) howMuch;
			});
	}
	
	private static ArgumentBuilder<CommandSource, ?> statsResetAll(ArgumentBuilder<CommandSource, ?> a) {
		return a
			.requires(src -> src.hasPermissionLevel(2))
			.executes(ctx -> {
				ManaStatsWsd stats = ManaStatsWsd.getFor(ctx);
				stats.resetAll();
				ctx.getSource().sendFeedback(new StringTextComponent("Reset all mana stats"), true);
				return 0;
			});
	}
	
	private static ArgumentBuilder<CommandSource, ?> statsResetOne(ArgumentBuilder<CommandSource, ?> a) {
		return a
			.then(argument("which", StringArgumentType.string()).suggests(SUGGEST_GENERATING_FLOWER_NAMES))
			.executes(ctx -> {
				ManaStatsWsd stats = ManaStatsWsd.getFor(ctx);
				String yeah = StringArgumentType.getString(ctx, "which");
				GeneratingFlowerType type = GeneratingFlowerType.byName(yeah);
				if(type == null) throw new SimpleCommandExceptionType(() -> "No flower named " + yeah).create();
				
				stats.reset(type);
				ctx.getSource().sendFeedback(new StringTextComponent("Reset stats for flower " + type.name), true);
				return (int) stats.total();
			});
	}
	
	private static ArgumentBuilder<CommandSource, ?> statsGui(ArgumentBuilder<CommandSource, ?> a) {
		return a.executes(ctx -> {
			ManaStatsWsd stats = ManaStatsWsd.getFor(ctx);
			ServerPlayerEntity player = ctx.getSource().asPlayer();
			
			//Beginning to regret this mod name.
			SuperDecayingSimulator2022NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
				new SuperDecayingSimulator2022NetworkHandler.S2COpenOrUpdateGui(stats, true));
			
			return 0;
		});
	}
}

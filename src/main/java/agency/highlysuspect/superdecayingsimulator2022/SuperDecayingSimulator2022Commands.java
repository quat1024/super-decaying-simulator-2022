package agency.highlysuspect.superdecayingsimulator2022;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
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
	
	private static final SuggestionProvider<CommandSource> SUGGEST_GENERATING_FLOWER_NAMES = (context, builder) -> ISuggestionProvider.suggest(GeneratingFlowerType.allNames().stream(), builder);
	
	private static ArgumentBuilder<CommandSource, ?> stats() {
		return literal("stats")
			.then(statsShowAll(literal("show-all")))
			.then(statsShowOne(literal("show")))
			.then(statsResetAll(literal("reset-all").requires(src -> src.hasPermissionLevel(2))))
			.then(statsResetOne(literal("reset").requires(src -> src.hasPermissionLevel(2))))
			.then(statsGui(literal("gui")));
	}
	
	private static void doShow(CommandContext<CommandSource> ctx, GeneratingFlowerType type, long mana) {
		ctx.getSource().sendFeedback(new TranslationTextComponent("command.super-decaying-simulator-2022.stats.show", type.toText(), mana), true);
	}
	
	private static ArgumentBuilder<CommandSource, ?> statsShowAll(ArgumentBuilder<CommandSource, ?> a) {
		return a.executes(ctx -> {
			ManaStatsWsd stats = ManaStatsWsd.getFor(ctx);
			stats.table.forEach((type, mana) -> doShow(ctx, type, mana));
			return (int) stats.total();
		});
	}
	
	private static final DynamicCommandExceptionType NOT_FLOWER = new DynamicCommandExceptionType(a -> new TranslationTextComponent("command.super-decaying-simulator-2022.no_flower", a));
	private static ArgumentBuilder<CommandSource, ?> statsShowOne(ArgumentBuilder<CommandSource, ?> a) {
		return a
			.then(argument("which", StringArgumentType.string()).suggests(SUGGEST_GENERATING_FLOWER_NAMES))
			.executes(ctx -> {
				ManaStatsWsd stats = ManaStatsWsd.getFor(ctx);
				String yeah = StringArgumentType.getString(ctx, "which");
				GeneratingFlowerType type = GeneratingFlowerType.byName(yeah);
				if(type == null) throw NOT_FLOWER.create(yeah);
				
				long mana = stats.get(type);
				doShow(ctx, type, mana);
				return (int) mana;
			});
	}
	
	private static ArgumentBuilder<CommandSource, ?> statsResetAll(ArgumentBuilder<CommandSource, ?> a) {
		return a
			.requires(src -> src.hasPermissionLevel(2))
			.executes(ctx -> {
				ManaStatsWsd stats = ManaStatsWsd.getFor(ctx);
				stats.resetAll();
				ctx.getSource().sendFeedback(new TranslationTextComponent("command.super-decaying-simulator-2022.stats.reset-all"), true);
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
				if(type == null) throw NOT_FLOWER.create(yeah);
				
				stats.reset(type);
				ctx.getSource().sendFeedback(new TranslationTextComponent("command.super-decaying-simulator-2022.stats.reset-one", type.toText()), true);
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

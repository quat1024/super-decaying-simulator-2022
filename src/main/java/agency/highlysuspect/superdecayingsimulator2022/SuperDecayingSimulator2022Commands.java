package agency.highlysuspect.superdecayingsimulator2022;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import static net.minecraft.command.Commands.literal;

public class SuperDecayingSimulator2022Commands {
	public static void register(CommandDispatcher<CommandSource> disp) {
		disp.register(
			literal(SuperDecayingSimulator2022.MODID)
				.then(stats())
		);
	}
	
	//Unused rn, string arguments acted kinda funky and the commands didnt work
	//private static final DynamicCommandExceptionType NOT_FLOWER = new DynamicCommandExceptionType(a -> new TranslationTextComponent("command.super-decaying-simulator-2022.no_flower", a));
	//private static final SuggestionProvider<CommandSource> SUGGEST_GENERATING_FLOWER_NAMES = (context, builder) -> ISuggestionProvider.suggest(GeneratingFlowerType.allNames().stream(), builder);
	
	private static ArgumentBuilder<CommandSource, ?> stats() {
		return literal("stats")
			.then(statsShowAll())
			.then(statsResetAll().requires(s -> s.hasPermissionLevel(2)))
			.then(statsOpenGui());
	}
	
	private static void doShow(CommandContext<CommandSource> ctx, GeneratingFlowerType type, long mana) {
		ctx.getSource().sendFeedback(new TranslationTextComponent("command.super-decaying-simulator-2022.stats.show", type.toText(), mana), true);
	}
	
	private static ArgumentBuilder<CommandSource, ?> statsShowAll() {
		return literal("show").executes(ctx -> {
			ManaStatsWsd stats = ManaStatsWsd.getFor(ctx);
			stats.table.forEach((type, mana) -> doShow(ctx, type, mana));
			return (int) stats.total();
		});
	}
	
	private static ArgumentBuilder<CommandSource, ?> statsResetAll() {
		return literal("reset")
			.requires(src -> src.hasPermissionLevel(2))
			.executes(ctx -> {
				ManaStatsWsd stats = ManaStatsWsd.getFor(ctx);
				stats.resetAll();
				ctx.getSource().sendFeedback(new TranslationTextComponent("command.super-decaying-simulator-2022.stats.reset-all"), true);
				return 0;
			});
	}
	
	private static ArgumentBuilder<CommandSource, ?> statsOpenGui() {
		return literal("gui").executes(ctx -> {
			ManaStatsWsd stats = ManaStatsWsd.getFor(ctx);
			ServerPlayerEntity player = ctx.getSource().asPlayer();
			
			//Beginning to regret this mod name.
			SuperDecayingSimulator2022NetworkHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
				new SuperDecayingSimulator2022NetworkHandler.S2COpenOrUpdateGui(stats, true));
			
			return 0;
		});
	}
}

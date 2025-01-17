package me.neovitalism.votereward.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.neovitalism.neoapi.modloading.command.CommandBase;
import me.neovitalism.neoapi.permissions.NeoPermission;
import me.neovitalism.neoapi.utils.ColorUtil;
import me.neovitalism.neoapi.utils.StringUtil;
import me.neovitalism.votereward.VoteReward;
import me.neovitalism.votereward.config.VoteRewardConfig;
import me.neovitalism.votereward.voteparty.VoteParty;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class VotePartyCommand extends CommandBase {
    public VotePartyCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        super(dispatcher, "voteparty");
    }

    @Override
    public NeoPermission[] getBasePermissions() {
        return NeoPermission.of("votereward.voteparty", 0).toArray();
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> getCommand(LiteralArgumentBuilder<ServerCommandSource> command) {
        return command.executes(context -> {
                    if (!VoteRewardConfig.isVotePartiesEnabled()) {
                        context.getSource().sendMessage(ColorUtil.parseColour(
                                VoteReward.inst().getModPrefix() + "&cVoteParty is currently disabled."));
                    } else {
                        context.getSource().sendMessage(ColorUtil.parseColour(StringUtil.replaceReplacements(
                                VoteRewardConfig.getVotePartyCommandFeedback(),
                                Map.of("{count}", String.valueOf(VoteParty.getCurrentVotes()),
                                        "{target}", String.valueOf(VoteRewardConfig.getVotePartyTarget())))));
                    }
                    return Command.SINGLE_SUCCESS;
                }).then(literal("set")
                .requires(NeoPermission.of("votereward.voteparty.set")::matches)
                .then(argument("count", IntegerArgumentType.integer(0))
                .executes(context -> {
                    int newCount = context.getArgument("count", Integer.class);
                    VoteParty.setCurrentVotes(newCount);
                    context.getSource().sendMessage(ColorUtil.parseColour(VoteReward.inst().getModPrefix() +
                            "&aSuccessfully set the vote party's current vote amount to " + newCount + "!"));
                    return Command.SINGLE_SUCCESS;
                }))).then(literal("forceparty")
                .requires(NeoPermission.of("votereward.voteparty.force")::matches)
                .executes(context -> {
                    VoteParty.executeRewards(context.getSource().getName());
                    return Command.SINGLE_SUCCESS;
                })
        );
    }
}

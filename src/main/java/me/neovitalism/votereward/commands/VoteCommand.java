package me.neovitalism.votereward.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.neovitalism.neoapi.modloading.command.CommandBase;
import me.neovitalism.neoapi.permissions.NeoPermission;
import me.neovitalism.neoapi.utils.ColorUtil;
import me.neovitalism.votereward.config.VoteRewardConfig;
import net.minecraft.server.command.ServerCommandSource;

public class VoteCommand extends CommandBase {
    public VoteCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        super(dispatcher, "vote");
    }

    @Override
    public NeoPermission[] getBasePermissions() {
        return NeoPermission.of("votereward.vote", 0).toArray();
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> getCommand(LiteralArgumentBuilder<ServerCommandSource> command) {
        return command.executes(context -> {
            StringBuilder sb = new StringBuilder();
            for (String line : VoteRewardConfig.getVoteCommandFeedback()) {
                if (!sb.isEmpty()) sb.append("<newline>");
                sb.append(line);
            }
            context.getSource().sendMessage(ColorUtil.parseColour(sb.toString()));
            return Command.SINGLE_SUCCESS;
        });
    }
}

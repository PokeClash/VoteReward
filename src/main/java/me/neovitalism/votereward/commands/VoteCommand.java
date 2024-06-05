package me.neovitalism.votereward.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.modloading.command.CommandBase;
import me.neovitalism.neoapi.utils.ChatUtil;
import me.neovitalism.votereward.VoteReward;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class VoteCommand implements CommandBase {
    public VoteCommand(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher) {
        register(instance, dispatcher);
    }

    @Override
    public String[] getCommandAliases() {
        return new String[0];
    }

    @Override
    public LiteralCommandNode<ServerCommandSource> register(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher) {
        return dispatcher.register(literal("vote")
                .executes(context -> {
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i < VoteReward.voteCommandFeedback.size(); i++) {
                        if(i != 0) sb.append("<newline>");
                        sb.append(VoteReward.voteCommandFeedback.get(i));
                    }
                    ChatUtil.sendPrettyMessage(context.getSource(), sb.toString());
                    return Command.SINGLE_SUCCESS;
                }));
    }
}

package me.neovitalism.votereward.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.modloading.command.CommandBase;
import me.neovitalism.neoapi.utils.ChatUtil;
import me.neovitalism.votereward.voteparty.VoteParty;
import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class VotePartyCommand implements CommandBase {
    public VotePartyCommand(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher) {
        register(instance, dispatcher);
    }

    @Override
    public String[] getCommandAliases() {
        return new String[0];
    }

    @Override
    public LiteralCommandNode<ServerCommandSource> register(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher) {
        return dispatcher.register(literal("voteparty")
                .executes(context -> {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("{count}", String.valueOf(VoteParty.getCurrentVotes()));
                    replacements.put("{target}", String.valueOf(VoteParty.getTargetVotes()));
                    ChatUtil.sendPrettyMessage(context.getSource(), VoteParty.getCommandResponse(), replacements);
                    return Command.SINGLE_SUCCESS;
                }));
    }
}

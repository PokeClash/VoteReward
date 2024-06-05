package me.neovitalism.votereward.commands;

import com.mojang.brigadier.CommandDispatcher;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.modloading.command.ReloadCommandBase;
import net.minecraft.server.command.ServerCommandSource;

public class VRReloadCommand extends ReloadCommandBase {
    public VRReloadCommand(NeoMod instance, CommandDispatcher<ServerCommandSource> dispatcher) {
        super(instance, dispatcher);
    }

    @Override
    public String[] getCommandAliases() {
        return new String[]{"vr"};
    }
}

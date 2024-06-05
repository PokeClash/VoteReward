package me.neovitalism.votereward;

import com.vexsoftware.votifier.fabric.event.VotifierEvent;
import me.neovitalism.neoapi.lang.LangManager;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.modloading.config.Configuration;
import me.neovitalism.neoapi.utils.ChatUtil;
import me.neovitalism.votereward.commands.VRReloadCommand;
import me.neovitalism.votereward.commands.VoteCommand;
import me.neovitalism.votereward.commands.VotePartyCommand;
import me.neovitalism.votereward.voteparty.VoteParty;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteReward extends NeoMod {
    public static List<String> voteCommandFeedback;
    public static List<String> commandsOnVote;

    @Override
    public String getModID() {
        return "VoteReward";
    }

    @Override
    public String getModPrefix() {
        return "&#696969[&bVoteReward&#696969]&f ";
    }

    @Override
    public LangManager getLangManager() {
        return null;
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            this.configManager(); // In case it doesn't load first, cba to test, won't hurt
            new VRReloadCommand(this, dispatcher);
            new VoteCommand(this, dispatcher);
            if(VoteParty.isCommandEnabled()) new VotePartyCommand(this, dispatcher);
        });
        VotifierEvent.EVENT.register(vote -> {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("{player}", vote.getUsername());
            for(String command : commandsOnVote) {
                getServer().getCommandManager().executeWithPrefix(getServer().getCommandSource(),
                        ChatUtil.replaceReplacements(command, replacements));
            }
            if(VoteParty.isEnabled()) VoteParty.increment(this, vote.getUsername());
        });
    }

    @Override
    public void configManager() {
        Configuration config = getDefaultConfig();
        voteCommandFeedback = config.getStringList("VoteCommandFeedback");
        commandsOnVote = config.getStringList("CommandsOnVote");
        VoteParty.reload(config.getSection("VoteParty"));
        if(VoteParty.isEnabled()) {
            try {
                File currentVotesFile = this.getOrCreateConfigurationFile("current-votes.yml");
                Configuration currentVotesConfig = getConfig("current-votes.yml");
                VoteParty.loadCurrentVotes(currentVotesFile, currentVotesConfig);
            } catch (IOException ignored) {}
        }
    }
}

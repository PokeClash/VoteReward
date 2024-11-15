package me.neovitalism.votereward;

import com.vexsoftware.votifier.fabric.event.VoteListener;
import me.neovitalism.neoapi.async.NeoAPIExecutorManager;
import me.neovitalism.neoapi.async.NeoExecutor;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.modloading.command.CommandRegistryInfo;
import me.neovitalism.neoapi.modloading.command.ReloadCommand;
import me.neovitalism.neoapi.utils.CommandUtil;
import me.neovitalism.neoapi.utils.StringUtil;
import me.neovitalism.votereward.commands.VoteCommand;
import me.neovitalism.votereward.commands.VotePartyCommand;
import me.neovitalism.votereward.config.VoteRewardConfig;
import me.neovitalism.votereward.voteparty.VoteParty;

import java.util.Map;

public class VoteReward extends NeoMod {
    public static final NeoExecutor EXECUTOR = NeoAPIExecutorManager.createScheduler("VoteReward Thread", 1);
    private static VoteReward instance;

    @Override
    public String getModID() {
        return "VoteReward";
    }

    @Override
    public String getModPrefix() {
        return "&#696969[&bVoteReward&#696969]&f ";
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
        VoteReward.instance = this;
        VoteListener.EVENT.register(vote -> VoteReward.EXECUTOR.runTaskSync(() -> {
            for (String command : VoteRewardConfig.getCommandsOnVote()) {
                CommandUtil.executeServerCommand(StringUtil.replaceReplacements(command,
                        Map.of("{player}", vote.getUsername())));
            }
            if (VoteRewardConfig.isVotePartiesEnabled()) VoteParty.increment(vote.getUsername());
        }));
        VoteParty.loadCurrentVotes(this.getConfig("current-votes.yml", false));
        this.getLogger().info("Loaded!");
    }

    @Override
    public void configManager() {
        VoteRewardConfig.reload(this.getConfig("config.yml", true));
    }

    @Override
    public void registerCommands(CommandRegistryInfo info) {
        this.configManager();
        new ReloadCommand(this, info.getDispatcher(), "votereward", "vr");
        new VoteCommand(info.getDispatcher());
        new VotePartyCommand(info.getDispatcher());
    }

    public static VoteReward inst() {
        return VoteReward.instance;
    }
}

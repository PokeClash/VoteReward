package me.neovitalism.votereward;

import com.vexsoftware.votifier.fabric.event.VoteListener;
import me.neovitalism.neoapi.async.NeoAPIExecutorManager;
import me.neovitalism.neoapi.async.NeoExecutor;
import me.neovitalism.neoapi.modloading.NeoMod;
import me.neovitalism.neoapi.modloading.command.CommandRegistryInfo;
import me.neovitalism.neoapi.modloading.command.ReloadCommand;
import me.neovitalism.neoapi.player.PlayerManager;
import me.neovitalism.neoapi.utils.CommandUtil;
import me.neovitalism.neoapi.utils.StringUtil;
import me.neovitalism.votereward.commands.VoteCommand;
import me.neovitalism.votereward.commands.VotePartyCommand;
import me.neovitalism.votereward.config.VoteRewardConfig;
import me.neovitalism.votereward.hooks.PlaceholderAPIHook;
import me.neovitalism.votereward.storage.VoteStorage;
import me.neovitalism.votereward.util.UUIDCache;
import me.neovitalism.votereward.voteparty.VoteParty;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

public class VoteReward extends NeoMod {
    public static final NeoExecutor EXECUTOR = NeoAPIExecutorManager.createScheduler("VoteReward Thread", 2);
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
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            try {
                Class.forName("eu.pb4.placeholders.api.Placeholders");
                new PlaceholderAPIHook();
                this.getLogger().info("TextPlaceholderAPI Support Enabled!");
            } catch (ClassNotFoundException ignored) {}
        });
        VoteReward.instance = this;
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            if (player == null) return;
            UUIDCache.cacheUUID(player.getName().getString(), player.getUuid());
            VoteStorage.checkOwedVotes(player);
        });
        VoteListener.EVENT.register(vote -> VoteReward.EXECUTOR.runTaskSync(() -> {
            ServerPlayerEntity player = PlayerManager.getPlayer(vote.getUsername());
            if (!VoteRewardConfig.shouldStoreOfflineVotes() || player != null) {
                for (String command : VoteRewardConfig.getCommandsOnVote()) {
                    CommandUtil.executeServerCommand(StringUtil.replaceReplacements(command,
                            Map.of("{player}", vote.getUsername(), "{service}", vote.getServiceName())));
                }
            } else VoteStorage.storeVote(vote.getUsername(), vote.getServiceName());
            if (VoteRewardConfig.isVotePartiesEnabled()) VoteParty.increment(vote.getUsername(), vote.getServiceName());
        }));
        VoteParty.loadCurrentVotes(this.getConfig("current-votes.yml", false));
        VoteStorage.load(this.getConfig("stored-votes.yml", false));
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

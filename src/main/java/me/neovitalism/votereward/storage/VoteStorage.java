package me.neovitalism.votereward.storage;

import me.neovitalism.neoapi.async.NeoExecutor;
import me.neovitalism.neoapi.async.saving.AsyncSavable;
import me.neovitalism.neoapi.config.Configuration;
import me.neovitalism.neoapi.utils.CommandUtil;
import me.neovitalism.neoapi.utils.StringUtil;
import me.neovitalism.votereward.VoteReward;
import me.neovitalism.votereward.config.VoteRewardConfig;
import me.neovitalism.votereward.util.UUIDCache;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class VoteStorage extends AsyncSavable {
    private static VoteStorage instance;

    public VoteStorage(NeoExecutor executor) {
        super(executor);
        VoteStorage.instance = this;
    }

    private static final Map<UUID, Map<String, Integer>> STORED_VOTES = new HashMap<>();

    public static void storeVote(String username, String service) {
        UUID playerUUID = UUIDCache.getUUIDFromUsername(username);
        if (playerUUID == null) return;
        service = service.replace(".", ",,");
        Map<String, Integer> storedVotes = VoteStorage.STORED_VOTES.computeIfAbsent(playerUUID, uuid -> new HashMap<>());
        int stored = storedVotes.getOrDefault(service, 0);
        storedVotes.put(service, stored+1);
        VoteStorage.instance.markToSave();
    }

    public static void checkOwedVotes(ServerPlayerEntity player) {
        Map<String, Integer> owedVotes = VoteStorage.STORED_VOTES.get(player.getUuid());
        if (owedVotes == null) return;
        Map<String, String> replacements = new HashMap<>();
        replacements.put("{player}", player.getName().getString());
        for (Map.Entry<String, Integer> entry : owedVotes.entrySet()) {
            replacements.put("{service}", entry.getKey().replace(",,", "."));
            for (int i = 0; i < entry.getValue(); i++) {
                for (String command : VoteRewardConfig.getCommandsOnVote()) {
                    CommandUtil.executeServerCommand(StringUtil.replaceReplacements(command, replacements));
                }
            }
        }
        VoteStorage.STORED_VOTES.remove(player.getUuid());
        VoteStorage.instance.markToSave();
    }

    @Override
    protected void save() {
        Configuration owedCommands = new Configuration();
        for (Map.Entry<UUID, Map<String, Integer>> entry : new HashSet<>(VoteStorage.STORED_VOTES.entrySet())) {
            Configuration playerSection = new Configuration();
            for (Map.Entry<String, Integer> storedVotes : entry.getValue().entrySet()) {
                playerSection.set(storedVotes.getKey(), storedVotes.getValue());
            }
            owedCommands.set(entry.getKey().toString(), playerSection);
        }
        VoteReward.inst().saveConfig("stored-votes.yml", owedCommands);
    }

    public static void load(Configuration config) {
        if (config == null) return;
        for (String key : config.getKeys()) {
            UUID uuid = UUID.fromString(key);
            Configuration playerSection = config.getSection(key);
            Map<String, Integer> owedVotes = new HashMap<>();
            for (String service : playerSection.getKeys()) owedVotes.put(service, playerSection.getInt(service));
            VoteStorage.STORED_VOTES.put(uuid, owedVotes);
        }
    }
}

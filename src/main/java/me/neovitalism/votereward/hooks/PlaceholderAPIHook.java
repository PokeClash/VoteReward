package me.neovitalism.votereward.hooks;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.neovitalism.votereward.config.VoteRewardConfig;
import me.neovitalism.votereward.voteparty.VoteParty;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PlaceholderAPIHook {
    public PlaceholderAPIHook() {
        Placeholders.register(
                Identifier.of("votereward", "party_count"),
                (ctx, arg) -> PlaceholderResult.value(Text.literal(String.valueOf(VoteParty.getCurrentVotes())))
        );
        Placeholders.register(
                Identifier.of("votereward", "party_target"),
                (ctx, arg) -> PlaceholderResult.value(Text.literal(String.valueOf(VoteRewardConfig.getVotePartyTarget())))
        );
    }
}

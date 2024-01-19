/*
 * Copyright 2024 EnhancedTab contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package snw.enhancedtab;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * A wrapper which wraps {@link EnhancedTabCompleter} to a standard Bukkit {@link TabCompleter}.
 *
 * @author SNWCreations
 * @since 1.0.0
 */
class EnhancedTabCompleterWrapper extends PlayerTabCompletionHandler<Suggestion> implements TabCompleter {
    private final EnhancedTabCompleter delegate;

    EnhancedTabCompleterWrapper(EnhancedTabCompleter delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            handlePlayerTabCompletion(player, command, s, strings);
        }
        return Collections.emptyList();
    }

    @Override
    protected List<Suggestion> doTabCompletion(Player sender, Command command, String label, String[] args) {
        return delegate.onTabComplete(sender, command, label, args);
    }

    @Override
    protected void completeSuggestions(SuggestionsBuilder builder, Suggestion from) {
        String content = from.getContent();
        String tooltip = from.getTooltip();
        if (tooltip != null) {
            builder.suggest(content, chatMessage2NMSChatComponent(tooltip));
        } else {
            builder.suggest(content);
        }
    }

    // we can't simply reuse code from AsyncTabCompleter here :(
    @Override
    protected void handleCompletionException(Player requester, String rebuiltCommandLine, Throwable e) {
        requester.getServer().getLogger().log(Level.SEVERE,
                "Unhandled exception from completer " +
                        delegate +
                        " during tab completion for command " + rebuiltCommandLine, e);
    }

    private static Message chatMessage2NMSChatComponent(String message) {
        // IChatBaseComponent extends Message, so we can convert it here
        return (Message) WrappedChatComponent.fromLegacyText(message).getHandle();
    }
}

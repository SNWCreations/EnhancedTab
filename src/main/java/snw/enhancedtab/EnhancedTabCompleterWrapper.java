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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

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
        Component tooltip = from.getTooltipComponent();
        if (tooltip != null) {
            builder.suggest(content, chatMessage2NMSChatComponent(tooltip));
        } else {
            builder.suggest(content);
        }
    }

    @Override
    protected void handleCompletionException(Player requester, String rebuiltCommandLine, Throwable e) {
        DefaultTabCompletionExceptionHandler.handle(requester, rebuiltCommandLine, e, delegate);
    }

    private static Message chatMessage2NMSChatComponent(Component message) {
        String json = GsonComponentSerializer.gson().serialize(message);
        // IChatBaseComponent extends Message, so we can convert it here
        return (Message) WrappedChatComponent.fromJson(json).getHandle();
    }
}

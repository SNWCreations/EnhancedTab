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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

/**
 * The {@link TabCompleter} which wraps a sync tab completer to let it run asynchronously.
 *
 * @author SNWCreations
 * @since 1.0.0
 */
class AsyncTabCompleter extends PlayerTabCompletionHandler<String> implements TabCompleter {
    private static final ExecutorService executor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder()
                    .setNameFormat("Async Tab Completion Thread - #%d")
                    .setDaemon(true)
                    .build()
    );
    private final TabCompleter delegate;

    AsyncTabCompleter(TabCompleter tc) {
        delegate = tc;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            final Future<?> future = executor.submit(() -> handlePlayerTabCompletion(player, command, label, args));
            EnhancedTabPlugin.getInstance().completionFuture.put(player.getUniqueId(), future);
        } // other things like console? unsupported yet
        return Collections.emptyList();
    }

    @Override
    protected List<String> doTabCompletion(Player sender, Command command, String label, String[] args) {
        return delegate.onTabComplete(sender, command, label, args);
    }

    @Override
    protected void completeSuggestions(SuggestionsBuilder builder, String from) {
        builder.suggest(from);
    }

    @Override
    protected void handleCompletionException(Player requester, String rebuiltCommandLine, Throwable e) {
        DefaultTabCompletionExceptionHandler.handle(requester, rebuiltCommandLine, e, delegate);
    }
}

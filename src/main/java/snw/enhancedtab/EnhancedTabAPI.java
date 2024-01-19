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

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 * The API of the Enhanced TAB plugin. <br>
 *
 * <b>Tips</b>: All conversion methods of tab completer just wrap the provided completer,
 * so you can no longer call other methods defined on original tab completer using the wrapped ones.
 * And the wrapped tab completer will not accept TAB completion request from server console!
 *
 * @author SNWCreations
 * @since 1.0.0
 */
public interface EnhancedTabAPI {

    /**
     * Wrap the provided tab completer to an async one. <br>
     * Return the input if it had been wrapped by this API. <br>
     * You should know your tab completion might be interrupted if player changed the
     * content of their chat bar (for example, they added characters to the chat bar).
     * So make sure your code can handle interruptions. <br>
     * The result will not be a {@link CommandExecutor} if the original one is not. <br>
     * We recommended you to use {@link #toAsyncTE} if your {@link TabCompleter}
     * also implemented {@link CommandExecutor}.
     *
     * @param tc The tab completer to be wrapped
     * @return The new async tab completer which is ready
     * to be passed to {@link org.bukkit.command.PluginCommand#setTabCompleter(TabCompleter)}.
     */
    TabCompleter toAsyncTC(TabCompleter tc);

    /**
     * Wrap the provided "tab executor" to an async one. <br>
     * Return the input if it had been wrapped by this API. <br>
     * <b>Tips</b>: Only tab completion will run asynchronously, command execution is still sync. <br>
     * You should know your tab completion might be interrupted if player changed the
     * content of their chat bar (for example, they added characters to the chat bar).
     * So make sure your code can handle interruptions. <br>
     *
     * @param from The "tab executor" to wrap
     * @return The new async "tab executor" which is ready
     * to be passed to {@link org.bukkit.command.PluginCommand#setExecutor(CommandExecutor)}.
     */
    <T extends TabCompleter & CommandExecutor, R extends TabCompleter & CommandExecutor>
    R toAsyncTE(T from);

    /**
     * Wraps the provided {@link EnhancedTabCompleter} into a Bukkit {@link TabCompleter}. <br>
     * Please use {@link #enhancedTEToBukkit} method instead if your {@link EnhancedTabCompleter}
     * implementation also implemented {@link CommandExecutor} interface. <br>
     * <b>Tips</b>: the result is not an async tab completer. To convert it to an async one,
     * use statements like {@code toAsyncTC(enhancedToBukkit(yourEnhancedTabCompleter))}.
     *
     * @param enhanced The instance of {@link EnhancedTabCompleter}
     * @return The tab completer which is ready to be passed
     * to {@link org.bukkit.command.PluginCommand#setTabCompleter(TabCompleter)}.
     */
    TabCompleter enhancedToBukkit(EnhancedTabCompleter enhanced);

    /**
     * Convert a {@link EnhancedTabCompleter} which also implemented {@link CommandExecutor}
     * interface to standard Bukkit interface implementation.
     *
     * @param from The original one
     * @return The wrapped "tab executor"
     */
    <T extends CommandExecutor & EnhancedTabCompleter, R extends CommandExecutor & TabCompleter>
    R enhancedTEToBukkit(T from);

    /**
     * Cancel the in-progress TAB completion executing by this API for the provided player.
     *
     * @param player The player
     */
    void cancelCompletionFor(Player player);

}

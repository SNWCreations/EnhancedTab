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

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.common.base.Preconditions;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * The main class of Enhanced TAB plugin, also the implementation of the API.
 *
 * @author SNWCreations
 * @since 1.0.0
 */
public final class EnhancedTabPlugin extends JavaPlugin implements EnhancedTabAPI {
    private static EnhancedTabPlugin instance;
    final Map<UUID, Future<?>> completionFuture = new HashMap<>();
    final Map<UUID, Integer> transactionId = new HashMap<>();
    final Map<UUID, String> completingCommands = new HashMap<>();
    ProtocolManager protocolManager;

    /**
     * Retrieves the instance of this plugin.
     *
     * @return The instance of this plugin
     * @throws IllegalStateException Thrown if the plugin is not enabled yet,
     *                               please check if you have added the name of
     *                               this plugin to "depend" list in your plugin.yml when you
     *                               encountered this exception.
     */
    public static EnhancedTabPlugin getInstance() throws IllegalStateException {
        Preconditions.checkState(instance != null, "The plugin is not enabled yet");
        return instance;
    }

    @Override
    public void onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new TransactionIdCapture(this)); // register transaction id capture
        instance = this;
    }

    @Override
    public void onDisable() {
        // release everything

        instance = null; // ensure API is not available now

        protocolManager.removePacketListeners(this); // remove packet listeners
        protocolManager = null; // release protocol manager

        for (Future<?> value : completionFuture.values()) {
            // notify the completion tasks that they are interrupted
            // however their logic will not be fully executed if the server stops faster than them
            value.cancel(true);
        }
        completionFuture.clear(); // remove all future object from map
        transactionId.clear(); // remove all tracked transaction id
        completingCommands.clear(); // remove commands caught from packets
    }

    // API Implementation start

    @Override
    public TabCompleter toAsyncTC(TabCompleter tc) {
        if (tc instanceof AsyncTabCompleter) {
            return tc;
        } else {
            if (tc instanceof CommandExecutor) {
                return toAsyncTE((CommandExecutor & TabCompleter) tc);
            } else {
                return new AsyncTabCompleter(tc);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends TabCompleter & CommandExecutor, R extends TabCompleter & CommandExecutor> R toAsyncTE(T from) {
        if (from instanceof AsyncTabExecutor<?>) {
            return (R) from;
        } else {
            return (R) new AsyncTabExecutor<>(from);
        }
    }

    @Override
    public TabCompleter enhancedToBukkit(EnhancedTabCompleter enhanced) {
        if (enhanced instanceof CommandExecutor) {
            return enhancedTEToBukkit((CommandExecutor & EnhancedTabCompleter) enhanced);
        } else {
            return new EnhancedTabCompleterWrapper(enhanced);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CommandExecutor & EnhancedTabCompleter, R extends CommandExecutor & TabCompleter> R enhancedTEToBukkit(T from) {
        // we don't need to do any check here because EnhancedTabCompleter != TabCompleter
        return (R) new EnhancedTabExecutor<>(from);
    }

    @Override
    public void cancelCompletionFor(Player player) {
        freePlayerData(player);
        Future<?> removed = completionFuture.remove(player.getUniqueId());
        if (removed != null) {
            removed.cancel(true);
        }
    }

    // API Implementation end

    void freePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        transactionId.remove(uuid);
        completingCommands.remove(uuid);
    }

}

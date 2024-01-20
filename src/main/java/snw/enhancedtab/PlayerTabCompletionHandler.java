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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Player TAB completion handler, the core of this plugin.
 *
 * @author SNWCreations
 * @since 1.0.0
 */
abstract class PlayerTabCompletionHandler<T> {

    protected abstract List<T> doTabCompletion(Player sender, Command command, String label, String[] args);

    protected abstract void completeSuggestions(SuggestionsBuilder builder, T from);

    protected abstract void handleCompletionException(Player requester, String rebuiltCommandLine, Throwable e);

    protected final void handlePlayerTabCompletion(Player sender, Command command, String label, String[] args) {
        List<T> list;

        // do TAB completion
        try {
            list = doTabCompletion(sender, command, label, args);
        } catch (Throwable e) {
            handleCompletionException(sender, "/" + label + " " + String.join(" ", args), e);
            return;
        }

        boolean useDefaults = list == null;
        if (!useDefaults && list.isEmpty()) {
            // nop because nothing will be suggested,
            // and make sure the transaction ID is still available
            // if delegate is also a PlayerTabCompletionHandler (e.g. EnhancedTabCompleterWrapper)
            return;
        }

        EnhancedTabPlugin plugin = EnhancedTabPlugin.getInstance();
        Integer transactionId = plugin.transactionId.remove(sender.getUniqueId()); // ensure the ID is released
        if (transactionId == null) { // plugin disabled or completion cancelled
            return;
        }

        // build necessary information
        int start;
        String commandLine = plugin.completingCommands.remove(sender.getUniqueId());
        String lastOne;
        if (args.length != 0) {
            lastOne = args[args.length - 1];
            int lastOneLength = lastOne.length();
            start = commandLine.length() - lastOneLength;
        } else {
            start = commandLine.length();
            lastOne = "";
        }

        SuggestionsBuilder builder = new SuggestionsBuilder(commandLine, start);
        if (!useDefaults) {
            for (T t : list) {
                completeSuggestions(builder, t);
            }
        } else {
            String lastOneLowerCase = lastOne.toLowerCase();
            // simulate what Bukkit did on null values
            List<String> matchingPlayerNames = sender.getServer().getOnlinePlayers()
                    .stream()
                    .filter(sender::canSee)
                    .map(Player::getName)
                    .filter(it -> it.toLowerCase().startsWith(lastOneLowerCase))
                    .collect(Collectors.toList());
            if (matchingPlayerNames.isEmpty()) {
                return; // avoid unnecessary reflection performed by ProtocolLib
            }
            for (String name : matchingPlayerNames) {
                builder.suggest(name);
            }
        }
        Suggestions suggestions = builder.build();

        // build the packet
        ProtocolManager protocolManager = plugin.protocolManager;
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.TAB_COMPLETE);
        packet.getIntegers().write(0, transactionId);
        packet.getModifier().write(1, suggestions);

        // send the packet
        protocolManager.sendServerPacket(sender, packet);
    }
}

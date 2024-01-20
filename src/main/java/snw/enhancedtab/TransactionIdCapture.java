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
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.UUID;

/**
 * The {@link PacketAdapter} implementation
 * which is used to capture transaction ID and the command line needed to be completed.
 *
 * @author SNWCreations
 * @since 1.0.0
 */
final class TransactionIdCapture extends PacketAdapter {
    TransactionIdCapture(Plugin plugin) {
        super(plugin,
                ListenerPriority.LOWEST,
                Collections.singleton(PacketType.Play.Client.TAB_COMPLETE),
                // must be SYNC to ensure this will get called before tab completion
                ListenerOptions.SYNC
        );
    }

    @Override
    public synchronized void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        int transaction = event.getPacket().getIntegers().read(0);
        String command = event.getPacket().getStrings().read(0);
        EnhancedTabPlugin instance = EnhancedTabPlugin.getInstance();
        instance.cancelCompletionFor(player); // cancel unfinished completion
        instance.transactionId.put(uuid, transaction);
        instance.completingCommands.put(uuid, command);
    }
}

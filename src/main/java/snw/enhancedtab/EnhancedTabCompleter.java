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

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The enhanced tab completer interface.
 *
 * @author SNWCreations
 * @since 1.0.0
 */
public interface EnhancedTabCompleter {

    /**
     * Called when player requests a tab completion
     * on the command which is bound by this implementation.
     *
     * @param sender  The requester
     * @param command The Bukkit command objet
     * @param label   The "alias" of the command
     * @param args    Currently provided arguments
     * @return The suggestion list, {@code null} to Bukkit defaults
     * (a list of player names which matches the last argument).
     */
    List<Suggestion> onTabComplete(Player sender, Command command, String label, String[] args);

}

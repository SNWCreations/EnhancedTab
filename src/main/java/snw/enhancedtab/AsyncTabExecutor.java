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
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * An {@link AsyncTabCompleter} implementation which also implements {@link CommandExecutor} interface.
 *
 * @author SNWCreations
 * @since 1.0.0
 */
class AsyncTabExecutor<T extends CommandExecutor & TabCompleter>
        extends AsyncTabCompleter implements CommandExecutor {
    private final T delegate;

    AsyncTabExecutor(T t) {
        super(t);
        this.delegate = t;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return delegate.onCommand(commandSender, command, s, strings);
    }

}

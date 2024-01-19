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

/**
 * The {@link EnhancedTabCompleter} implementation which also implemented
 * {@link CommandExecutor} interface.
 *
 * @author SNWCreations
 * @since 1.0.0
 */
final class EnhancedTabExecutor<T extends CommandExecutor & EnhancedTabCompleter>
        extends EnhancedTabCompleterWrapper implements CommandExecutor {
    private final T delegate;

    EnhancedTabExecutor(T delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return delegate.onCommand(commandSender, command, s, strings);
    }
}

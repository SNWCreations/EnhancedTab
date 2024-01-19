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

/**
 * The command suggestion object. <br>
 * Example: <br>
 * <code>Suggestion.of("test", org.bukkit.ChatColor.GREEN + "test")</code>
 *
 * @author SNWCreations
 * @since 1.0.0
 */
public final class Suggestion {
    private final String content;
    private final String tooltip; // nullable

    public Suggestion(String content, String tooltip) {
        this.content = content;
        this.tooltip = tooltip;
    }

    public String getContent() {
        return content;
    }

    public String getTooltip() {
        return tooltip;
    }

    // Just for coders who dislike "new" statements
    public static Suggestion of(String content, String tooltip) {
        return new Suggestion(content, tooltip);
    }
}

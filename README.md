# EnhancedTab

A simple Bukkit library (in plugin form) that provides enhanced command
 TAB completion experience to commands which are based on Bukkit's standard command system.

Support Bukkit 1.13+, tested on Minecraft 1.20.1, compatible with Paper (and its forks)

**Requires [ProtocolLib](https://github.com/dmulloy2/ProtocolLib) to work!**

Latest version: ![GitHub Tag](https://img.shields.io/github/v/tag/SNWCreations/EnhancedTab)

## Screenshots

Async Tab Completion

![](imgs/async.gif)

Async Enhanced TAB completion (with tooltip)

![](imgs/async+enhanced.gif)

Enhanced TAB completion (with tooltip)

![](imgs/enhanced.gif)

## Usage

### For server admins

Just download latest version of this plugin in
 [Releases](https://github.com/SNWCreations/EnhancedTab/releases) page and put it
 into your `plugins` folder.

After that, your plugins which require this library will no longer complain.

### For Developers

**There is a notice that all wrapped TAB completer will no longer accepts TAB completion request
 from server console!**

We have no idea to deal with that problem. :(

#### Importing

First, import the library to your project.

**For the following examples, please replace `(latest-version)` with the latest tag name manually.**

For Gradle users, please add the JitPack repository and this library
 as a dependency to your buildscript.

(The following example is suitable for Gradle 8.x)
```groovy
repositories {
    maven {
        name = "jitpack"
        url = "https://jitpack.io/"
    }
}

dependencies {
    compileOnly 'com.github.SNWCreations:EnhancedTab:(latest-version)'
}
```

For Maven users, please add the following configuration to your pom.xml

```xml
<repositories>
    <!-- ... your other repositories ... -->
    <repository>
        <id>jitpack</id>
        <url>https://jitpack.io/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.SNWCreations</groupId>
        <artifactId>EnhancedTab</artifactId>
        <version>(latest-version)</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

#### Other preparations

Add this as a dependency in your plugin.yml

```yml
# other plugin.yml stuff
depend:
 - EnhancedTab
```

After that, refresh your project, then you are good to go.

#### Example

```java
public class MyCompleter implements CommandExecutor, TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        Thread.sleep(3000); // sleep 3s for example, or other blocking operations like Database query?
        return List.of("aaaa", "bbbb", "cccc");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        // your command logic, will still execute synchronously!
        return true;
    }
}

// following lines in your plugin's onEnable() method:
EnhancedTabAPI api = EnhancedTabPlugin.getInstance();
getCommand("test").setExecutor(api.toAsyncTE(new MyCompleter()));
```

So your TAB completion will run asynchronously now!

Provide tooltip on your TAB suggestions? Of course, you can!

```java
public class MyCompleter implements CommandExecutor, EnhancedTabCompleter {
    @Override
    public List<Suggestion> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        Thread.sleep(3000); // sleep 3s for example, or other blocking operations like database query?
        return List.of(
                Suggestion.of( // or "new Suggestion", that's up to you!
                        "aaa", // the suggestion content
                        ChatColor.GREEN + "ChatColor = VERY YES!" // the tooltip, chat color is supported
                )
        );
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        // your command logic, will still execute synchronously!
        return true;
    }
}

// following lines in your plugin's onEnable() method:
EnhancedTabAPI api = EnhancedTabPlugin.getInstance();
getCommand("test").setExecutor(api.toAsyncTE(api.enhancedTEToBukkit(new MyCompleter())));
```

## Paper!

This plugin works on Paper (and its forks).

## License

EnhancedTab - The Enhanced command TAB completion library for Bukkit plugins.

Copyright (C) 2024 SNWCreations. Licensed under Apache 2.0 License.

See more information in LICENSE file in this repository.

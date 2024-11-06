package snw.enhancedtab;

import org.bukkit.entity.Player;

import java.util.logging.Level;

final class DefaultTabCompletionExceptionHandler {
    private DefaultTabCompletionExceptionHandler() {
    }

    static void handle(Player requester, String rebuiltCommandLine, Throwable e, Object delegate) {
        requester.getServer().getLogger().log(Level.SEVERE,
                "Unhandled exception from completer " +
                        delegate +
                        " during tab completion for command " + rebuiltCommandLine, e);
    }
}

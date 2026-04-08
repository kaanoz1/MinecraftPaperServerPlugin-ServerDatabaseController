package org.prag.mc.plugins.serverDatabaseController.Exceptions.Auth;

import org.prag.mc.plugins.serverDatabaseController.Models.RecordedPlayer;

public class NotRegisteredYet extends Exception {

    public NotRegisteredYet(RecordedPlayer player) {
        super(String.format("Player [ID: %d, Name: %s, UUID: %s] is not registered yet!",
                player.getId(),
                player.getNickname(),
                player.getUuid().toString()));
    }

    public NotRegisteredYet(String message) {
        super(message);
    }
}
package org.prag.mc.plugins.serverDatabaseController.Exceptions.Auth;

import org.prag.mc.plugins.serverDatabaseController.Models.RecordedPlayer;

public class AlreadyRegisteredException extends RuntimeException {

    public AlreadyRegisteredException(RecordedPlayer player) {
        super(String.format("Player [ID: %d, Name: %s, UUID: %s] is already registered!",
                player.getId(),
                player.getNickname(),
                player.getUuid().toString()));
    }

    public AlreadyRegisteredException(String message) {
        super(message);
    }
}
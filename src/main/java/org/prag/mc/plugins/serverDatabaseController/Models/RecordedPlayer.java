package org.prag.mc.plugins.serverDatabaseController.Models;

import jakarta.persistence.*;
import org.prag.mc.plugins.serverDatabaseController.Auth.PlayerRegisterOptions;
import org.prag.mc.plugins.serverDatabaseController.Exceptions.Auth.AlreadyRegisteredException;
import org.prag.mc.plugins.serverDatabaseController.Exceptions.Auth.NotRegisteredYet;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "players")
public class RecordedPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private long id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private UUID uuid;

    @Column(name = "nickname", unique = true, nullable = false)
    private String nickname;

    @Column(name = "password", nullable = true)
    private String password;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "last_logged_in_at")
    private LocalDateTime lastLoggedInAt;

    @Column(name = "password_updated_at")
    private LocalDateTime passwordUpdatedAt;

    protected RecordedPlayer() {
    }

    public RecordedPlayer(UUID uuid, String nickname) {
        this.uuid = uuid;
        this.nickname = nickname;
        this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now(ZoneOffset.UTC);

    }

    public void register(PlayerRegisterOptions options) {
        if (isRegistered())
            throw new AlreadyRegisteredException(this);

        this.password = options.password;
        this.registeredAt = LocalDateTime.now(ZoneOffset.UTC);
        this.passwordUpdatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public void updatePassword(String newPassword) throws NotRegisteredYet, IllegalArgumentException {
        if (!isRegistered())
            throw new NotRegisteredYet(this);

        if (newPassword == null || newPassword.isEmpty())
            throw new IllegalArgumentException("New password cannot be empty!");

        this.password = newPassword;
        this.passwordUpdatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }


    public boolean isPasswordCorrect(String inputPassword) throws NotRegisteredYet {
        if (!isRegistered())
            throw new NotRegisteredYet(this);

        boolean isCorrect = this.password.equals(inputPassword);
        if (isCorrect)
            this.lastLoggedInAt = LocalDateTime.now(ZoneOffset.UTC);

        return isCorrect;
    }

    public boolean isRegistered() {
        return this.password != null && !this.password.isEmpty();
    }

    public long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public LocalDateTime getLastLoggedInAt() {
        return lastLoggedInAt;
    }

    public LocalDateTime getPasswordUpdatedAt() {
        return passwordUpdatedAt;
    }
}
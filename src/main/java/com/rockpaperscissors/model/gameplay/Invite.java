package com.rockpaperscissors.model.gameplay;

import com.rockpaperscissors.model.actors.Player;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Invite {

    private Player player;
    private String inviteCode;

    public Invite(Player player) {
        this.player = player;
        this.inviteCode = generateInviteCode();
    }

    private String generateInviteCode() {
        return String.valueOf(player.hashCode() + System.currentTimeMillis());
    }

}

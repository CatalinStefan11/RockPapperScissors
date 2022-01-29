package com.rockpaperscissors.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PlayRequest {

  private String playerName;
  private String inviteCode;
  private String move;

  public PlayRequest(String playerName, String inviteCode, String move) {
    if (playerName == null || inviteCode == null || move == null) {
      throw new IllegalArgumentException(
          "playerName or inviteCode or move must be specified to play your turn");
    }
    this.inviteCode = inviteCode;
    this.playerName = playerName;
    this.move = move;
  }

}

package com.rockpaperscissors.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlayRequest {

  private String playerName;
  private String sessionCode;
  private String move;

  public PlayRequest(String playerName, String sessionCode, String move) {
    if (playerName == null || sessionCode == null || move == null) {
      throw new IllegalArgumentException(
          "PlayerName or sessionCode or move must be specified to play your turn");
    }
    this.sessionCode = sessionCode;
    this.playerName = playerName;
    this.move = move;
  }

}

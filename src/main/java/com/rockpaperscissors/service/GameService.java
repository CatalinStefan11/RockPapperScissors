package com.rockpaperscissors.service;

import com.rockpaperscissors.model.dto.PlayRequest;

public interface GameService {

    void playMove(PlayRequest playRequest);
}

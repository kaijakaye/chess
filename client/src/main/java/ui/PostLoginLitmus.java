package ui;

import chess.ChessGame;

public record PostLoginLitmus(boolean joinedGame, ChessGame.TeamColor teamColor) {
}

package ch.bbw.towerofhanoi.service;

import ch.bbw.towerofhanoi.model.HanoiBoard;
import org.springframework.stereotype.Service;
import ch.bbw.towerofhanoi.model.exception.InvalidMoveException;


@Service
public class HanoiService {
    private final HanoiBoard hanoiBoard;

    public HanoiService(HanoiBoard hanoiBoard) {
        this.hanoiBoard = hanoiBoard;
    }

    public void move(int from, int to) throws InvalidMoveException {
        hanoiBoard.move(new HanoiBoard.Move(HanoiBoard.Peg.values()[from], HanoiBoard.Peg.values()[to]));
    }

    public String getBestMove() {
        return hanoiBoard.calculateBestMove(); //das sötti no implementiere
    }

    public HanoiBoard getGameState() {
        return hanoiBoard;
    }
}

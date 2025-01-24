package ch.bbw.towerofhanoi.service;

import ch.bbw.towerofhanoi.model.HanoiBoard;
import org.springframework.stereotype.Service;
import ch.bbw.towerofhanoi.model.exception.InvalidMoveException;


@Service
public class HanoiService {
    private final HanoiBoard hanoiBoard;

    public HanoiService() {
        this.hanoiBoard = HanoiBoard.initializeWithSize(3);
    }
    public void move(int from, int to) throws InvalidMoveException {
        hanoiBoard.move(new HanoiBoard.Move(HanoiBoard.Peg.values()[from], HanoiBoard.Peg.values()[to]));
    }

    public String getBestMove(int numDisks, int source, int target, int helper) {
        return hanoiBoard.calculateBestMove(numDisks, source, target, helper);
    }
    public HanoiBoard getGameState() {
        return hanoiBoard;
    }
}

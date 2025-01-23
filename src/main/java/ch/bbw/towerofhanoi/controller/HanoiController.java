package ch.bbw.towerofhanoi.controller;

import ch.bbw.towerofhanoi.service.HanoiService;
import ch.bbw.towerofhanoi.model.HanoiBoard;
import org.springframework.beans.factory.annotation.Autowired;
import ch.bbw.towerofhanoi.model.exception.InvalidMoveException;
import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hanoi")
public class HanoiController {
    @GetMapping
    public ResponseEntity<String> handleRoot() {
        return ResponseEntity.ok("Welcome to the Tower of Hanoi API!");
    }
    private final HanoiService hanoiService;

    @Autowired
    public HanoiController(HanoiService hanoiService) {
        this.hanoiService = hanoiService;
    }

    @PostMapping("/move")
    public ResponseEntity<String> makeMove(@RequestParam int from, @RequestParam int to) {
        try {
            hanoiService.move(from, to);
            return ResponseEntity.ok("Move successful.");
        } catch (InvalidMoveException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/bestMove")
    public ResponseEntity<String> getBestMove() {
        String bestMove = hanoiService.getBestMove();
        return ResponseEntity.ok(bestMove);
    }

    @GetMapping("/currentState")
    public ResponseEntity<HanoiBoard> getGameState() {
        return ResponseEntity.ok(hanoiService.getGameState());
    }
}

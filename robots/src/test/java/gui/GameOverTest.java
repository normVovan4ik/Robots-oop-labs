package gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class GameOverTest {

    private static final Confirmer YES = (parent, message, title) -> true;
    private static final Confirmer NO = (parent, message, title) -> false;

    private final KeyboardControls controls = new KeyboardControls();

    @Test
    void gameIsNotOverAtTheStart() {
        assertFalse(new GameModel(2).isGameOver());
    }

    @Test
    void predatorsCatchStandingRobot() {
        GameModel model = new GameModel(2);

        assertTrue(runUntilGameOver(model));
    }

    @Test
    void withoutPredatorsNobodyCatchesRobot() {
        GameModel model = new GameModel(0);

        assertFalse(runUntilGameOver(model));
    }

    @Test
    void modelStopsMovingAfterGameOver() {
        GameModel model = finishedGame();
        double x = model.getRobot().getPositionX();
        double predatorX = model.getPredators().get(0).getPositionX();

        for (int i = 0; i < 100; i++) {
            model.update(10, controls);
        }

        assertEquals(x, model.getRobot().getPositionX(), 1e-9);
        assertEquals(predatorX, model.getPredators().get(0).getPositionX(), 1e-9);
    }

    @Test
    void restartIsNotOfferedWhileGameIsRunning() {
        GameModel model = new GameModel(2);
        List<String> questions = new ArrayList<>();

        boolean restarted = model.askRestart((parent, message, title) -> questions.add(message), null);

        assertFalse(restarted);
        assertTrue(questions.isEmpty());
    }

    @Test
    void confirmedRestartStartsNewGame() {
        GameModel model = finishedGame();

        boolean restarted = model.askRestart(YES, null);

        assertTrue(restarted);
        assertFalse(model.isGameOver());
        assertEquals(100, model.getRobot().getPositionX(), 1e-9);
        assertEquals(100, model.getRobot().getPositionY(), 1e-9);
    }

    @Test
    void declinedRestartKeepsGameOver() {
        GameModel model = finishedGame();

        boolean restarted = model.askRestart(NO, null);

        assertFalse(restarted);
        assertTrue(model.isGameOver());
    }

    @Test
    void restartQuestionAndTitleAreShown() {
        GameModel model = finishedGame();
        List<String> shown = new ArrayList<>();

        model.askRestart((parent, message, title) -> {
            shown.add(message);
            shown.add(title);
            return false;
        }, null);

        assertEquals(List.of("Вас поймали. Начать заново?", "Игра окончена"), shown);
    }

    @Test
    void gameCanBeLostAgainAfterRestart() {
        GameModel model = finishedGame();
        model.askRestart(YES, null);

        assertTrue(runUntilGameOver(model));
    }

    private GameModel finishedGame() {
        GameModel model = new GameModel(2);
        assertTrue(runUntilGameOver(model));
        return model;
    }

    private boolean runUntilGameOver(GameModel model) {
        for (int i = 0; i < 20_000 && !model.isGameOver(); i++) {
            model.update(10, controls);
        }
        return model.isGameOver();
    }
}
package org.example.gamedata;

public class BlockButtons {
    private boolean UP, DOWN, RIGHT, LEFT;

    public BlockButtons() {
        UP = true;
        DOWN = true;
        RIGHT = true;
        LEFT = true;
    }

    /**
     * Check if all the direction buttons are blocked.
     */
    public boolean allButtonsAreBlocked() {
        return !(UP || DOWN || RIGHT || LEFT);
    }

    /**
     * Check particular direction button's state.
     * @param button is one of the buttons that control the direction of the movement.
     * @return current state of the provided button; false if blocked, true otherwise.
     */
    public boolean getState(DirControlButton button) {
        return switch (button) {
            case UP -> UP;
            case DOWN -> DOWN;
            case RIGHT -> RIGHT;
            case LEFT -> LEFT;
        };
    }

    /**
     * Block a particular direction button.
     * @param button is one of the buttons that control the movement direction.
     */
    public void blockButton(DirControlButton button) {
        switch (button) {
            case UP -> UP = false;
            case DOWN -> DOWN = false;
            case RIGHT -> RIGHT = false;
            case LEFT -> LEFT = false;
        }
    }

    /**
     * Block all four direction buttons.
     */
    public void blockAllDirButtons() {
        UP = false;
        DOWN = false;
        RIGHT = false;
        LEFT = false;
    }

    /**
     * Unblock all four direction buttons.
     */
    public void unblockAllDirButtons     () {
        UP = true;
        DOWN = true;
        RIGHT = true;
        LEFT = true;
    }
}

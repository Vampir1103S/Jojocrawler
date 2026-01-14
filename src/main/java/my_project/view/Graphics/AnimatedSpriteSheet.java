package my_project.view.Graphics;

public class AnimatedSpriteSheet extends SpriteSheet{
    private double frameCooldownX;
    private double frameCooldownY;
    private double frameTimerX;
    private double frameTimerY;
    public AnimatedSpriteSheet(String spriteSheet, int rows, int cols) {
        super(spriteSheet, rows, cols);
        frameCooldownX = 0;
        frameCooldownY = 0;
        frameTimerX = 0;
        frameTimerY = 0;
    }
    @Override
    public void update(double dt){
        if (frameCooldownX != 0) {
            frameTimerX += dt;
            if (Math.abs(frameCooldownX) < frameTimerX) {
                frameTimerX %= Math.abs(frameCooldownX); //reset the timer
                if (frameCooldownX > 0) {
                    currentX += 1;
                } else {
                    currentX -= 1;
                }
            }
        }
        if (frameCooldownY != 0) {
            frameTimerY += dt;
            if (Math.abs(frameCooldownY) < frameTimerY) {
                frameTimerY %= Math.abs(frameCooldownY); //reset the timer
                if (frameCooldownY > 0) {
                    currentY += 1;
                } else {
                    currentY -= 1;
                }
            }
        }
        super.update(dt);
    }
    public void setFrameCooldownX(double frameCooldownX) {
        this.frameCooldownX = frameCooldownX;
    }
    public void setFrameCooldownY(double frameCooldownY) {
        this.frameCooldownY = frameCooldownY;
    }
}

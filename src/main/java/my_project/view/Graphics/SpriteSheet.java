package my_project.view.Graphics;

import java.awt.image.BufferedImage;

public class SpriteSheet extends Texture {
    BufferedImage[][] subImages;
    protected int currentX;
    protected int currentY;
    protected double width;
    protected double height;


    /** Creates a SpriteSheet Object
     *
     * @param spriteSheet path to the Imagefile relative from src/main/resources/graphic/
     * @param rows amount of rows in the Sprite Sheet
     * @param cols amount of columns in the Sprite Sheet
     */
    public SpriteSheet(String spriteSheet, int rows, int cols) {
        super(spriteSheet);
        subImages = new BufferedImage[cols][rows];
        currentX = 0;
        currentY = 0;
        width = (double) getMyImage().getWidth() /cols;
        height = (double) getMyImage().getHeight() /rows;


        for (int iX = 0; iX < cols; iX++) {
            for (int iY = 0; iY < rows; iY++) {
                subImages[iX][iY] = getMyImage().getSubimage((int)(iX* width), (int)(iY* height), (int)(width), (int)(height));
            }
        }
        setImageToCurrent();
    }
    @Override
    public void update(double dt){
        super.update(dt);

        setImageToCurrent();

        /**
         * Setzt das aktuell anzuzeigende Bild basierend auf den aktuellen
         * Animationsindizes.
         * <p>
         * Die Indizes werden mithilfe des Modulo-Operators auf die gültigen
         * Grenzen des Sprite-Sheets begrenzt, um Zugriffe außerhalb des
         * Bildarrays zu vermeiden.
         * Anschließend wird das entsprechende Teilbild als aktuelles
         * Anzeigebild gesetzt.
         */
    }
    protected void setImageToCurrent(){
        currentX %= subImages.length;
        currentY %= subImages[0].length;
        setImage(getSubImage(currentX, currentY));
    }

    /**
     * Setzt die aktuellen Indizes des Sprite-Sheets und aktualisiert das Bild.
     * <p>
     * Die übergebenen Werte bestimmen, welches Teilbild aus dem
     * Sprite-Sheet angezeigt wird.
     * Nach dem Setzen der Indizes wird automatisch das
     * entsprechende Bild aktiviert.
     *
     * @param x der Spaltenindex des Teilbildes
     * @param y der Zeilenindex des Teilbildes
     */

    public void setCurrent(int x, int y) {
        currentX = x;
        currentY = y;
        setImageToCurrent();
    }
    public void setCurrentX(int currentX) {
        this.currentX = currentX;
        setImageToCurrent();
    }
    public void setCurrentY(int currentY) {
        this.currentY = currentY;
        setImageToCurrent();
    }
    /**
     * Gibt ein einzelnes Teilbild (Sprite) aus dem Sprite-Sheet zurück.
     * <p>
     * Das Teilbild wird anhand der übergebenen Spalten- und Zeilenindizes
     * aus dem internen Sprite-Array ausgewählt.
     *
     * @param x der Spaltenindex des gewünschten Teilbildes
     * @param y der Zeilenindex des gewünschten Teilbildes
     * @return das ausgewählte {@link BufferedImage} aus dem Sprite-Sheet
     */

    public BufferedImage getSubImage(int x, int y) {
        return subImages[x][y];
    }

    public int getCurrentX() {
        return currentX;
    }
    public int getCurrentY() {
        return currentY;
    }

    public double getSubImgWidth() {
        return width;
    }
    public double getSubImgHeight() {
        return height;
    }
    public int getRows(){
        return subImages[0].length;
    }
    public int getCols(){
        return subImages.length;
    }

    public void setRandom() {
        currentX = (int)(Math.random()*getCols());
        currentY = (int)(Math.random()*getRows());
        setImageToCurrent();
    }
}

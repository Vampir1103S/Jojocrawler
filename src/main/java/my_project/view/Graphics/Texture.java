package my_project.view.Graphics;

import KAGO_framework.model.*;
import KAGO_framework.view.*;
import my_project.view.*;

public class Texture extends GraphicalObject {
    /** Creates a Texture Object
     *
     * @param texture path to the Imagefile relative from src/main/resources/graphic/
     */
    public Texture(String texture) {
        setNewImage("src/main/resources/graphic/" + texture);
    }
    /** create a Texture without Image (Only for easier implementation of Spritesheet)
     */
    protected Texture() {}

    public void draw(DrawTool drawTool, double x, double y, double scale) {
        drawTool.drawTransformedImage(getMyImage(), x, y, 0, scale);
    }
    
    public void drawToWidth(DrawTool drawTool, double x, double y, double width) {
        drawTool.drawTransformedImage(getMyImage(), x, y, 0, width/this.width);
    }

    public double getHeightRelativeToWidth(double width){
        return getMyImage().getHeight()*(width/this.width);
    }

    public double getScaleRelativeToWidth(double width){
            return width/this.width;
    }
}

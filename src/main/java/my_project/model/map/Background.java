package my_project.model.map;

import KAGO_framework.view.DrawTool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Background {

    private BufferedImage image;
    private BufferedImage image2;

    public Background(){
        try {
            image = ImageIO.read(new File("src/main/resources/graphic/Inside-Train.png"));
            image2 = ImageIO.read(new File("src/main/resources/graphic/map101.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(DrawTool drawTool, int s) {
        if (image != null && s == 1) {
            drawTool.drawTransformedImage(image,0,0,0,10);
        }else if(image2 != null && s == 2){
            drawTool.drawTransformedImage(image2,0,0,0,10);
        }
    }

    public void update(){

    }
}

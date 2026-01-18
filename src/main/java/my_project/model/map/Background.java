package my_project.model.map;

import KAGO_framework.view.DrawTool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Background {

    private BufferedImage image1;
    private BufferedImage image2;
    private BufferedImage image3;

    public Background(){
        try {
            image1 = ImageIO.read(new File("src/main/resources/graphic/Bahnhof.png"));
            image2 = ImageIO.read(new File("src/main/resources/graphic/Trainstation.png"));
            image3 = ImageIO.read(new File("src/main/resources/graphic/Inside-Train.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(DrawTool drawTool, int s) {
        if (image1 != null && s == 1) {
            drawTool.drawTransformedImage(image1,0,0,0,1.417);
        }else if(image2 != null && s == 2){
            drawTool.drawTransformedImage(image2,0,0,0,1.417);
        }else if(image3 != null && s == 2){
            drawTool.drawTransformedImage(image3,0,0,0,10);
        }
    }

    public void update(){

    }
}

package byow.UI;

import byow.World.Vector2Int;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

//handy way to draw some ui,since this is not a game with professional ui,
//just use these methods.
//these methods should be called after rendering the tile world(if required)
public class UIDrawer {
   public static final Font FONT_TITLE =new Font("OCR A Extended",Font.PLAIN,50);
   public static final Font FONT_OPTION=new Font("Consolas",Font.BOLD,30);
   public static final Font FONT_HUD=new Font("Consolas",Font.PLAIN,30);

   private static final String TITLE_MAIN="CS61B";
   private static final String TITLE_SECOND="BUILDING YOUR OWN WORLD";
   private static final String TITLE_THIRD="(DEMO)";
   private static final String OPTION_NEW_WORLD="New Game (N)";
   private static final String OPTION_LOAD_WORLD="Load Game (L)";
   private static final String OPTION_QUIT="Quit (Q)";

    private final int width;
    private final int height;
    private final int tileSizeInPixel;

    /**
     * @param canvasSize canvas size in pixel
     */
    public UIDrawer(Vector2Int canvasSize,int tileSizeInPixel){
        this.width=canvasSize.x/tileSizeInPixel;
        this.height=canvasSize.y/tileSizeInPixel;
        this.tileSizeInPixel=tileSizeInPixel;
    }

    public void drawMainMenu(){
        StdDraw.clear(new Color(0, 0, 0));

        Color previousColor=StdDraw.getPenColor();
        Font previousFont=StdDraw.getFont();

        Color penColor=Color.WHITE;
        StdDraw.setPenColor(penColor);
        //hardcode position I don't care
        StdDraw.setFont(FONT_TITLE);
        StdDraw.text((double) width /2,15+ (double) height /2,TITLE_MAIN);
        StdDraw.text((double) width /2,12+ (double) height /2,TITLE_SECOND);
        StdDraw.text((double) width /2,9+ (double) height /2,TITLE_THIRD);
        StdDraw.setFont(FONT_OPTION);
        StdDraw.text((double) width /2,-10+ (double) height /2,OPTION_NEW_WORLD);
        StdDraw.text((double) width /2,-13+ (double) height /2,OPTION_LOAD_WORLD);
        StdDraw.text((double) width /2,-16+ (double) height /2,OPTION_QUIT);

        StdDraw.setPenColor(previousColor);
        StdDraw.setFont(previousFont);
        //show
        StdDraw.show();
    }

    /**
     * draw text in canvas,call this after rendering the world
     * @param position rough position in PIXEL
     */
    public void drawText(Vector2Int position,Font font,Color penColor,String content){
        //not clear
        Color previousColor=StdDraw.getPenColor();
        Font previousFont=StdDraw.getFont();

        StdDraw.setPenColor(penColor);
        StdDraw.setFont(font);
        StdDraw.text((double) position.x /tileSizeInPixel,
                (double) position.y /tileSizeInPixel,content);


        StdDraw.setPenColor(previousColor);
        StdDraw.setFont(previousFont);

        StdDraw.show();
    }
    public void clearCanvas(){
        StdDraw.clear(new Color(0, 0, 0));
    }
}

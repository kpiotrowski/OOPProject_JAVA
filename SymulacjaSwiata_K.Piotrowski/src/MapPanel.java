import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JPanel;
import javax.swing.UIManager;

public class MapPanel extends JPanel implements Runnable {
	
	//Rozmiar mapy
	private final int maxMapX=4000;
	private final int maxMapY=4000;
	
	//Lewy górny róg mapy rzeczywistej(dużej, nie przeskalowanej) od którego rysujemy.
	private int mapStartX=0;
	private int mapStartY=0;
	
	//Wysokosc i szerokość obszaru dużej (nieprzeskalowanej) mapy ktory wyświetlamy na ekranie
	private static int displayMapWidth=0;
	private static int displayMapHeight=4000;
	
	//Punkt kliknięcia na mapie - używanie przy określaniu odległości przeciągnięcia.
	private int mouseClickX=0;
	private int mouseClickY=0;
	
	//ZOOM
	private double mapZOOM=5.6;
	private double maxZOOM=5.6;
	private double minZOOM=1;
	/**
	 * 
	 */
	private static final long serialVersionUID = -7366974390936682417L;
	
	public MapPanel(){
		setBackground(UIManager.getColor("Button.focus"));
		setLayout(null);
		setDoubleBuffered(true);
		
		addMouseWheelListener(new MouseAdapter() {
			public void mouseWheelMoved(MouseWheelEvent e) {
		        float steps = e.getWheelRotation();
		        mapZOOM+=steps/10;
		        if(mapZOOM>maxZOOM) mapZOOM=maxZOOM;
		        if(mapZOOM<minZOOM) mapZOOM=minZOOM;
		      }
		});
		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
			    mouseClickX=e.getX();
			    mouseClickY=e.getY();
			    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			  }
			public void mouseReleased(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			  }
			
		});
		addMouseMotionListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e){
				
				int przesuniecieX=e.getX()-mouseClickX;
				int przesuniecieY=e.getY()-mouseClickY;
				
				mapStartX-=(przesuniecieX/30*mapZOOM);
				mapStartY-=(przesuniecieY/30*mapZOOM);
				
				if(mapStartX<0) mapStartX=0;
				if(mapStartY<0) mapStartY=0;
				if(mapStartX+displayMapWidth>maxMapX){
					mapStartX=maxMapX-displayMapWidth;
				}
				if(mapStartY+displayMapHeight>maxMapY){
					mapStartY=maxMapY-displayMapHeight;
				}
			}
		});
	}

	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int x=this.getWidth();
        int y=this.getHeight();
          
        int width=(int)(x*mapZOOM);
        int height=(int)(y*mapZOOM);
        
        if(width+mapStartX > maxMapX ){
        	mapStartX=maxMapX-width;
        }
        if(height+mapStartY >maxMapY ){
        	mapStartY=maxMapY-height;
        }
        if(mapStartX<0) mapStartX=0;
		if(mapStartY<0) mapStartY=0;
        if(width+mapStartX > maxMapX ){
        	mapZOOM=maxMapX/(float)x;
        }
        if(height+mapStartY >maxMapY ){
        	mapZOOM=maxMapY/(float)y;
        }
    
        displayMapHeight=(int)(y*mapZOOM);
        displayMapWidth=(int)(x*mapZOOM);   
        
        g.drawImage(Swiat.getBufferImage(),
        	       0, 0, x, y,
        	       mapStartX, mapStartY, displayMapWidth+mapStartX, displayMapHeight+mapStartY,
        	       null);
	}
	
	public void run() {
		while(true) {
            try {
            	repaint();
                Thread.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
}

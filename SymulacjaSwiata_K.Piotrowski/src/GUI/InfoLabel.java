package GUI;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;

@SuppressWarnings("serial")
/**
 * 
 * @author Kamil Piotrowski
 * Napis z informacją - o określonym wyglądzie
 */
public class InfoLabel extends JLabel{

	/**
	 * Kontruktor
	 * @param text String - Label jest tworzony z tym tekstem
	 */
	public InfoLabel(String text){
		setForeground(Color.WHITE);
		setText(text);
		this.setPreferredSize( new Dimension(240,21));
	}
}

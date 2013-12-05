package restaurant2;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class IndicatorIcon {

 private JLabel label = new JLabel();
 private ImageIcon icon = new ImageIcon("resources/tux_icon.jpg");
 private String text;
 
 //methods
 IndicatorIcon(ImageIcon image)
 {
	 label.setIcon(image);
 }
 IndicatorIcon(ImageIcon image, String text)
 {
	 label.setIcon(image);
	 label.setText(text);
	 this.text = text;
	 this.icon = image;
 }
 void changeIcon(ImageIcon image)
 {
	 label.setIcon(image);
	 this.icon = image;
 }
 void changeText(String text)
 {
	 label.setText(text);
	 this.text = text;
 }
 void changeBoth(ImageIcon image, String text)
 {
	 label.setIcon(image);
	 label.setText(text);
	 this.icon = image;
	 this.text = text;
 }
 ImageIcon getImageIcon()
 {
	 return (ImageIcon) label.getIcon();
 }
 String getText()
 {
	 return (String) label.getText();
 }
}

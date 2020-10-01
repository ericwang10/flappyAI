package flappybird;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bird {
	int birdnumber =0; 
	BufferedImage preprocessedbird; 
	BufferedImage flappybird; 
	Shape hitbox; 
	int birdx =100 ;
	double birdy =200;
	double velocity;
	double gravity = 0.5;
	int timealive =0; 
	boolean gameover; 
	int tilt; 
	public Bird(){
		preprocessedbird = loadImage("flappy bird.png");
		flappybird = resize(preprocessedbird,50,50);
		hitbox = new Ellipse2D.Float(birdx+9,(int)birdy+9,30,30); //hitbox for flappy bird resized 
		timealive =0;
		gameover = false;
	}
	
	BufferedImage loadImage(String name){
		BufferedImage image = null;
		try{
			image = ImageIO.read(new File(name));
		}catch (IOException ex){
			System.out.print("Failed"+ex);
		}
		return image; 
	}
	
	BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
	public int getTilt(){
		if(velocity>0){
			tilt = 45;
		}else if(velocity<0){
			tilt = -45;
		}else {
			tilt =0;
		}
		if(gameover){
			tilt = 90;
		}
		return tilt;
	}
	public void reset(){
		gameover=false;
		birdy=200;
	}
	public void setBirdY(int y){
		birdy = y;
	}
	public boolean stillAlive(){
		return !gameover;
	}
	public void endGame(){
		gameover = true; 
	}
	public void addTimeAlive(){
		timealive++;
	}
	public int getTimeAlive(){
		return timealive;
	}
	public void birdJump(){
		velocity = -8;
	}
	public void birdMove(){
		birdy+=velocity;
		velocity +=gravity;
	}
	public void updateHitbox(){
		hitbox = new Ellipse2D.Float(birdx+9,(int)birdy+9,30,30);
	}
	public Shape getHitbox(){
		return hitbox;
	}
	public double getVelocity(){
		return velocity; 
	}
	public double getBirdY(){
		return birdy;
	}
	public double getBirdX(){
		return birdx; 
	}
	public BufferedImage getBird(){
		return flappybird; 
	}
}

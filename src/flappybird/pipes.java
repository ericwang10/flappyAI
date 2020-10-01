package flappybird;

import java.awt.Rectangle;

public class pipes extends Rectangle{
	Rectangle rect; 
	int rect1height;
	int bottomrange;
	int rect2y;
	int rect2height; 
	Rectangle rect1;
	Rectangle rect2; 
	int xcoord;
	int pipenumber;
	public pipes(int x){
		xcoord = x; 
	}
	public pipes(){
		xcoord = 500;
	}
	public void createPipes(){
		//top pipe 
		rect1height = (int) (Math.random()*500 + 10); //10 is minimum height 
		bottomrange = 1000 - rect1height - 100; //-100 is minimum space, plus 10 from the top, minimum space is 90 pixels?
		rect2height = (int) (Math.random()*bottomrange+10);
		rect2y = 1000 - rect2height; 

	}
	
	public void createEqualPipes(){
		rect1height = (int) (Math.random()*250 + 10); //10 is minimum height 
		
		rect2y = rect1height + 200; //use this instead of 		rect2y = 1000 - rect1height - 200; to maintain same size gaps
		//idk why the other one doesn't work 
		//this still makes rect2height and rect2y add up to 1000
		rect2height = 515 - rect2y;
	}
	
	public void createEqualNewPipes(){
		xcoord= 1000;
		
		rect1height = (int) (Math.random()*250 + 10); //10 is minimum height 
		rect2y = rect1height + 200; //use this instead of 		rect2y = 1000 - rect1height - 200; to maintain same size gaps
		rect2height = 515 - rect2y;
	}
	public void createNewPipes(){
		//top pipe 
		xcoord = 700;
		
		rect1height = (int) (Math.random()*500 + 10); 
		
		bottomrange = 1000 - rect1height - 100;
		rect2height = (int) (Math.random()*bottomrange+10);
		rect2y = 1000 - rect2height; 

	}
	public void setPipeNumber(int x){
		pipenumber = x;
	}
	public int getPipeNumber(){
		return pipenumber;
	}
	public Rectangle getRect1(){
		rect1 = new Rectangle(xcoord,0,45,rect1height);
		return rect1;
	}
	public Rectangle getRect2(){
		rect2 = new Rectangle(xcoord,rect2y,45, rect2height);
		return rect2; 
	}
	public double getRect1Height(){
		return rect1height;
	}
	public boolean getPast(){
		if (xcoord <=0){
			return true;
		}
		return false; 
	}
	public void moveRects(){
		xcoord-=4;
	}
	
	public int getXcoord(){
		return xcoord; 
	}

}

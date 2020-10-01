/*package flappybird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class board extends JPanel implements KeyListener, ActionListener, MouseListener{

	double gravity = 0.5; 
	int score =0;
	int timealive=0; 
	Timer tm = new Timer(15,this);
	pipes[] pipesystem; 
	int separation=250; 
	int numberofpipes;
	int tilt=0;
	boolean gameover = false; 
	pipes closestpipe= null;
	//flappyAI AI1 = new flappyAI(bird1.getHitbox(), velocity, bird1.getBirdY(), timealive, pipesystem); //initialize ai but 
	//dont think I need to do this here
	
	Bird bird1;
	flappyAI AI1; 
	
	Bird bird2;
	flappyAI AI2;
	
	Bird bird3;
	flappyAI AI3;
	double[][] dist; 
	
	public board(){
		addKeyListener(this);
		setFocusable(true);
		requestFocus();
		addMouseListener(this);
		//bird 1 and corresponding ai 
		bird1 = new Bird(); 
		AI1 = new flappyAI();
		//bird 2
		bird2 = new Bird(); 
		AI2 = new flappyAI();
		//bird 3
		bird3 = new Bird(); 
		AI3 = new flappyAI();
		numberofpipes = 800 / separation;
		pipesystem = new pipes[numberofpipes];
		for (int x=0; x<pipesystem.length; x++){
			pipesystem[x] = new pipes(x*separation+300);
			pipesystem[x].createEqualPipes();
			pipesystem[x].setPipeNumber(x);
		}
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(bird1.preprocessedbird, 200,200,this); //draw flappy bird 
		g2d.setColor(Color.GREEN);
		g2d.fillRect(0, 800, 1000, 200); //filling ground
		
		for(int x=0;x<pipesystem.length;x++){
			g2d.fill(pipesystem[x].getRect1());
			g2d.fill(pipesystem[x].getRect2());
			g2d.setFont(new Font("arial", Font.BOLD,30));
			g2d.drawString("pipe number: " + x, pipesystem[x].getXcoord(),400);
		}
		g2d.setFont(new Font("arial", Font.BOLD,30));
		g2d.drawString(score+"", 900,50);
		if(closestpipe!=null){
			g2d.drawString(closestpipe.getPipeNumber()+"", 900,100);
		}
		//filling hitbox
		g2d.fill(bird1.getHitbox());
		g2d.fill(bird2.getHitbox());
		g2d.fill(bird3.getHitbox());

		
		//rotating bird 1
		AffineTransform at = AffineTransform.getTranslateInstance(bird1.getBirdX(), bird1.getBirdY()); //draw image cannot use double but 
		//affine transform can!!!
		
		at.rotate(Math.toRadians(bird1.getTilt()),bird1.getBird().getWidth()/2, bird1.getBird().getHeight()/2); // getwidth and getheight/2 
		//rotates around center 
		g2d.drawImage(bird1.getBird(), at, null); //this draws the rotated image 
		
		//bird 2
		AffineTransform at2 = AffineTransform.getTranslateInstance(bird1.getBirdX(), bird2.getBirdY()); 
		at2.rotate(Math.toRadians(bird2.getTilt()),bird2.getBird().getWidth()/2, bird2.getBird().getHeight()/2); 
		g2d.drawImage(bird2.getBird(), at2, null);
		
		//bird3
		AffineTransform at3 = AffineTransform.getTranslateInstance(bird1.getBirdX(), bird3.getBirdY()); 
		at3.rotate(Math.toRadians(bird3.getTilt()),bird3.getBird().getWidth()/2, bird3.getBird().getHeight()/2); 
		g2d.drawImage(bird3.getBird(), at2, null);
	}
	public pipes getClosestPipe(){
		int diff =1000;
		pipes closest =null; 
		for (int x=0; x<pipesystem.length;x++){
			if(pipesystem[x].getRect1().getX()-(bird1.getBirdX()+9)<diff){
				diff = (int) (pipesystem[x].getRect1().getX()-(bird1.getBirdX()+9));
				closest =pipesystem[x];
			}
		}
		return closest; 
	}
	public double[][] closestPipeDistance(Bird bird){
		int toprectx, toprecty;
		int botrectx, botrecty;
		Rectangle toprect, botrect;
		double[][] distance = new double[2][2]; //first array will store top rectangle, second bottom
		//first data entry will be x difference, 2nd data entry will be y diff 
		if(closestpipe !=null){
			toprect = closestpipe.getRect1();
			botrect = closestpipe.getRect2();
			//hitbox need to add width and
			//hitbox coords are upper left 
			toprectx = (int) (toprect.getX()-(bird.getBirdX()+30+9)); //need to add width and adjustment of hitbox 
			//since coords are top left, add width and then add adjustment of the hitbox i believe... 
			toprecty = (int) (toprect.getY()-(bird.getBirdY()+(9+30)/2)); //divide by half right now just to get midpoint
			//otherwise its not going to work since y distance will change based on where hitbox is relative to pipes
			//eg below vs above a pipe 
			
			botrectx = (int) (botrect.getX() - (bird.getBirdX()+30+9));
			botrecty = (int) (botrect.getY() - (bird.getBirdY()+(9+30)/2)); // add height and adjustment 
			//fixed bug, before i was dividing entire bird.getbirdy by half when it should be just 39/2

			distance[0][0] = toprectx;
			distance[0][1] = toprecty;
			distance[1][0] = botrectx;
			distance[1][1] = botrecty;
		}
		return distance;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
	    if (key == KeyEvent.VK_ENTER) {
	        tm.start(); //start timer in paint component... could also start it in key listener
	        //tm.stop();
	    }
	    if(!gameover){
		    if (key == KeyEvent.VK_SPACE){
		    	bird1.birdJump(); // when flapping, must set velocity equal to something otherwise it won't work and it won't have 
		    	//the curve, can't use -= because it just stays like that I think, also no "bounce" in the flapping 
		    }
	    }
	    if(dist!=null){
		    System.out.println("distance from top rect: x = " + dist[0][0] + " y = " +dist[0][1]);
		    System.out.println("distance from bot rect: x = " + dist[1][0] + " y = " +dist[1][1]);
	    }
	    if (key == KeyEvent.VK_E){
	    	tm.stop();
	    }
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		//cant put tm.start here or else it doesnt work 

		pipe1.moveRects();
		pipe2.moveRects();
		if(pipe1.getXcoord()<=0){
			pipe1.createPipes();
		}
		if(bird1.getBirdY()<770){ //make sure this is set to the same coords as the y coord when the bird dies or else it will glitch
			bird1.birdMove(); //y coordinates increase by velocity 
			bird1.updateHitbox();
		}
		else if(bird1.getBirdY()>=770){ //make sure all of these y coordinates are equal 
			gameover = true;
			bird1.birdy=770; //can't make this less than 800 for the upper if statement or else its going to keep glitching around
		}
		if(!gameover){
			bird1.addTimeAlive();
			movePipeSystem(pipesystem);
			for(int x=0; x<pipesystem.length;x++){
				if(hitbox.intersects(pipesystem[x].getRect1())|| hitbox.intersects(pipesystem[x].getRect2())){
					System.out.println("GAME OVER");
					gameover = true; 
				}
				if(pipesystem[x].getPast()){
					pipesystem[x].createEqualNewPipes();
					score++;
				}
				
			}
		}
		closestpipe = getClosestPipe();
	    dist = closestPipeDistance(bird1);
		AI1.update(bird1.getHitbox(), bird1.getVelocity(),bird1.getBirdY(), bird1.getTimeAlive(), dist); //must intialize
		//this IN CONSTRUCTOR OR ELSE ITS GOING TO RETURN NULL POINTER EXCEPTION EVEN IF NONE OF THESE ARE NULL
		if(AI1.jump()){
			bird1.birdJump();
		}
		repaint();
	}
	public void gameMechanics(Bird bird){
		if(bird.getBirdY()<770){ //make sure this is set to the same coords as the y coord when the bird dies or else it will glitch
			bird.birdMove(); //y coordinates increase by velocity 
			bird.updateHitbox();
		}
		else if(bird.getBirdY()>=770){ //make sure all of these y coordinates are equal 
			gameover = true;
			bird.setBirdY(770); //can't make this less than 800 for the upper if statement or else its going to keep glitching around
		}
		if(!gameover){
			bird.addTimeAlive();
			//movePipeSystem(pipesystem);
			for(int x=0; x<pipesystem.length;x++){
				if(hitbox.intersects(pipesystem[x].getRect1())|| hitbox.intersects(pipesystem[x].getRect2())){
					System.out.println("GAME OVER");
					gameover = true; 
				}
				if(pipesystem[x].getPast()){
					pipesystem[x].createEqualNewPipes();
					score++;
				}
				
			}
		}
	}
	public void movePipeSystem(pipes[] array){
		for (int x=0; x<array.length;x++){
			array[x].moveRects();
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("X coord = " + e.getX() + " Y Coord = " +e.getY());
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
pipe1 = new pipes(); 
pipe1.createEqualPipes();
pipe2 = new pipes(300);
pipe2.createPipes();

//testing subpixel stuff
//AffineTransform t = new AffineTransform();
//t.translate(0, birdy);
//g2d.drawImage(birdresized,t,null);
		AffineTransform t1 = new AffineTransform();
t1.translate(102, 100.5);
g2d.drawImage(birdresized,t1,null);
g2d.drawImage(birdresized, 500,500,this); //draw resized flappy bird 
g2d.drawImage(birdresized, 501,500,this); //draw resized flappy bird 

*/























/*package flappybird;

import java.awt.Shape;

public class flappyAI implements Runnable{
	Shape hitbox;
	double velocity, birdy;
	int score;
	double[][] closestdist; 
	double toprectx, toprecty;
	double botrectx, botrecty; 
	double pipesrating;
	double jumpdesire; 
	
	double toprectdelta;
	double toprectrating;
	
	double botrectdelta;
	double botrectrating;
	
	double velocityrating;
	double velocitydelta;
	double distanceaway;
	double distancerating;
	double distancedelta;
	double learningrate=0.1; 
	double threshold; 
	public flappyAI(double newtoprectdelta, double newbotrectdelta, double newvelocitydelta, double newdistancedelta, double newthreshold){
		toprectdelta = newtoprectdelta + newtoprectdelta*Math.random()*2-1;
		botrectdelta = newbotrectdelta + newbotrectdelta*Math.random()*2-1;
		velocitydelta = newvelocitydelta + newvelocitydelta*Math.random()*2-1;
		distancedelta = newdistancedelta + newdistancedelta*Math.random()*2-1;
		threshold = newthreshold + newthreshold*Math.random()*2-1;
	}
	public flappyAI(){
		toprectdelta = Math.random()+1;
		botrectdelta = Math.random()+1;
		velocitydelta = Math.random()*5+1;
		distancedelta = Math.random()*300+1000;
		threshold = Math.random()*100+800;
	}
	public void update(){
		
	}
	public void update(Shape hbox, double velo, double birdycoords, int timealive, double[][] distances){
		hitbox =hbox;
		velocity = velo;
		birdy = birdycoords;
		score = timealive;
		closestdist = distances; 
		toprectx = distances[0][0];
		toprecty=distances[0][1];
		botrectx= distances[1][0];
		botrecty = distances[1][1];
		distanceaway = toprectx;
	}
	public void update(double velo, int timealive){
		velocity = velo;
		score = timealive;
	}
	public double getThreshold(){
		return threshold;
	}
	public double[] releaseData(){
		double[] data = new double[4];
		data[0] = toprectdelta;
		data[1] = botrectdelta;
		data[2] = velocitydelta;
		data[3] = distancedelta; 
		return data;
	}
	public boolean jump(){
		toprectrating = toprecty * toprectdelta;
		botrectrating = botrecty * botrectdelta; 

		velocityrating = velocity*velocitydelta; 
		//higher velocity makes you want to jump to cancel it out 

		distancerating = (1/distanceaway)*distancedelta;
		//higher distance means longer time to decide, shorter distance while greater pipe difference means need to jump more
		
		jumpdesire = pipesrating + velocityrating + distancerating; 
		//System.out.println("rect diff " + (toprecty - botrecty));
		//System.out.println("pipes " +pipesrating);
		//System.out.println("delta " +pipesdelta);
		//System.out.println(distancerating +"distance rating");
		
		if(jumpdesire>threshold){
			return true;
		}
		else {
			return false;
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			while(true){
				jump();
				Thread.sleep(10);
				//System.out.println("THREAD RUNNING");
			}
		}catch(Exception ex){
			System.out.println(ex);
		}
	}
	
}
pipesrating = -(toprecty - botrecty) *pipesdelta; //higher it is, the more you want to jump, lower it is, the less you want to jump
//close to 0 also don't want to jump 
//edit : it seems like negative means worse and the more closer to 0 it is the better
//thus adding minus sign 
*/

























/*


package flappybird;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class board extends JPanel implements KeyListener, ActionListener, MouseListener{

	double gravity = 0.5; 
	int score =0;
	int timealive=0; 
	Timer tm = new Timer(15,this);
	pipes[] pipesystem; 
	int separation=250; 
	int numberofpipes;
	int tilt=0;
	boolean gameover = false; 
	pipes closestpipe= null;
	//flappyAI AI1 = new flappyAI(bird1.getHitbox(), velocity, bird1.getBirdY(), timealive, pipesystem); //initialize ai but 
	//dont think I need to do this here
	
	Bird bird1;
	flappyAI AI1; 
	
	Bird bird2;
	flappyAI AI2;
	
	Bird bird3;
	flappyAI AI3;
	
	Bird bird4;
	flappyAI AI4;
	
	Bird bird5;
	flappyAI AI5;
	
	Bird bird6;
	flappyAI AI6;
		
	Bird[] nest; 
	flappyAI[] nestAI; 
	AffineTransform[] transformers;
	
	int noBirds= 20;
	double[][] dist; 
	
	boolean gamewideover=false;
	
	Thread[] t;
	public board(){
		addKeyListener(this);
		setFocusable(true);
		requestFocus();
		addMouseListener(this);
		nest = new Bird[noBirds];
		nestAI = new flappyAI[noBirds];
		transformers = new AffineTransform[noBirds];
		for(int x=0; x<nest.length;x++){
			nest[x] = new Bird();
			nestAI[x] = new flappyAI();
		}
		//bird 1 and corresponding ai 
		bird1 = new Bird(); 
		AI1 = new flappyAI();
		//bird 2
		bird2 = new Bird(); 
		AI2 = new flappyAI();
		//bird 3
		bird3 = new Bird(); 
		AI3 = new flappyAI();
		//bird4
		bird4 = new Bird(); 
		AI4 = new flappyAI();
		//bird5
		bird5 = new Bird(); 
		AI5 = new flappyAI();
		//bird6
		bird6 = new Bird(); 
		AI6 = new flappyAI();
		
		numberofpipes = 800 / separation;
		pipesystem = new pipes[numberofpipes];
		for (int x=0; x<pipesystem.length; x++){
			pipesystem[x] = new pipes(x*separation+300);
			pipesystem[x].createEqualPipes();
			pipesystem[x].setPipeNumber(x);
		}
		
		t = new Thread[noBirds];
		for(int x=0; x<nest.length;x++){
			t[x] = new Thread(nestAI[x]);
			t[x].start();
		}
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.GREEN);
		g2d.fillRect(0, 800, 1000, 200); //filling ground
		
		for(int x=0;x<pipesystem.length;x++){
			g2d.fill(pipesystem[x].getRect1());
			g2d.fill(pipesystem[x].getRect2());
			g2d.setFont(new Font("arial", Font.BOLD,30));
			g2d.drawString("pipe number: " + x, pipesystem[x].getXcoord(),400);
		}
		g2d.setFont(new Font("arial", Font.BOLD,30));
		g2d.drawString(score+"", 900,50);
		if(closestpipe!=null){
			g2d.drawString(closestpipe.getPipeNumber()+"", 900,100);
		}
		//filling hitbox
		for(int x=0; x<nest.length;x++){
			g2d.fill(nest[x].getHitbox());
		}
		g2d.fill(bird1.getHitbox());
		g2d.fill(bird2.getHitbox());
		g2d.fill(bird3.getHitbox());
		g2d.fill(bird4.getHitbox());
		g2d.fill(bird5.getHitbox());
		g2d.fill(bird6.getHitbox());
		// rotating birds
		for(int x=0; x<nest.length;x++){
			transformers[x] = AffineTransform.getTranslateInstance(nest[x].getBirdX(), nest[x].getBirdY());
			transformers[x].rotate(Math.toRadians(nest[x].getTilt()),nest[x].getBird().getWidth()/2, nest[x].getBird().getHeight()/2); // getwidth and getheight/2 
			g2d.drawImage(nest[x].getBird(), transformers[x], null); //this draws the rotated image 
		}
		//rotating bird 1
		AffineTransform at = AffineTransform.getTranslateInstance(bird1.getBirdX(), bird1.getBirdY()); //draw image cannot use double but 
		//affine transform can!!!
		
		at.rotate(Math.toRadians(bird1.getTilt()),bird1.getBird().getWidth()/2, bird1.getBird().getHeight()/2); // getwidth and getheight/2 
		//rotates around center 
		g2d.drawImage(bird1.getBird(), at, null); //this draws the rotated image 
		
		//bird 2
		AffineTransform at2 = AffineTransform.getTranslateInstance(bird2.getBirdX(), bird2.getBirdY()); 
		at2.rotate(Math.toRadians(bird2.getTilt()),bird2.getBird().getWidth()/2, bird2.getBird().getHeight()/2); 
		g2d.drawImage(bird2.getBird(), at2, null);
		
		//bird3
		AffineTransform at3 = AffineTransform.getTranslateInstance(bird3.getBirdX(), bird3.getBirdY()); 
		at3.rotate(Math.toRadians(bird3.getTilt()),bird3.getBird().getWidth()/2, bird3.getBird().getHeight()/2); 
		g2d.drawImage(bird3.getBird(), at3, null);
		
		//bird
	}
	public pipes getClosestPipe(){
		int diff =1000;
		pipes closest =null; 
		for (int x=0; x<pipesystem.length;x++){
			if(pipesystem[x].getRect1().getX()-(nest[0].getBirdX()+9)<diff){ //using a bird obj although I think x just stays const
				diff = (int) (pipesystem[x].getRect1().getX()-(nest[0].getBirdX()+9));
				closest =pipesystem[x];
			}
		}
		return closest; 
	}
	public double[][] closestPipeDistance(Bird bird){
		int toprectx, toprecty;
		int botrectx, botrecty;
		Rectangle toprect, botrect;
		double[][] distance = new double[2][2]; //first array will store top rectangle, second bottom
		//first data entry will be x difference, 2nd data entry will be y diff 
		if(closestpipe !=null){
			toprect = closestpipe.getRect1();
			botrect = closestpipe.getRect2();
			//hitbox need to add width and
			//hitbox coords are upper left 
			toprectx = (int) (toprect.getX()+toprect.getHeight()-(bird.getBirdX()+30+9)); //need to add width and adjustment of hitbox 
			//since coords are top left, add width and then add adjustment of the hitbox i believe... 
			toprecty = (int) (toprect.getY()-(bird.getBirdY()+(9+30)/2)); //divide by half right now just to get midpoint
			//otherwise its not going to work since y distance will change based on where hitbox is relative to pipes
			//eg below vs above a pipe 
			
			botrectx = (int) (botrect.getX() - (bird.getBirdX()+30+9));
			botrecty = (int) (botrect.getY() - (bird.getBirdY()+(9+30)/2)); // add height and adjustment 
			//fixed bug, before i was dividing entire bird.getbirdy by half when it should be just 39/2

			distance[0][0] = toprectx;
			distance[0][1] = toprecty;
			distance[1][0] = botrectx;
			distance[1][1] = botrecty;
		}
		return distance;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		int key = e.getKeyCode();
	    if (key == KeyEvent.VK_ENTER) {
	        tm.start(); //start timer in paint component... could also start it in key listener
	        //tm.stop();
	    }
	    if(!gameover){
		    if (key == KeyEvent.VK_SPACE){
		    	//bird1.birdJump(); // when flapping, must set velocity equal to something otherwise it won't work and it won't have 
		    	//the curve, can't use -= because it just stays like that I think, also no "bounce" in the flapping 
		    }
	    }
	    if(dist!=null){
		    System.out.println("distance from top rect: x = " + dist[0][0] + " y = " +dist[0][1]);
		    System.out.println("distance from bot rect: x = " + dist[1][0] + " y = " +dist[1][1]);
	    }
	    if (key == KeyEvent.VK_E){
	    	tm.stop();
	    }
	}
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		//cant put tm.start here or else it doesnt work 

		pipe1.moveRects();
		pipe2.moveRects();
		if(pipe1.getXcoord()<=0){
			pipe1.createPipes();
		}
		for(int x=0; x<nest.length;x++){
			gameMechanics(nest[x]);
		}
		for(int x=0; x<nest.length;x++){
			if(nest[x].stillAlive()){
				movePipeSystem(pipesystem);
				for(int y=0; y<pipesystem.length;y++){
					if(pipesystem[y].getPast()){
						pipesystem[y].createEqualNewPipes();
						score++;
					}
				}
				break;
			}
			if(x==nest.length-1 && !nest[x].stillAlive()){
				gamewideover = true;
			}
		}
		
		closestpipe = getClosestPipe();
	    dist = closestPipeDistance(nest[0]);
	    for(int x=0; x<nest.length;x++){
			nestAI[x].update(nest[x].getHitbox(), nest[x].getVelocity(),nest[x].getBirdY(), nest[x].getTimeAlive(), closestPipeDistance(nest[x])); //must intialize
	    }
		AI1.update(bird1.getHitbox(), bird1.getVelocity(),bird1.getBirdY(), bird1.getTimeAlive(), dist); //must intialize
		//this IN CONSTRUCTOR OR ELSE ITS GOING TO RETURN NULL POINTER EXCEPTION EVEN IF NONE OF THESE ARE NULL
		AI2.update(bird2.getHitbox(), bird2.getVelocity(),bird2.getBirdY(), bird2.getTimeAlive(), closestPipeDistance(bird2));
		AI3.update(bird3.getHitbox(), bird3.getVelocity(),bird3.getBirdY(), bird3.getTimeAlive(), closestPipeDistance(bird3));
		for(int x=0;x<nest.length;x++){
			if(nestAI[x].jump()&&nest[x].stillAlive()){
				nest[x].birdJump();
			}
		}
	    if(AI1.jump()&&bird1.stillAlive()){
			bird1.birdJump();
		}
		if(AI2.jump()&& bird2.stillAlive()){
			bird2.birdJump();
		}
		if(AI3.jump() && bird3.stillAlive()){
			bird3.birdJump();
		}
		
		if(gamewideover){
			//bird 1 and corresponding ai 
			score =0;
			int max= 0;
			flappyAI fittest = null;
			double thresholdfit;
			double[] data; 
			for(int x=0; x<nest.length;x++){
				if(max<nest[x].getTimeAlive()){
					max = nest[x].getTimeAlive();
					fittest = nestAI[x];
				}
			}
			data = fittest.releaseData();
			thresholdfit = fittest.getThreshold();
			for(int x=0; x<nest.length;x++){
				nest[x] = new Bird();
				nest[x].reset();
				nestAI[x] = new flappyAI(data[0],data[1],data[2],data[3],thresholdfit);
			}
			for(int x=0; x<=5; x++){
				nestAI[x] = new flappyAI();
			}
			for (int x=0; x<pipesystem.length; x++){
				pipesystem[x] = new pipes(x*separation+300);
				pipesystem[x].createEqualPipes();
				pipesystem[x].setPipeNumber(x);
			}
			gamewideover=false;
		}
			
		repaint();
	}
	public void gameMechanics(Bird bird){
		if(bird.getBirdY()<770){ //make sure this is set to the same coords as the y coord when the bird dies or else it will glitch
			bird.birdMove(); //y coordinates increase by velocity 
			bird.updateHitbox();
		}
		else if(bird.getBirdY()>=770){ //make sure all of these y coordinates are equal 
			bird.endGame();
			bird.setBirdY(770); //can't make this less than 800 for the upper if statement or else its going to keep glitching around
		}
		else if (bird.getBirdY()< 50){
			bird.endGame();
		}
		for(int x=0; x<pipesystem.length;x++){
			if(bird.getHitbox().intersects(pipesystem[x].getRect1())|| bird.getHitbox().intersects(pipesystem[x].getRect2())){
				System.out.println("GAME OVER");
				bird.endGame(); 
			}
		}
		if(!gameover){
			bird.addTimeAlive();
			//movePipeSystem(pipesystem);
		}
	}
	public void movePipeSystem(pipes[] array){
		for (int x=0; x<array.length;x++){
			array[x].moveRects();
		}
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("X coord = " + e.getX() + " Y Coord = " +e.getY());
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
pipe1 = new pipes(); 
pipe1.createEqualPipes();
pipe2 = new pipes(300);
pipe2.createPipes();

//testing subpixel stuff
//AffineTransform t = new AffineTransform();
//t.translate(0, birdy);
//g2d.drawImage(birdresized,t,null);
		AffineTransform t1 = new AffineTransform();
t1.translate(102, 100.5);
g2d.drawImage(birdresized,t1,null);
g2d.drawImage(birdresized, 500,500,this); //draw resized flappy bird 
g2d.drawImage(birdresized, 501,500,this); //draw resized flappy bird 

if(bird1.stillAlive()||bird2.stillAlive()||bird3.stillAlive()){
	movePipeSystem(pipesystem);
	for(int x=0; x<pipesystem.length;x++){
		
		if(pipesystem[x].getPast()){
			pipesystem[x].createEqualNewPipes();
			score++;
		}
		
	}
}else{
	gamewideover = true;
}*/









/*





package flappybird;

import java.awt.Shape;

public class flappyAI implements Runnable{
	Shape hitbox;
	double velocity, birdy;
	int score;
	double[][] closestdist; 
	double toprectx, toprecty;
	double botrectx, botrecty; 
	double jumpdesire; 
	
	double toprectyrating;
	double botrectyrating;
	double botrectydelta;
	double toprectydelta; 
	
	
	double velocityrating;
	double velocitydelta;
	double distanceaway;
	double distancerating;
	double distancedelta;
	double learningrate=0.6; 
	double threshold; 
	double bottomrating;
	double bottomdelta;
	double bottom;
	
	public flappyAI(double newtoprectydelta, double newbotrectydelta, double newvelocitydelta, double newdistancedelta,
			double bottomdelta, double newthreshold){
		toprectydelta = newtoprectydelta + newtoprectydelta *(Math.random()*2-1)*learningrate;
		botrectydelta = newbotrectydelta +newbotrectydelta*(Math.random()*2-1) *learningrate;
		velocitydelta = newvelocitydelta + newvelocitydelta*(Math.random()*2-1)*learningrate;
		distancedelta = newdistancedelta + newdistancedelta*(Math.random()*2-1)*learningrate;
		threshold = newthreshold + newthreshold*Math.random()*2-1;
	}
	public flappyAI(){
		toprectydelta = Math.random()+1;
		botrectydelta = Math.random()+1.8;
		velocitydelta = Math.random()*5+1;
		distancedelta = Math.random()*300+1000;
		threshold = Math.random()*100+1100;
		bottomdelta = Math.random()*300+1000;
	}
	public void update(){
		
	}
	public void update(Shape hbox, double velo, double birdycoords, int timealive, double[][] distances){
		hitbox =hbox;
		velocity = velo;
		birdy = birdycoords;
		score = timealive;
		closestdist = distances; 
		toprectx = distances[0][0];
		toprecty=distances[0][1];
		botrectx= distances[1][0];
		botrecty = distances[1][1];
		distanceaway = toprectx;
	}
	public void update(double velo, int timealive){
		velocity = velo;
		score = timealive;
	}
	public double getThreshold(){
		return threshold;
	}
	public double[] releaseData(){
		double[] data = new double[5];
		data[0] = toprectydelta;
		data[1] = botrectydelta;
		data[2] = velocitydelta;
		data[3] = distancedelta; 
		data[4] = bottomdelta;
		return data;
	}
	public double getCloseness(){
		double closeness = toprecty+botrecty;
		return Math.abs(closeness); 
	}
	public boolean jump(){
		toprectyrating = toprecty*toprectydelta;
		botrectyrating = -botrecty*botrectydelta;
		velocityrating = velocity*velocitydelta; 
		//higher velocity makes you want to jump to cancel it out 

		distancerating = Math.sqrt(distanceaway/200) *distancedelta;
		//System.out.println(distanceaway/200);
		//higher distance means longer time to decide, shorter distance while greater pipe difference means need to jump more
		//changed from 1/x to divide by 200 and sqrt function 
		
		bottomrating = Math.sqrt((600- birdy)/600)*bottomdelta;
		jumpdesire = toprectyrating + botrectyrating + velocityrating + distancerating; 
		//System.out.println("rect diff " + (toprecty - botrecty));
		//System.out.println("pipes " +pipesrating);
		//System.out.println("delta " +pipesdelta);
		//System.out.println(distancerating +"distance rating");
		
		if(jumpdesire>threshold){
			return true;
		}
		else {
			return false;
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			while(true){
				jump();
				Thread.sleep(3);
				//System.out.println("THREAD RUNNING");
			}
		}catch(Exception ex){
			System.out.println(ex);
		}
	}
	
}
pipesrating = -(toprecty - botrecty) *pipesdelta; //higher it is, the more you want to jump, lower it is, the less you want to jump
//close to 0 also don't want to jump 
//edit : it seems like negative means worse and the more closer to 0 it is the better
//thus adding minus sign 
*/












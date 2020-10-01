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
	pipes[] pipesystem; //pipes array 
	int separation=200; //separation between pipes 
	int numberofpipes; //the amount of pipes you want 
	int tilt=0; //this might be outdated 
	boolean gameover = false; 
	pipes closestpipe= null;

		
	Bird[] nest; 
	flappyAI[] nestAI; 
	AffineTransform[] transformers;
	
	int noBirds= 5;
	int nextgenerationnumber = 10;
	double[][] dist; 
	
	boolean gamewideover=false;
	
	Thread[] t;
	
	int maxscore = 0;
	int generation =0;
	int startlength = 500; 
	
	BufferedImage background; 
	BufferedImage resizedbackground;
	
	BufferedImage[][] pipeimages;
	BufferedImage pipeimage;
	
	public board(){
		addKeyListener(this);
		setFocusable(true);
		requestFocus();
		addMouseListener(this);
		nest = new Bird[noBirds]; //add array of birds 
		nestAI = new flappyAI[noBirds]; //add same length array for flappy bird ai
		transformers = new AffineTransform[noBirds]; //add same length array for tilt 
		for(int x=0; x<nest.length;x++){ //initialize the various things
			nest[x] = new Bird(); 
			nestAI[x] = new flappyAI();
		}
		numberofpipes = 1000 / separation; //number of pipes will equal 1000 free space / separation between pipes 
		pipesystem = new pipes[numberofpipes]; //initialize pipesystem 
		for (int x=0; x<pipesystem.length; x++){ //create the pipes
			pipesystem[x] = new pipes(x*separation+startlength); //call constructor
			pipesystem[x].createEqualPipes(); //create these pipes 
			pipesystem[x].setPipeNumber(x); //number the pipes
		}
		
		t = new Thread[noBirds]; //make new thread equal to array of birds length 
		for(int x=0; x<nest.length;x++){
			t[x] = new Thread(nestAI[x]); //initialize the threads 
			t[x].start();
		}
		pipeimages = new BufferedImage[numberofpipes][2];//wip for drawing actual pipes 
		background = loadImage("flappy background.png"); //initialize the background 
		resizedbackground = resize(background,900,600); //resize the background 
		pipeimage = loadImage("flappy pipes.png"); //load the pipe image WIP 
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.GRAY);
		g2d.fillRect(0, 500, 1000, 500); //filling ground 
		g2d.setColor(new Color(75,158,72));
		
		g2d.drawImage(resizedbackground,0,0,this); //flappy background, resized 

		for(int x=0;x<pipesystem.length;x++){
			g2d.fill(pipesystem[x].getRect1()); //filling the pipes 
			g2d.fill(pipesystem[x].getRect2());
			
			//resizing and adding the tubes
/*			pipeimages[x][0] = resize(pipeimage,(int)pipesystem[x].getRect1().getWidth(),(int)pipesystem[x].getRect1().getHeight());
			pipeimages[x][1] = resize(pipeimage,(int)pipesystem[x].getRect2().getWidth(),(int)pipesystem[x].getRect2().getHeight());

			g2d.drawImage(pipeimages[x][0],(int)pipesystem[x].getRect1().getX(),(int)pipesystem[x].getRect1().getY(),null);
			g2d.drawImage(pipeimages[x][1],(int)pipesystem[x].getRect2().getX(),(int)pipesystem[x].getRect2().getY(),null);*/

			g2d.setFont(new Font("arial", Font.BOLD,30));
			//g2d.drawString("pipe number: " + x, pipesystem[x].getXcoord(),400);
		}
		g2d.setFont(new Font("arial", Font.BOLD,30)); //making font 
		g2d.drawString(score+"", 820,50); //displaying score 
		if(closestpipe!=null){ //if closest pipe is not null
			g2d.drawString(closestpipe.getPipeNumber()+"", 850,100); //display closest pipe number 
		}
		g2d.drawString("Max Score: "+ maxscore, 10, 50); //for HUD 
		g2d.drawString("Generation: "+generation, 10, 100);
		//filling hitbox
		for(int x=0; x<nest.length;x++){
			if (!nest[x].stillAlive()){ //if bird is dead, make hitbox red 
				g2d.setColor(Color.RED);
			}
			g2d.fill(nest[x].getHitbox()); //fill hitbox 
			g2d.setColor(Color.GREEN); //reset color 

		}
		
		// rotating birds
		for(int x=0; x<nest.length;x++){
			transformers[x] = AffineTransform.getTranslateInstance(nest[x].getBirdX(), nest[x].getBirdY()); //this gets the oords
			transformers[x].rotate(Math.toRadians(nest[x].getTilt()),nest[x].getBird().getWidth()/2, nest[x].getBird().getHeight()/2); // getwidth and getheight/2 
			//^ this rotates image 
			g2d.drawImage(nest[x].getBird(), transformers[x], null); //this draws the rotated image 
		}

	}
	public pipes getClosestPipe(){
		int diff =1000; //arbitrary large difference 
		pipes closest =null; //closest pipe is first null 
		for (int x=0; x<pipesystem.length;x++){
			if(pipesystem[x].getRect1().getX()-(nest[0].getBirdX()+9)<diff && pipesystem[x].getRect1().getX()>70){ //using a bird obj although I think x just stays const
				//if pipesystem pipe - bird x coord is less than the previous one, take new one as minimum 
				// added another and conditional to make closest pipe appear faster
				//this is because the most recent pipe will pass the bird but still be closer until it reappears farther down the
				//level. By doing this and checking rectangles only over 100 in x coord, then we can ignore past pipes
				//EDIT since width of pipe is 30? or 25, birds seem to be crashing into the top of the pipe if it is set to 100
				//this is probs because they don't care about it once it passes 100, even though they can still get hit by the 
				//flat of the pipe
				//so I'm subtracting 30 to make it 70 to see how it is
				diff = (int) (pipesystem[x].getRect1().getX()-(nest[0].getBirdX()+9)); //record this new value 
				closest =pipesystem[x]; //set closest pipe as corresponding pipe 
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
		if(closestpipe !=null){ //if closest pipe is not null 
			toprect = closestpipe.getRect1(); //get the rectangles 
			botrect = closestpipe.getRect2();
			//hitbox need to add width and
			//hitbox coords are upper left 
			toprectx = (int) (toprect.getX()-(bird.getBirdX()+30+9)); //need to add width and adjustment of hitbox 
			//since coords are top left, add width and then add adjustment of the hitbox i believe... 
			toprecty = (int) (toprect.getY()+toprect.getHeight()-(bird.getBirdY()+(9+30)/2)); //divide by half right now just to get midpoint
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
	    if(closestpipe!=null){
	    	System.out.println("PIPE DIFFERENCE = "+pipeheightdiff(closestpipe));
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

		for(int x=0; x<nest.length;x++){
			gameMechanics(nest[x]); //run game mechanics for each bird 
		}
		for(int x=0; x<nest.length;x++){
			if(nest[x].stillAlive()){ //if bird is still alive 
				movePipeSystem(pipesystem); //move the rectangles 
				for(int y=0; y<pipesystem.length;y++){
					if(pipesystem[y].getPast()){ //if pipes are past, make new ones 
						pipesystem[y].createEqualNewPipes();
						score++; //add 1 to score 
					}
				}
				break; //we can break here since at least one is alive 
			}
			if(x==nest.length-1 && !nest[x].stillAlive()){ //if no one is alive 
				gamewideover = true; //make game for everyone over 
			}
		}
		
		closestpipe = getClosestPipe(); //store closest pipe 
	    dist = closestPipeDistance(nest[0]); //take distance of closestpipe 
	    for(int x=0; x<nest.length;x++){ //update all the bird ai's with necessary info from each corresponding bird 
			nestAI[x].update(nest[x].getHitbox(), nest[x].getVelocity(),nest[x].getBirdY(), nest[x].getTimeAlive(), closestPipeDistance(nest[x])); //must intialize
	    }
		/*AI1.update(bird1.getHitbox(), bird1.getVelocity(),bird1.getBirdY(), bird1.getTimeAlive(), dist); //must intialize
		//this IN CONSTRUCTOR OR ELSE ITS GOING TO RETURN NULL POINTER EXCEPTION EVEN IF NONE OF THESE ARE NULL
		AI2.update(bird2.getHitbox(), bird2.getVelocity(),bird2.getBirdY(), bird2.getTimeAlive(), closestPipeDistance(bird2));
		AI3.update(bird3.getHitbox(), bird3.getVelocity(),bird3.getBirdY(), bird3.getTimeAlive(), closestPipeDistance(bird3));*/
		for(int x=0;x<nest.length;x++){ //see if each bird can jump 
			if(nestAI[x].jump()&&nest[x].stillAlive()){ //if ai wants to jump
				nest[x].birdJump(); //make bird jump 
			}
		}

		if(gamewideover){ //this is the evolution logic
			//bird 1 and corresponding ai 
			if(score>maxscore){ //just for HUD 
				maxscore = score;
			}
			generation++; //just for HUD 
			score =0; //for HUD
			int max= 0; //arbitrary max value as 0 
			flappyAI fittest = null; //make one ai the fittest 
			double thresholdfit; //also corresponding threshold for that AI
			double closeness =1000; //arbitrary value for closeness - referring to how close it is to the gap or inbetween 2 pipes
			//lower the better 
			double[] data; //data that is to be released for the fittest bird 
			for(int x=0; x<nest.length;x++){ //run through each bird arry 
				if(max<nest[x].getTimeAlive() && closeness>nestAI[x].getCloseness()){ //get closeness and max alive value
					//fittest bird needs to be closest to the pipe and also the longest alive 
					max = nest[x].getTimeAlive(); //set that max value to the greatest 
					closeness = nestAI[x].getCloseness(); //set that value for the closeness 
					fittest = nestAI[x]; //set ai to be fittest 
				}
			}
			data = fittest.releaseData(); //at the end of the loop, release the data of the fittest bird 
			thresholdfit = fittest.getThreshold();
/*			nest = new Bird[nextgenerationnumber];
			nestAI = new flappyAI[nextgenerationnumber];*/
			for(int x=0; x<nest.length;x++){ //initialize new birds 
				nest[x] = new Bird(); //make new birds... (i dont know why it kept showing birdy =470 then...
				nest[x].reset(); //reset birds just incase... 
				nestAI[x] = new flappyAI(data[0],data[1],data[2],data[3], data[4],thresholdfit); //give them this data 
			}
			for(int x=0; x<4; x++){
				nestAI[x] = new flappyAI(); //also make some new ai that is independent of the previous gen
			}
			nestAI[0] = fittest; //make the fittest one still survive or else all the progress will be lost 
			for (int x=0; x<pipesystem.length; x++){
				pipesystem[x] = new pipes(x*separation+startlength); //redo pipes again 
				pipesystem[x].createEqualPipes();
				pipesystem[x].setPipeNumber(x);
			}
			gamewideover=false; //set game to false and go on with the next generation 
		}
			
		repaint();
	}
	public double pipeheightdiff(pipes pipe){
		pipes nextpipe;
		int pipenumber; 
		double diff; 
		if(pipe.getPipeNumber()<3){
			pipenumber = pipe.getPipeNumber();
			nextpipe = pipesystem[pipenumber+1];
			diff = nextpipe.getRect2().getY()-pipe.getRect2().getY();
		}else{
			nextpipe = pipesystem[0];
			diff = nextpipe.getRect2().getY()-pipe.getRect2().getY();
		}
		return diff;
	}
	public void gameMechanics(Bird bird){ //mechanis of game 
		if(bird.getBirdY()<470 && bird.getBirdY()>0){ //make sure this is set to the same coords as the y coord when the bird dies or else it will glitch
			bird.birdMove(); //y coordinates increase by velocity 
			bird.updateHitbox();
			//if birdY is inbetween coordinates, then it can move 
		}
		else if(bird.getBirdY()>=470){ //make sure all of these y coordinates are equal 
			bird.endGame();
			bird.setBirdY(470); //can't make this less than 800 for the upper if statement or else its going to keep glitching around
			//if it is on the ground, set it to always be on the ground 
		}
		else if (bird.getBirdY()< 0){ //if it flies too high, destroy it 
			bird.endGame();
			bird.birdMove(); //move it to the ground?
		}
		for(int x=0; x<pipesystem.length;x++){ //if it crashes into a pipe, destroy it 
			if(bird.getHitbox().intersects(pipesystem[x].getRect1())|| bird.getHitbox().intersects(pipesystem[x].getRect2())){
				System.out.println("GAME OVER");
				bird.endGame(); 
			}
		}
		if(bird.stillAlive()){
			bird.addTimeAlive(); //add time alive for the AI metric 
			//movePipeSystem(pipesystem);
		}
	}
	public void movePipeSystem(pipes[] array){
		for (int x=0; x<array.length;x++){
			array[x].moveRects(); //move pipes 
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
}
/*pipe1 = new pipes(); 
pipe1.createEqualPipes();
pipe2 = new pipes(300);
pipe2.createPipes();*/

//testing subpixel stuff
//AffineTransform t = new AffineTransform();
//t.translate(0, birdy);
//g2d.drawImage(birdresized,t,null);
/*		AffineTransform t1 = new AffineTransform();
t1.translate(102, 100.5);
g2d.drawImage(birdresized,t1,null);
g2d.drawImage(birdresized, 500,500,this); //draw resized flappy bird 
g2d.drawImage(birdresized, 501,500,this); //draw resized flappy bird 
*/
/*if(bird1.stillAlive()||bird2.stillAlive()||bird3.stillAlive()){
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
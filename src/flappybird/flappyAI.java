package flappybird;

import java.awt.Shape;

public class flappyAI implements Runnable{
	Shape hitbox;
	double velocity, birdy; //data that ai can  see 
	int score;
	double[][] closestdist; 
	double toprectx, toprecty;
	double botrectx, botrecty; 
	double jumpdesire; 
	
	double toprectyrating; //bird choice weightings 
	double botrectyrating;
	double botrectydelta;
	double toprectydelta; 
	
	
	double velocityrating;
	double velocitydelta;
	double distanceaway;
	double distancerating;
	double distancedelta;
	double learningrate=0.07; 
	double threshold; 
	
	double bottomrating;
	double bottom;
	double bottomdelta;
	public flappyAI(double newtoprectydelta, double newbotrectydelta, double newvelocitydelta, double newdistancedelta, 
			double newbottomdelta ,double newthreshold){ //constructor to get genes from fitter bird 
		toprectydelta = newtoprectydelta + newtoprectydelta *(Math.random()*2-1)*learningrate; //modify each delta a bit 
		botrectydelta = newbotrectydelta +newbotrectydelta*(Math.random()*2-1) *learningrate;
		velocitydelta = newvelocitydelta + newvelocitydelta*(Math.random()*2-1)*learningrate;
		distancedelta = newdistancedelta + newdistancedelta*(Math.random()*2-1)*learningrate;
		threshold = newthreshold + newthreshold*Math.random()*2-1;
	}
	public flappyAI(){ //make a random delta for a new AI 
		toprectydelta = Math.random()*100; //tweak these values 
		botrectydelta = Math.random()*100;
		velocitydelta = Math.random()*100;
		distancedelta = Math.random()*900;
		bottomdelta = Math.random()*500;
		threshold = Math.random()*1400;
		
		//new metric maybe height of second pipe relative to first?
		//make it a function of the botrect rating?
		// so multiply it by botrect rating
		//the lower you are, the more you want to jump, amplified espiecially if the 2nd pipe is also higher 
	}
	public void update(){
		
	}
	public void update(Shape hbox, double velo, double birdycoords, int timealive, double[][] distances){ 
		//constant stream of data that is updated so ai can make a choice 
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
	public double getThreshold(){ //get threshold 
		return threshold;
	}
	public double[] releaseData(){ //release data 
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
		toprectyrating = -toprecty*toprectydelta; 
		//negative value, want to jump more the more negative it is
		//if it is positive it means bird is on top, (i believe), makes bird want to jump less 
		botrectyrating = -botrecty*botrectydelta;
		//same as above 
		velocityrating = velocity*velocitydelta; 
		//higher velocity makes you want to jump to cancel it out 

		//distancerating = Math.sqrt(distanceaway/200) *distancedelta;
		distancerating = 1/(distanceaway-1) * distancedelta;
		//System.out.println(distanceaway/200);
		//higher distance means longer time to decide, shorter distance while greater pipe difference means need to jump more
		//changed from 1/x to divide by 200 and sqrt function 
		
		//bottomrating = Math.sqrt((500-birdy)/500)*bottomdelta;
		bottom = 600-birdy;
		bottomrating = (1/(bottom-1))*bottomdelta * 1; //since bottomrating is small might have to multiply 
		//use 1/x but transform it so a value of 0 won't destroy the program 
		//higher values means that bottom rating will be lower
		//this is because the higher the bird from the bottom, the less it needs to jump
		//vice versa for lower 
		
		//System.out.println(toprectyrating + " Threshold = "+ threshold);
		jumpdesire = toprectyrating + botrectyrating + velocityrating + distancerating; 
		//add all of these weights together to get singular jump desire value 
		
		//System.out.println("rect diff " + (toprecty - botrecty));
		//System.out.println("pipes " +pipesrating);
		//System.out.println("delta " +pipesdelta);
		//System.out.println(distancerating +"distance rating");
		
		//AUG 25 EDIT
		//seems like I dont even need the other variables other than toprect and botrect
		
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
				Thread.sleep(4);
				//System.out.println("THREAD RUNNING");
			}
		}catch(Exception ex){
			System.out.println(ex);
		}
	}
	
}
/*pipesrating = -(toprecty - botrecty) *pipesdelta; //higher it is, the more you want to jump, lower it is, the less you want to jump
//close to 0 also don't want to jump 
//edit : it seems like negative means worse and the more closer to 0 it is the better
//thus adding minus sign 
*/
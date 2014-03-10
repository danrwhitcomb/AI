package assignment_robots;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.Polygon;
import javafx.scene.Group;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.ArrayList;

import assignment_robots.ProbRoadmapPlanner.Configuration;
import assignment_robots.SearchProblem.SearchNode;

public class ArmDriver extends Application {
	// default window size
	protected int window_width = 600;
	protected int window_height = 400;
	public Group group;
	public Stage stage;
	public Scene sc;
	public int nColor;
	
	public void addPolygon(Group g, Double[] points) {
		Polygon p = new Polygon();
	    p.getPoints().addAll(points);
	    
	    g.getChildren().add(p);
	}
	
	// plot a ArmRobot;
	public void plotArmRobot(Group g, ArmRobot arm, double[] config) {
		arm.set(config);
		double[][] current;
		Double[] to_add;
		Polygon p;
		for (int i = 1; i <= arm.getLinks(); i++) {
			current = arm.getLinkBox(i);
			
			
			to_add = new Double[2*current.length];
			for (int j = 0; j < current.length; j++) {
				System.out.println(current[j][0] + ", " + current[j][1]);
				to_add[2*j] = current[j][0];
				//to_add[2*j+1] = current[j][1];
				to_add[2*j+1] = window_height - current[j][1];
			}
			p = new Polygon();
			p.getPoints().addAll(to_add);
			p.setStroke(Color.BLUE);
			Color blue = Color.ANTIQUEWHITE;
			
			for(int v = 0; v < nColor ; v++){
				blue = blue.darker();
			}
			
			
			p.setFill(blue);
			g.getChildren().add(p);
		}
		
	}
	
	public void plotWorld(Group g, World w) {
		int len = w.getNumOfObstacles();
		double[][] current;
		Double[] to_add;
		Polygon p;
		for (int i = 0; i < len; i++) {
			current = w.getObstacle(i);
			to_add = new Double[2*current.length];
			for (int j = 0; j < current.length; j++) {
				to_add[2*j] = current[j][0];
				//to_add[2*j+1] = current[j][1];
				to_add[2*j+1] = window_height - current[j][1];
			}
			p = new Polygon();
			p.getPoints().addAll(to_add);
			g.getChildren().add(p);
		}
	}
	
	// The start function; will call the drawing;
	// You can run your PRM or RRT to find the path; 
	// call them in start; then plot the entire path using
	// interfaces provided;
	@Override
	public void start(Stage primaryStage) {
		
		
		// setting up javafx graphics environments;
		primaryStage.setTitle("CS 76 2D world");

		Group root = new Group();
		Scene scene = new Scene(root, window_width, window_height);

		primaryStage.setScene(scene);
		
		Group g = new Group();
		group = g;
		sc = scene;
		stage = primaryStage;

		// setting up the world;
		
		// creating polygon as obstacles;
	
		double ab[][] = {{10, 400}, {40, 400}, {40, 200}, {10, 200}};
		Poly obstacle4 = new Poly(ab);
		
		double d[][] = {{100, 400}, {130, 400}, {130, 200}, {100, 200}};
		Poly obstacle5 = new Poly(d);
		
		double a[][] = {{285, 400}, {315, 400}, {315, 200}, {285, 200}};
		Poly obstacle1 = new Poly(a);
		
		double b[][] = {{0, 150}, {600, 150}, {600, 0}, {0, 0}};

		Poly obstacle2 = new Poly(b);
	
		// Declaring a world; 
		World w = new World();
		// Add obstacles to the world;
		w.addObstacle(obstacle1);
		w.addObstacle(obstacle2);
		w.addObstacle(obstacle4);
		w.addObstacle(obstacle5);
		//w.addObstacle(obstacle3);
		System.out.println("Plotted obstacles");

		
		plotWorld(g, w);
		System.out.println("Plotted world");

		
		ArmRobot arm = new ArmRobot(2);
		ArmLocalPlanner ap = new ArmLocalPlanner();
		double[] config1 = {10, 20, 80, Math.PI/4, 80, Math.PI/4};
		double[] config2 = {500, 300, 80, .1, 80, .2};
		
		arm.set(config2);

		System.out.println("setup start and end nodes");

		
		// Plan path between two configurations;
		ProbRoadmapPlanner prm = new ProbRoadmapPlanner(window_width, window_height, w, this);
		
		System.out.println("Searching for path...");
		ArmRobot robot = new ArmRobot(2);
		robot.set(config1);
		LinkedList<SearchNode> path = prm.prm(robot, config1, config2);
		
		
		/*
		System.out.println("Found path!");
		plotArmRobot(g, arm, config1);
		SearchNode prevNode = prm.new Configuration(config1);
		
		for(SearchNode n : path){
			
			
			double[] midPath = ap.getPath(((Configuration)prevNode).config, ((Configuration)n).config);
			double time = ap.moveInParallel(((Configuration)prevNode).config, ((Configuration)n).config);
			double[] config = new double[((Configuration)prevNode).config.length];
			double step = 0.5;
			for (int i = 0; i < ((Configuration)prevNode).config.length; i++) {
				config[i] = ((Configuration)prevNode).config[i];
			}
			double current = 0;
			
			if(!w.armCollisionPath(robot, ((Configuration)prevNode).config, ((Configuration)n).config)){
				while (current < time) {
					for (int i = 0; i < ((Configuration)prevNode).config.length; i++) {
						config[i] = config[i] + midPath[i] * step;
					}
					nColor++;
					
					plotArmRobot(g, arm, config);
					
					current = current + step;
				}
			} else {
				while (current < time) {
					for (int i = 0; i < ((Configuration)prevNode).config.length; i++) {
						config[i] = config[i] - midPath[i] * step;
					}
					nColor++;
					
					plotArmRobot(g, arm, config);
					
					current = current + step;
				}
			}
			
			
		
			nColor++;
			plotArmRobot(g, arm, ((Configuration)n).config);   
			prevNode = n;
		}
		 
		
	    scene.setRoot(g);
	    primaryStage.show();
	    */
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
}

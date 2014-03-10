package assignment_mazeworld;

import java.util.ArrayList;

import java.util.List;
import java.util.LinkedList;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import assignment_mazeworld.SearchProblem.SearchNode;
import assignment_mazeworld.RobotMazeProblem.RobotMazeNode;

public class RobotMazeDriver extends Application {

	Maze maze;
	
	// instance variables used for graphical display
	private static final int PIXELS_PER_SQUARE = 32;
	MazeView mazeView;
	List<AnimationPath> animationPathList;
	
	// some basic initialization of the graphics; needs to be done before 
	//  runSearches, so that the mazeView is available
	private void initMazeView() {
		maze = Maze.readFromFile("aroundTown.maz");
		
		animationPathList = new ArrayList<AnimationPath>();
		// build the board
		mazeView = new MazeView(maze, PIXELS_PER_SQUARE);
		
	}
	
	// assumes maze and mazeView instance variables are already available
	private void runSearches() {
		
		/*
		int sx = 0;
		int sy = 0;
		int gx = 6;
		int gy = 0;

		SimpleMazeProblem mazeProblem = new SimpleMazeProblem(maze, sx, sy, gx,
				gy);

		List<SearchNode> bfsPath = mazeProblem.breadthFirstSearch();
		animationPathList.add(new AnimationPath(mazeView, bfsPath));
		System.out.println("DFS:  ");
		mazeProblem.printStats();

		List<SearchNode> dfsPath = mazeProblem
				.depthFirstPathCheckingSearch(5000);
		animationPathList.add(new AnimationPath(mazeView, dfsPath));
		System.out.println("BFS:  ");
		mazeProblem.printStats();

		List<SearchNode> astarPath = mazeProblem.astarSearch();
		animationPathList.add(new AnimationPath(mazeView, astarPath));
		System.out.println("A*:  ");
		mazeProblem.printStats();
		*/
		int[][] startLocations = {{0, 0}, {1, 0}, {2, 0}};
		int[][] goalLocations = {{2, 2}, {0, 2}, {1, 2}};
		
		RobotMazeProblem rProblem = new RobotMazeProblem(maze, startLocations, goalLocations);
		LinkedList<SearchNode> roboPath = rProblem.astarSearch();
		animationPathList.add(new AnimationPath(mazeView, roboPath));
		System.out.println("roboA*Star");
		System.out.println(roboPath.size());
		rProblem.printStats();
	
	}


	public static void main(String[] args) {
		launch(args);
	}

	// javafx setup of main view window for mazeworld
	@Override
	public void start(Stage primaryStage) {
		
		initMazeView();
	
		primaryStage.setTitle("CS 76 Mazeworld");

		// add everything to a root stackpane, and then to the main window
		StackPane root = new StackPane();
		root.getChildren().add(mazeView);
		primaryStage.setScene(new Scene(root));

		primaryStage.show();

		// do the real work of the driver; run search tests
		runSearches();

		// sets mazeworld's game loop (a javafx Timeline)
		Timeline timeline = new Timeline(1.0);
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(.05), new GameHandler()));
		timeline.playFromStart();

	}

	// every frame, this method gets called and tries to do the next move
	//  for each animationPath.
	private class GameHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {
			// System.out.println("timer fired");
			for (AnimationPath animationPath : animationPathList) {
				// note:  animationPath.doNextMove() does nothing if the
				//  previous animation is not complete.  If previous is complete,
				//  then a new animation of a piece is started.
				animationPath.doNextMove();
			}
		}
	}

	// each animation path needs to keep track of some information:
	// the underlying search path, the "piece" object used for animation,
	// etc.
	private class AnimationPath {
		private ArrayList<Node> pieces;
		private LinkedList<SearchNode> searchPath;
		private int currentMove = 0;

		private int[] lastX;
		private int[] lastY;

		boolean animationDone = true;

		public AnimationPath(MazeView mazeView, LinkedList<SearchNode> path) {
			searchPath = path;
			RobotMazeNode firstNode = (RobotMazeNode) searchPath.poll();
			int numRobots = firstNode.getLocations().length;
			int[][]locations = firstNode.getLocations();
			pieces = new ArrayList<Node>();
			lastX = new int[locations.length];
			lastY = new int[locations.length];
			
			for(int i=0; i < numRobots; i++){
				Node node = mazeView.addPiece(locations[i][0], locations[i][1]);
				lastX[i] = locations[i][0];
				lastY[i] = locations[i][1];
				pieces.add(node);
			}
			
			
			
		}

		// try to do the next step of the animation. Do nothing if
		// the mazeView is not ready for another step.
		public void doNextMove() {

			// animationDone is an instance variable that is updated
			//  using a callback triggered when the current animation
			//  is complete
			if (searchPath.size() != 0 && animationDone) {
				RobotMazeNode mazeNode = (RobotMazeNode) searchPath.poll();
				int dx = mazeNode.getX() - lastX[mazeNode.roboThatMoved()];
				int dy = mazeNode.getY() - lastY[mazeNode.roboThatMoved()];
				System.out.println("animating " + dx + " " + dy);
				int curRobo = mazeNode.roboThatMoved();
				animateMove(pieces.get(curRobo), dx, dy);
				lastX[mazeNode.roboThatMoved()] = mazeNode.getX();
				lastY[mazeNode.roboThatMoved()] = mazeNode.getY();

				currentMove++;
			}
		}

		// move the piece n by dx, dy cells
		public void animateMove(Node n, int dx, int dy) {
			animationDone = false;
			TranslateTransition tt = new TranslateTransition(
					Duration.millis(300), n);
			tt.setByX(PIXELS_PER_SQUARE * dx);
			tt.setByY(-PIXELS_PER_SQUARE * dy);
			// set a callback to trigger when animation is finished
			tt.setOnFinished(new AnimationFinished());

			tt.play();

		}

		// when the animation is finished, set an instance variable flag
		//  that is used to see if the path is ready for the next step in the
		//  animation
		private class AnimationFinished implements EventHandler<ActionEvent> {
			@Override
			public void handle(ActionEvent event) {
				animationDone = true;
			}
		}
	}
}
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
import assignment_mazeworld.SimpleMazeProblem.SimpleMazeNode;
import assignment_mazeworld.RobotMazeProblem;
import assignment_mazeworld.BlindRobotProblem.*;

public class BlindRobotDriver extends Application {

	Maze maze;
	
	// instance variables used for graphical display
	private static final int PIXELS_PER_SQUARE = 32;
	MazeView mazeView;
	List<AnimationPath> animationPathList;
	
	// some basic initialization of the graphics; needs to be done before 
	//  runSearches, so that the mazeView is available
	private void initMazeView() {
		maze = Maze.readFromFile("simple.maz");
		
		animationPathList = new ArrayList<AnimationPath>();
		// build the board
		mazeView = new MazeView(maze, PIXELS_PER_SQUARE);
		
	}
	
	// assumes maze and mazeView instance variables are already available
	private void runSearches() {
		BlindRobotProblem bProblem = new BlindRobotProblem(0, 0, 1, 2, maze);
		LinkedList<SearchNode> bPath = bProblem.astarSearch();
		animationPathList.add(new AnimationPath(mazeView, bPath));
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
				new KeyFrame(Duration.seconds(1), new GameHandler()));
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
		private Node piece;
		private List<SearchNode> searchPath;
		private int currentMove = 0;

		private int lastX;
		private int lastY;

		boolean animationDone = true;

		public AnimationPath(MazeView mazeView, List<SearchNode> path) {
			searchPath = path;
			BlindRobotNode firstNode = (BlindRobotNode) searchPath.get(0);
			
			for(int i = 0; i < maze.height; i++){
				for(int v = 0; v < maze.width; v++){
					if(!firstNode.grid[i][v]){
						mazeView.addSquare(v, i, "red");
					}	
				}
			}
		}

		// try to do the next step of the animation. Do nothing if
		// the mazeView is not ready for another step.
		public void doNextMove() {

			// animationDone is an instance variable that is updated
			//  using a callback triggered when the current animation
			//  is complete
			if (searchPath.size() != 0 && animationDone) {
				BlindRobotNode mazeNode = (BlindRobotNode) searchPath
						.get(currentMove);
				for(int i = 0; i < maze.height; i++){
					for(int v = 0; v < maze.width; v++){
						if(!mazeNode.grid[i][v]){
							mazeView.addSquare(v, i, "red");
						} else {
							mazeView.addSquare(v, i, "white");
						}
					}
				}
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
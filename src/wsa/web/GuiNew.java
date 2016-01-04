package wsa.web;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GuiNew extends Application{
	
	public void start(Stage primaryStage) {
        Scene scene = new Scene(createUI(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("WSA");
        primaryStage.show();
	}
	
    private Parent createUI() {

    	
    	Pane top = new Pane();
    	Pane right = new Pane();
    	Pane left = new Pane();
    	Pane bottom = new Pane();
    	
    	// LEFT ---
        Rectangle temp1 = new Rectangle(-100, 100, 60, 60);
        setRectAnimation(temp1, Color.YELLOW);
        setRectMouseSpec(temp1, Color.DARKGOLDENROD, Color.YELLOW );
        temp1.setStroke(Color.BLUEVIOLET);
        temp1.setStrokeWidth(10);
        left.getChildren().add(temp1);
        
        Rectangle temp2 = new Rectangle(-100, 200, 60, 60);
        setRectAnimation(temp2, Color.RED);
        setRectMouseSpec(temp2, Color.DARKRED, Color.RED);
        temp2.setStroke(Color.ORANGE);
        temp2.setStrokeWidth(10);
        left.getChildren().add(temp2);
        
        Rectangle temp3 = new Rectangle(-100, 300, 60, 60);
        setRectAnimation(temp3, Color.GREEN);
        setRectMouseSpec(temp3, Color.DARKGREEN, Color.GREEN);
        temp3.setStroke(Color.YELLOW);
        temp3.setStrokeWidth(10);
        left.getChildren().add(temp3);
        
        Rectangle temp4 = new Rectangle(-100, 400, 60, 60);
        setRectAnimation(temp4, Color.CORAL);
        setRectMouseSpec(temp4, Color.DARKGOLDENROD, Color.CORAL);
        temp4.setStroke(Color.AQUAMARINE);
        temp4.setStrokeWidth(10);
        left.getChildren().add(temp4);
        
        left.setPrefWidth(100);
        left.setStyle("-fx-background-color: black");
        
        
    	
        // --------
        
    	// TOP ---
        Text addUriText = new Text("WEB SITE ANALYZER");
        addUriText.setFont(new Font(addUriText.getText(), 50));
        addUriText.setFill(Color.YELLOW);
        addUriText.setStroke(Color.GREEN);
        addUriText.setStrokeWidth(2);
        addUriText.setTextAlignment(TextAlignment.CENTER);
        StackPane sp = new StackPane(addUriText);
        sp.setAlignment(Pos.TOP_CENTER);
        
        top.getChildren().add(sp);
        top.setStyle("-fx-background-color: black");
        
    	// --------------------
        
        // RIGHT ---
    	right.setPrefWidth(100);
    	
    	/**
        Text addUriText = new Text("Add Uris");
        addUriText.setFont(new Font(addUriText.getText(), 20));
        addUriText.setFill(Color.YELLOW);
        addUriText.setStroke(Color.GREEN);
        addUriText.setStrokeWidth(2);
        StackPane sp = new StackPane(addUriText);
        **/
        Rectangle addUris = new Rectangle(100, 100, 60, 60);
        setRectAnimation(addUris, Color.YELLOW);
        setRectMouseSpec(addUris, Color.DARKGOLDENROD, Color.YELLOW );
        addUris.setStroke(Color.BLUEVIOLET);
        addUris.setStrokeWidth(10);
        /**
        HBox hb = new HBox(addUriText, addUris);
        hb.setAlignment(Pos.CENTER);
        hb.setSpacing(10);
        **/
        right.getChildren().add(addUris);
        
        
        Rectangle save = new Rectangle(100, 200, 60, 60);
        setRectAnimation(save, Color.RED);
        setRectMouseSpec(save, Color.DARKRED, Color.RED);
        save.setStroke(Color.ORANGE);
        save.setStrokeWidth(10);
        right.getChildren().add(save);
        
        Rectangle go = new Rectangle(100, 300, 60, 60);
        setRectAnimation(go, Color.GREEN);
        setRectMouseSpec(go, Color.DARKGREEN, Color.GREEN);
        go.setStroke(Color.YELLOW);
        go.setStrokeWidth(10);
        right.getChildren().add(go);
        
        Rectangle pause = new Rectangle(100, 400, 60, 60);
        setRectAnimation(pause, Color.CORAL);
        setRectMouseSpec(pause, Color.DARKGOLDENROD, Color.CORAL);
        pause.setStroke(Color.AQUAMARINE);
        pause.setStrokeWidth(10);
        right.getChildren().add(pause);
    	
        right.setStyle("-fx-background-color: darkturquoise");
    	
    	BorderPane borderPane = new BorderPane();
    	borderPane.setTop(top);
    	borderPane.setRight(right);
    	borderPane.setLeft(left);
    	borderPane.setBottom(bottom);
    	borderPane.setStyle("-fx-background-color: mediumslateblue");
    	

    	
    	return borderPane;
	}
    
    private void setRectAnimation(Rectangle rect, Paint color) {
        rect.setArcHeight(15);
        rect.setArcWidth(15);
        rect.setFill(color);
        //rectParallel.setTranslateX(50);
        //rectParallel.setTranslateY(75);
        	RotateTransition rotateTransition = 
                new RotateTransition(Duration.millis(800), rect);
            rotateTransition.setByAngle(180f);
            rotateTransition.setCycleCount(2);
            rotateTransition.setAutoReverse(true);
            final Timeline timeline = new Timeline();
            //timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.setAutoReverse(false);
            final KeyValue kv = new KeyValue(rect.xProperty(), 20);
            final KeyFrame kf = new KeyFrame(Duration.millis(1500), kv);
            timeline.getKeyFrames().add(kf);
            ParallelTransition parallelTransition = new ParallelTransition();
            parallelTransition.getChildren().addAll(
                    rotateTransition,
                    timeline
            );
            parallelTransition.setCycleCount(1);
            parallelTransition.play();
    }
    
    private void setRectMouseSpec(Rectangle rect, Paint pressedColor, Paint releasedColor) {
    	rect.setOnMousePressed( (e) -> {
    		rect.setFill(pressedColor);
    		RotateTransition rotateTransition = 
                    new RotateTransition(Duration.millis(800), rect);
                rotateTransition.setByAngle(180f);
                rotateTransition.setCycleCount(1);
                rotateTransition.play();
    	});
    	rect.setOnMouseReleased( (e) -> {
        	rect.setFill(releasedColor);
        });
    }
    
	public static void main(String[] args) {
        launch(args);
    }  
}

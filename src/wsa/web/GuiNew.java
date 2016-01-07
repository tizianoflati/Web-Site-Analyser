package wsa.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GuiNew extends Application{
	List<String> uris = new ArrayList<>();
	String[] colorList = {"chartreuse", "coral", "deeppink", "lightgreen",
			"gold", "mediumturquoise", "orangered", "snow",
			"slateblue" };
	
	public void start(Stage primaryStage) {
        Scene scene = new Scene(createUI(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("WSA");
        primaryStage.show();
	}
	
    private Parent createUI() {
    	
    	// TOP ---
        
        Text addUriText = new Text("WEB SITE ANALYZER");
        addUriText.setFont(new Font(addUriText.getText(), 50));
        addUriText.setFill(Color.YELLOW);
        addUriText.setStroke(Color.GREEN);
        addUriText.setStrokeWidth(2);
        addUriText.setTextAlignment(TextAlignment.CENTER);
        VBox vbTop = new VBox(addUriText);
        vbTop.setAlignment(Pos.TOP_CENTER);
        vbTop.setStyle("-fx-background-color: black"); 
        
    	// --------------------
        
        
        // RIGHT ---
        
        Button addUrisB = new Button("ADD URI");
        //addUrisB.setPrefWidth(50);
        addUrisB.setPrefHeight(50);
        addUrisB.setMaxWidth(70);
        Rectangle r1 = new Rectangle(20, 100, 80, 60);
        r1.setStroke(Color.BLUEVIOLET);
        r1.setStrokeWidth(10);
        setRectAnimation(r1, Color.YELLOW);       
        setRectMouseSpec(addUrisB, r1);
        StackPane sp1 = new StackPane(r1, addUrisB);

        
        Rectangle r2 = new Rectangle(20, 200, 60, 60);
        r2.setStroke(Color.ORANGE);
        r2.setStrokeWidth(10);
        setRectAnimation(r2, Color.RED);
        Button saveB = new Button("SAVE");
        saveB.setPrefWidth(50);
        saveB.setPrefHeight(50);
        setRectMouseSpec(saveB, r2);
        StackPane sp2 = new StackPane(r2, saveB);
        
        
        Rectangle r3 = new Rectangle(20, 300, 60, 60);
        r3.setStroke(Color.YELLOW);
        r3.setStrokeWidth(10);
        Button goB = new Button("GO!!");
        goB.setPrefWidth(50);
        goB.setPrefHeight(50);
        setRectAnimation(r3, Color.GREEN);
        setRectMouseSpec(goB, r3);
        StackPane sp3 = new StackPane(r3, goB);
        
        
        Rectangle r4 = new Rectangle(100, 400, 70, 70);
        r4.setStroke(Color.AQUAMARINE);
        r4.setStrokeWidth(10);
        Button pauseB = new Button("PAUSE");
        pauseB.setPrefWidth(60);
        pauseB.setPrefHeight(60);
        setRectAnimation(r4, Color.CORAL);
        setRectMouseSpec(pauseB, r4);
        StackPane sp4 = new StackPane(r4, pauseB);
        
        // END RIGHT ------------------------------------
        
        // LEFT -----------------------------------------
        
        VBox vbLeft = new VBox();
        vbLeft.setSpacing(20);
        vbLeft.setAlignment(Pos.CENTER);
        vbLeft.setStyle("-fx-background-color: black");
        
        
        // CENTER -------------------------------------
        
        // Campo dominio
    	Text dominioText = new Text("DOMINIO ->");
        TextField dominio = new TextField();
        dominioText.setFont(new Font(dominioText.getText(), 20));
        dominioText.setFill(Color.YELLOW);
        dominioText.setStroke(Color.BLACK);
        dominioText.setStrokeWidth(1);
        
        // Campo primo uri
    	Text uriText = new Text("URI ->");
        TextField uri = new TextField();
        uriText.setFont(new Font(uriText.getText(), 20));
        uriText.setFill(Color.YELLOW);
        uriText.setStroke(Color.BLACK);
        uriText.setStrokeWidth(1);
        
        HBox hbDominio = new HBox(dominioText, dominio);
        HBox.setHgrow(dominio, Priority.ALWAYS);
        hbDominio.setSpacing(20);
        hbDominio.setStyle("-fx-background-color: mediumslateblue;");
        
        HBox hbUri = new HBox(uriText, uri);
        HBox.setHgrow(uri, Priority.ALWAYS);
        hbUri.setSpacing(20);
        hbUri.setStyle("-fx-background-color: mediumslateblue;");
        
        VBox vbCenter = new VBox(hbDominio, hbUri);
        vbCenter.setAlignment(Pos.TOP_CENTER);
        vbCenter.setSpacing(20);
        vbCenter.setStyle("-fx-background-color: deepskyblue");
        
        // aggiusta la gui per ogni nuova textfield
        addUrisB.setOnAction( (e) -> {   	
        	Text uriTexts = new Text("URI ->");
        	uriTexts.setFont(new Font(uriText.getText(), 20));
            uriTexts.setFill(Color.YELLOW);
            uriTexts.setStroke(Color.BLACK);
            uriTexts.setStrokeWidth(1);
            
        	TextField newUri = new TextField();
            HBox hbUris = new HBox(uriTexts, newUri);
            HBox.setHgrow(newUri, Priority.ALWAYS);
            hbUris.setSpacing(20);
            //Da qui, tutti gli HBox dei nuovi uri avranno colori randomatici
            Random random = new Random();
            hbUris.setStyle("-fx-background-color: " + colorList[random.nextInt(colorList.length)]);
     
        	vbCenter.getChildren().add(hbUris);
        });
        
        

        
        
        // END CENTER ----------------------------------------------
        
        // BOTTOM -------------------------------------------
        
        Rectangle r5 = new Rectangle(0, 0, 30, 30);
        r5.setStroke(Color.AQUAMARINE);
        r5.setStrokeWidth(10);
        r5.setArcHeight(15);
        r5.setArcWidth(15);
        r5.setFill(Color.YELLOW);
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(800), r5);
        rotateTransition.setByAngle(180f);
        rotateTransition.setCycleCount(Timeline.INDEFINITE);
        rotateTransition.setAutoReverse(false);
        
        
        StackPane spBottom = new StackPane(r5);
        HBox hbBottom = new HBox(spBottom);
        hbBottom.setAlignment(Pos.CENTER);
        hbBottom.setPrefHeight(50);
        
        // END BOTTOM ----------------------------------------
        
        // SETTINGS ACTIONS
        
        //test di recupero uri dai vari TextField creati, pare funge
        goB.setOnAction( (e) -> {
        	rotateTransition.play();
        	ScaleTransition scaleTransition = 
                    new ScaleTransition(Duration.millis(300), goB);
                scaleTransition.setToX(3f);
                scaleTransition.setToY(3f);
                scaleTransition.setCycleCount(2);
                scaleTransition.setAutoReverse(true);
                scaleTransition.play();
        	uris = new ArrayList<>();
            for(Node f : vbCenter.getChildren()) {
            	if( f instanceof HBox) { //tanto sono tutti Hbox
            		HBox accabi = (HBox) f; //quindi casto facile
            		for( Node g : accabi.getChildren() ){
                       	if( g instanceof TextField) {
                        	TextField tf = (TextField)g;
                        	//System.out.println(tf.getText() );
                        	uris.add(tf.getText());
                        }
            		}
            	}
            }       
            for( String s : uris) System.out.println( "uri: " + s );    
            });
        
        pauseB.setOnAction( (e) -> {
        	rotateTransition.stop();
    		ScaleTransition scaleTransition = 
                    new ScaleTransition(Duration.millis(500), pauseB);
                scaleTransition.setToX(1.5f);
                scaleTransition.setToY(1f);
                scaleTransition.setCycleCount(2);
                scaleTransition.setAutoReverse(true);
                scaleTransition.play();
        	if(pauseB.getText().equalsIgnoreCase("PAUSE")) {
        		pauseB.setText("RESUME");
        		pauseB.setPrefWidth(pauseB.getWidth()+20);
        		r4.setWidth(90);
        		
        	}
        	else {
        		ScaleTransition scaleTransition2 = 
                        new ScaleTransition(Duration.millis(500), pauseB);
                    scaleTransition2.setToX(0.5f);
                    scaleTransition2.setToY(1f);
                    scaleTransition2.setCycleCount(2);
                    scaleTransition2.setAutoReverse(true);
                    scaleTransition2.play();
        		pauseB.setText("PAUSE");
        		pauseB.setPrefWidth(pauseB.getWidth()-20);
        		r4.setWidth(70);
        	}
        });
        
        // ---------------------------------------------------------------
        
        VBox vbRight = new VBox(sp1, sp2, sp3, sp4);
        vbRight.setPrefWidth(100);
        vbRight.setSpacing(20);
        vbRight.setAlignment(Pos.CENTER);
        vbRight.setStyle("-fx-background-color: darkturquoise");
        //vbRight.setPadding(new Insets(0, 20, 10, 20)); 
    	
    	BorderPane borderPane = new BorderPane();
    	borderPane.setTop(vbTop);
    	borderPane.setCenter(vbCenter);
    	borderPane.setRight(vbRight);
    	borderPane.setLeft(vbLeft);
    	borderPane.setBottom(hbBottom);
    	
    	//borderPane.setBottom(bottom);
    	borderPane.setStyle("-fx-background-color: mediumslateblue");

    	return borderPane;
	}
    
    private void setRectAnimation(Rectangle rect, Paint color) {
        rect.setArcHeight(15);
        rect.setArcWidth(15);
        rect.setFill(color);
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(1000), rect);
        rotateTransition.setByAngle(180f);
        rotateTransition.setCycleCount(2);
        rotateTransition.setAutoReverse(true);
        rotateTransition.play();
    }
    
    private void setRectMouseSpec(Button button, Rectangle rect) {
    	button.setOnMousePressed( (e) -> {
    		RotateTransition rotateTransition = 
                    new RotateTransition(Duration.millis(500), rect);
                rotateTransition.setByAngle(180f);
                rotateTransition.setCycleCount(1);
                rotateTransition.play();
    	});
    }
    
    
	public static void main(String[] args) {
        launch(args);
    }  
}

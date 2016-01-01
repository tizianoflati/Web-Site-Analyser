package wsa.web;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Gui extends Application {
	Node[] textfList = new TextField[3];
	
    public void start(Stage primaryStage) {
        Scene scene = new Scene(createUI(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("WSA");
        primaryStage.show();
    }

    
    private Parent createUI() {
    	// Intestazione superiore app
        Text appName = new Text("Web site analyser");
        appName.setFont(new Font(appName.getText(), 40));
        appName.setFill(Color.YELLOW);
        appName.setStroke(Color.BLACK);
        appName.setStrokeWidth(1);
    	
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
        
        Button addButton = new Button("Add uris");
        
        Button goButton = new Button("GO!");
        goButton.setAlignment(Pos.BOTTOM_CENTER);
        goButton.setScaleX(2);
        goButton.setScaleY(2);
   
        
        HBox hbDominio = new HBox(dominioText, dominio);
        HBox.setHgrow(dominio, Priority.ALWAYS);
        hbDominio.setSpacing(20);
        
        HBox hbUris = new HBox(uriText, uri, addButton);
        HBox.setHgrow(uri, Priority.ALWAYS);
        hbUris.setSpacing(20);
        
        VBox vb = new VBox(appName, hbDominio, hbUris, goButton);
        vb.setAlignment(Pos.TOP_CENTER);
        vb.setSpacing(30);
        vb.setStyle("-fx-background-color: grey");
        
        // mmhhh...
        addButton.setOnAction( (e) -> {
        	vb.getChildren().remove(vb.getChildren().size()-1);
        	vb.getChildren().add(new TextField());
        	vb.getChildren().add(goButton);
        });
        
        
        //tet di recupero uri dai vari TextField creati, pare funge
        goButton.setOnAction( (e) -> {
        	
            System.out.println(dominio.getText());
            System.out.println(uri.getText());
            
            for(Node f : vb.getChildren()) {
            	if( f instanceof TextField) {
            		TextField tf = (TextField)f;
            		System.out.println(tf.getText() );
            	}
            }
            
            });

        return vb;
	}


	public static void main(final String[] args) {
        launch(args);
    }

}

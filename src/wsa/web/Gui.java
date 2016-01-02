package wsa.web;

import java.util.ArrayList;
import java.util.List;

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
	List<String> uris = new ArrayList<>();
	
    public void start(Stage primaryStage) {
        Scene scene = new Scene(createUI(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("WSA");
        primaryStage.show();
    }

    
    private Parent createUI() {
    	// Intestazione superiore app
        Text appName = new Text("Web site analyser");
        appName.setFont(new Font(appName.getText(), 60));
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
        
        HBox hbUri = new HBox(uriText, uri);
        HBox.setHgrow(uri, Priority.ALWAYS);
        hbUri.setSpacing(20);
        
        VBox vb = new VBox(appName, hbDominio, hbUri, addButton, goButton);
        vb.setAlignment(Pos.TOP_CENTER);
        vb.setSpacing(30);
        vb.setStyle("-fx-background-color: grey");
        
        // aggiusta la gui per ogni nuova textfield
        addButton.setOnAction( (e) -> {
        	vb.getChildren().remove(vb.getChildren().size()-2);
        	vb.getChildren().remove(vb.getChildren().size()-1);
        	
        	Text uriTexts = new Text("URI ->");
        	uriTexts.setFont(new Font(uriText.getText(), 20));
            uriTexts.setFill(Color.YELLOW);
            uriTexts.setStroke(Color.BLACK);
            uriTexts.setStrokeWidth(1);
        	TextField newUri = new TextField();
            HBox hbUris = new HBox(uriTexts, newUri);
            HBox.setHgrow(newUri, Priority.ALWAYS);
            hbUris.setSpacing(20);
     
        	vb.getChildren().add(hbUris);
        	vb.getChildren().add(addButton);
        	vb.getChildren().add(goButton);
        });
        
        
        //test di recupero uri dai vari TextField creati, pare funge
        goButton.setOnAction( (e) -> {        
            for(Node f : vb.getChildren()) {
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

        return vb;
	}


	public static void main(final String[] args) {
        launch(args);
    }

}

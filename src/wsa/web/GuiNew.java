package wsa.web;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class GuiNew extends Application{
	private List<String> uris = new ArrayList<>();
	private String[] colorList = {"chartreuse", "coral", "deeppink", "lightgreen",
			"gold", "mediumturquoise", "orangered", "snow",
			"slateblue" };
	private File selectedDirectory;
	private Scene infoScene;
	private Stage infoStage;
	private Integer currentWebsite = 1;
	private Integer websiteNumber = 1;
	// siteMap associa ogni bottone ad un numero che rappresenta il sito web da esplorare
	private Map<Button, Integer> siteMap = new HashMap<Button, Integer>();
	// stateMap associa sitoWeb con un suo stato (tabella, situazione bottoni personalizzata ecc..)
	private Map<Integer, WebsiteState> stateMap = new HashMap<Integer, WebsiteState>();
	private BorderPane borderPane = new BorderPane();
	private WebEngine we;
	private WebView wView;
	
	//Classe simulativa per i risultati crawlerResults
	public class LinkResult {
	    private final SimpleStringProperty urlName;
	 
	    private LinkResult(String url) {
	        this.urlName = new SimpleStringProperty(url);
	    }
	    
	    public SimpleStringProperty getUrlName() {
	    	return urlName;
	    }
	 
	    public String getUrl() {
	        return urlName.get();
	    }
	    
	    public void setUrl(String url) {
	        this.urlName.set(url);
	    }
      
	}

	public class WebsiteState {
		private List<String> seedList;
		private VBox vb;
		private Parent table;
		
		public WebsiteState(Parent table, VBox vb) {
			this.table = table;
			this.vb = vb;
		}
		
		public void setseedList(List<String> seedList) {
			this.seedList = seedList;
		}
		
		public List<String> getSeedList() { return seedList; }
		public VBox getVBox() { return vb; }
		public Parent getTable() { return table; }
		
	}
	
	public void start(Stage primaryStage) {
        Scene scene = new Scene(createUI(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("WSA");
        primaryStage.show();
        
	}
		
    private Parent createUI() {
        wView = new WebView();
        we = wView.getEngine();
    	
    	// TOP
        Text addUriText = new Text("WEB SITE ANALYZER");
        addUriText.setFont(new Font(addUriText.getText(), 50));
        addUriText.setFill(Color.YELLOW);
        addUriText.setStroke(Color.GREEN);
        addUriText.setStrokeWidth(2);
        addUriText.setTextAlignment(TextAlignment.CENTER);
        VBox vbTop = new VBox(addUriText);
        vbTop.setAlignment(Pos.TOP_CENTER);
        vbTop.setStyle("-fx-background-color: black");        
    	// END TOP
        
        // LEFT     
        Button addSiteB = new Button("Add website");
        Button websiteB = new Button("Website " + websiteNumber);
        siteMap.put(websiteB, websiteNumber);      
        websiteB.setOnAction( e -> {
    		currentWebsite = siteMap.get(websiteB);
    		System.out.println("current website number selected: " + currentWebsite);
    		WebsiteState wss = stateMap.get(currentWebsite);
    		System.out.println("wss: " + wss);
    		
    		borderPane.setCenter(new ScrollPane(wss.getTable()));       		
    		borderPane.setRight(wss.getVBox());
        }); 
        	VBox vbCenterBase = createCenter();        	
    		ScrollPane spCenter = new ScrollPane(vbCenterBase);
    		spCenter.setFitToWidth(true);  // Per far sì che il contenitore spCenter
    		spCenter.setFitToHeight(true); // occupi tutto lo spazio disponibile 
    		// END CENTER
        WebsiteState wss1 = new WebsiteState(createTableView("Website " + websiteNumber), createRightUi(vbCenterBase));
        stateMap.put(currentWebsite, wss1);       
        VBox vbLeft = new VBox(addSiteB, websiteB);
        vbLeft.setPrefWidth(100);
        vbLeft.setSpacing(20);
        vbLeft.setAlignment(Pos.TOP_CENTER);
        //vbLeft.setStyle("-fx-background-color: black");        
        ScrollPane spLeft = new ScrollPane(vbLeft);
        spLeft.setFitToWidth(true);
        spLeft.setFitToHeight(true);       
        addSiteB.setOnAction( e -> {
        	websiteNumber++;
        	currentWebsite = websiteNumber;
        	System.out.println("websiteNumber: " + websiteNumber);
        	VBox vbCenter = createCenter();
        	VBox vbRight = createRightUi(vbCenter);
        	borderPane.setCenter(vbCenter);
        	borderPane.setRight(vbRight);
        	Button newWebsiteB = new Button("Website " + websiteNumber);        	
            WebsiteState nwss = new WebsiteState(createTableView("Website " + websiteNumber), createRightUi(vbCenter));
            stateMap.put(websiteNumber, nwss);
            
            System.out.println("stateMap: " + stateMap);
            
        	newWebsiteB.setOnAction( f -> {
        		currentWebsite = siteMap.get(newWebsiteB);
        		System.out.println("current website number selected: " + currentWebsite);
        		WebsiteState wss = stateMap.get(currentWebsite);
        		System.out.println("wss: " + wss);            	
            	borderPane.setCenter(new ScrollPane(wss.getTable()));       		       		
        		borderPane.setRight(wss.getVBox());
        	});
        	vbLeft.getChildren().add( newWebsiteB );
        	siteMap.put(newWebsiteB, websiteNumber);
        });
        // END LEFT
             
        // RIGHT
        VBox vbRight = createRightUi(vbCenterBase);
        // END RIGHT

        // BOTTOM -------------------------------------------
        
        // END BOTTOM ----------------------------------------
	
    	borderPane.setTop(vbTop);
    	borderPane.setCenter(spCenter);
    	borderPane.setRight(vbRight);
    	borderPane.setLeft(spLeft);
    	//borderPane.setBottom(hbBottom);
    	borderPane.setStyle("-fx-background-color: mediumslateblue");
    	
    	return borderPane;
	}
    
	private VBox createCenter() {
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
		
		return vbCenter;
	}
    
    private VBox createRightUi(VBox vbCenter) {
        Button addSeedB = new Button("Add seed");
        Button goB = new Button("Go!!");
        Button saveB = new Button("Save");
        
        VBox vb = new VBox(addSeedB, goB, saveB);
        vb.setPrefWidth(100);
        vb.setSpacing(20);
        vb.setAlignment(Pos.TOP_CENTER);
        vb.setStyle("-fx-background-color: darkturquoise");
        
        goB.setOnAction( (e) -> {
        	WebsiteState wss = stateMap.get(currentWebsite);
        	System.out.println(currentWebsite);
        	ScrollPane spCenter = new ScrollPane(wss.getTable());
        	borderPane.setCenter(spCenter);
        	if(goB.getText().equalsIgnoreCase("PAUSE")) {
        		goB.setText("RESUME");
        	}
        	else {
        		goB.setText("PAUSE");
        	}
        	
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
            
        	
        	stateMap.get(currentWebsite).setseedList(uris);
        	
        });

        
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource File");
      
        saveB.setOnAction( (e) -> {
        	final File selectedDirectory = directoryChooser.showDialog(new Stage());
            if (selectedDirectory != null) {
            	this.selectedDirectory = selectedDirectory;
                System.out.println(selectedDirectory.getAbsolutePath()); //Test
            }
        });
        
        addSeedB.setOnAction( (e) -> {   	
        	Text uriTexts = new Text("URI ->");
        	uriTexts.setFont(new Font(uriTexts.getText(), 20));
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
        
        return vb;
    }
    
    private Parent createTableView(String title) {
    	TableView table = new TableView();
    	table.setPrefWidth(Double.MAX_VALUE);

    	ObservableList<LinkResult> data = FXCollections.observableArrayList(
    			new LinkResult("http://www.multiplayer.it"),
    			new LinkResult("http://www.eurogamer.it"));
    	
    	Label label = new Label(title);
        label.setFont(new Font("Arial", 20));
 
        table.setEditable(true);
 
        TableColumn urlNameCol = new TableColumn("URL");
        urlNameCol.setMinWidth(100);
        //urlNameCol.setCellValueFactory( new PropertyValueFactory<LinkResult, String>("url"));
        urlNameCol.setCellValueFactory(new Callback<CellDataFeatures<LinkResult, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(CellDataFeatures<LinkResult, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().getUrlName();
            }
         });
        
        TableColumn testCol = new TableColumn("test");
        testCol.setMinWidth(100);
   
        table.setItems(data);
        table.getColumns().addAll(urlNameCol, testCol);
 
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);
        
        Group group = new Group(vbox);
        
        //urlNameCol.setCellFactory
        
        table.setOnContextMenuRequested( e -> {
        	we.load((String)table.getSelectionModel().getSelectedItem());
        	Stage stage = new Stage();
        	Scene scene = new Scene(wView, 400, 600);
        	stage.setScene(scene);
        	stage.show();
        });
        
        return group;
	}
    
	public static void main(String[] args) {
        launch(args);
    }  
}

package wsa.web;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.sun.prism.impl.Disposer.Record;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class GuiNew extends Application{
	private List<String> uris = new ArrayList<>();
	private String[] colorList = {"chartreuse", "coral", "deeppink", "lightgreen",
			"gold", "mediumturquoise", "orangered", "snow",
			"slateblue" };
	private Integer currentWebsite = 1;
	private Integer websiteNumber = 1;
	private File selectedDirectory = null;
	// siteMap associa ogni bottone ad un numero che rappresenta il sito web da esplorare
	private Map<Button, Integer> siteMap = new HashMap<Button, Integer>();
	// stateMap associa sitoWeb con un suo stato (tabella, situazione bottoni personalizzata ecc..)
	private Map<Integer, WebsiteState> stateMap = new HashMap<Integer, WebsiteState>();
	private BorderPane borderPane = new BorderPane();
	private WebFactoryWSA webFactory = new WebFactoryWSA();

	
	/**Classe simulativa per i risultati crawlerResults */
	public class LinkResult {
	    private final SimpleStringProperty urlName;
	    private final SimpleBooleanProperty linkPage;
	    private final SimpleStringProperty exception;
	    
	    private LinkResult(CrawlerResult result) {
	        this.urlName = new SimpleStringProperty(result.uri.toString());
	        this.linkPage = new SimpleBooleanProperty(result.linkPage);
	        this.exception = new SimpleStringProperty(result.exc == null ? "OK" : result.exc.toString() + ": " + result.exc.getMessage());
	    }
	    
	    public SimpleStringProperty getUrlName() {
	    	return urlName;
	    }
	 
	    public void setUrl(String url) {
	        this.urlName.set(url);
	    }

		public ObservableValue<String> getStatus() {
			return this.exception;
		}
      
	}

	/** 
	 * Un oggetto che rappresenta lo stato di una singola esplorazione di un sito web
	 * quello che serve graficamente come stato è solo la tabella dei dati ed i bottoni
	 * della parte destra della GUI
	 */
	public class WebsiteState {
		private List<String> seedList = new ArrayList<>();
		private VBox rightVb;
		private VBox centerVb;
		private Parent table = null;
		private SiteCrawler siteCrawler = null;
		private Path path = null;
		private boolean isStarted = false;
		
		/**
		 * 
		 * @param table la tabella relativo al webSite
		 * @param vb la parte destra della GUI
		 */
		public WebsiteState(Parent table, VBox centerVb, VBox rightVb) {
			this.table = table;
			this.rightVb = rightVb;
			this.centerVb = centerVb;
		}
		
		public void setseedList(List<String> seedList) {
			this.seedList = seedList;
		}
		
		public void setRightVBox(VBox rightVb) {
			this.rightVb = rightVb;
		}
		
		public void setPath(Path path){
			this.path = path;
		}
		
		public void start(Path dir){
			
			try {
				siteCrawler = webFactory.getSiteCrawler(new URI(seedList.get(0)), dir);
				isStarted = true;
				//siteCrawler.start();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		public boolean isStarted() {
			return isStarted;
		}
		
		public List<String> getSeedList() { return seedList; }
		
		public VBox getRightVBox() { return rightVb; }
		
		public VBox getCenterVBox() { return centerVb; }
		
		public Parent getTable() { return table; }
		
		//per test
		public void showInfo() {
			System.out.println("SeedList: " + seedList);
			System.out.println("Path: " + path);
		}
		
	}
	
	public void start(Stage primaryStage) {
        Scene scene = new Scene(createUI(), 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("WSA");
        primaryStage.show();
        
	}
		
    private Parent createUI() {
    	
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
    		
    		// test
    		//System.out.println("wss: " + wss);
    		System.out.println("WebSite  " + currentWebsite + ":");
    		wss.showInfo();
    		
    		if( wss.isStarted() ){
    			borderPane.setCenter(new ScrollPane(wss.getTable()));       		
    		}
    		else {
    			borderPane.setCenter(wss.getCenterVBox());
    		}
    		borderPane.setRight(wss.getRightVBox());
    		
    	}); 
        	
        VBox vbCenterBase = createCenter();        	
    		ScrollPane spCenter = new ScrollPane(vbCenterBase);
    		spCenter.setFitToWidth(true);  // Per far sì che il contenitore spCenter
    		spCenter.setFitToHeight(true); // occupi tutto lo spazio disponibile 
    		
        WebsiteState wss1 = new WebsiteState(createTableView("Website " + websiteNumber), createCenter(), createRightUi(vbCenterBase));
        stateMap.put(currentWebsite, wss1);   
        
        VBox vbLeft = new VBox(addSiteB, websiteB);
        vbLeft.setPrefWidth(100);
        vbLeft.setSpacing(20);
        vbLeft.setAlignment(Pos.TOP_CENTER);
        vbLeft.setStyle("-fx-background-color: black");        
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
            WebsiteState nwss = new WebsiteState(createTableView("Website " + websiteNumber), createCenter(), createRightUi(vbCenter));
            stateMap.put(websiteNumber, nwss);
            
            System.out.println("stateMap: " + stateMap);
            
        	newWebsiteB.setOnAction( f -> {
        		
        		currentWebsite = siteMap.get(newWebsiteB);
        		System.out.println("current website number selected: " + currentWebsite);
        		WebsiteState wss = stateMap.get(currentWebsite);
        		
        		//TEST
        		System.out.println("WebSite  " + currentWebsite + ":");
        		wss.showInfo();
        		
        		if( wss.isStarted() ){
        			borderPane.setCenter(new ScrollPane(wss.getTable()));       		
        		}
        		else {
        			borderPane.setCenter(wss.getCenterVBox());
        		}
        		borderPane.setRight(wss.getRightVBox());

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
		
		Text websiteT = new Text("Website " + currentWebsite);
		websiteT.setFont(new Font(websiteT.getText(), 20));
		websiteT.setFill(Color.YELLOW);
		websiteT.setStroke(Color.BLACK);
		websiteT.setStrokeWidth(1);
		
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
		
		VBox vbCenter = new VBox(websiteT, hbDominio, hbUri);
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
            
        	if( uris.get(0).isEmpty() || uris.get(1).isEmpty() ) {
        		Stage stage = new Stage();
        		
        		Text text = new Text("Dati insufficienti!");
        		text.setTextAlignment(TextAlignment.CENTER);
        		
        		Button button = new Button("Ok");
        		button.setOnAction( e2 -> {
        			stage.close();
        		});
        		
        		VBox vbPop = new VBox(text, button);
        		vbPop.setAlignment(Pos.CENTER);
        		vbPop.setSpacing(10);
        		
        		Scene scene = new Scene(vbPop, 50, 50);
        	    stage.setScene(scene);
        	    stage.show();
           	}
        	else {
            	//aggiorna la VBox per levare il tasto "save"
            	VBox nvb = new VBox(addSeedB, goB);
                nvb.setPrefWidth(100);
                nvb.setSpacing(20);
                nvb.setAlignment(Pos.TOP_CENTER);
                nvb.setStyle("-fx-background-color: darkturquoise");
            	
            	if(goB.getText().equalsIgnoreCase("PAUSE")) {
            		goB.setText("RESUME");
            	}
            	else {
            		goB.setText("PAUSE");
            	}
                //carica lo stato associato al webSite e...
            	WebsiteState wss = stateMap.get(currentWebsite);
            	//..setta la nuova parte destra di GUI
            	wss.setRightVBox(nvb);
            	//setta gli uri forniti
            	wss.setseedList(uris);
            	
            	wss.showInfo();
            	
            	//START
            	if(selectedDirectory == null)
            		wss.start(null);
            	else
            		wss.start(selectedDirectory.toPath());
            	
            	//aggiorna il borderPane con i dati nuovi
            	borderPane.setRight(nvb);
            	System.out.println(currentWebsite);
            	ScrollPane spCenter = new ScrollPane(wss.getTable());
            	borderPane.setCenter(spCenter);
        	}
        		
        	
        });

        
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource File");
      
        saveB.setOnAction( (e) -> {
        	final File selectedDirectory = directoryChooser.showDialog(new Stage());
            if (selectedDirectory != null) {
            	this.selectedDirectory = selectedDirectory;
                System.out.println(selectedDirectory.getAbsolutePath()); //Test
                stateMap.get(currentWebsite).setPath(selectedDirectory.toPath());
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
     
            WebsiteState wss = stateMap.get(currentWebsite);
            if( wss.isStarted ) {
              	Stage stage = new Stage();
            	Button addseednw = new Button("Add uri");
            		addseednw.setOnAction( e2 -> {
            			
            			wss.getSeedList().add(newUri.getText());
            			System.out.println("new uri added");
            			stage.close();
            		});
            	VBox vbnw = new VBox(hbUris, addseednw);
            	vbnw.setAlignment(Pos.CENTER);
            	vbnw.setSpacing(20);
            	vbnw.setStyle("-fx-background-color: mediumslateblue;");
                Scene scene = new Scene(vbnw, 300, 100);
                stage.setScene(scene);
                stage.setTitle("Add uri");
                stage.show();
            }
            else
            	vbCenter.getChildren().add(hbUris);
        	
        });
        
        return vb;
    }
    
    private Parent createTableView(String title)
    {
    	TableView<LinkResult> table = new TableView<LinkResult>();
    	table.setPrefWidth(Double.MAX_VALUE);

		try
		{
			ObservableList<LinkResult> data = FXCollections.observableArrayList(
					new LinkResult(new CrawlerResult(new URI("http://www.multiplayer.it"), false, null, null, null))
//    			new LinkResult("http://www.eurogamer.it")
					);
			
			table.setItems(data);
		}
		catch (URISyntaxException e1)
		{
			e1.printStackTrace();
		}
    	
    	Label label = new Label(title);
        label.setFont(new Font("Arial", 20));
 
        table.setEditable(true);
 
        TableColumn<LinkResult, String> urlNameCol = new TableColumn<LinkResult, String>("URL");
        urlNameCol.setMinWidth(100);
        urlNameCol.setCellValueFactory(new Callback<CellDataFeatures<LinkResult, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(CellDataFeatures<LinkResult, String> linkResultCell) {
                return linkResultCell.getValue().getUrlName();
            }
         });
        TableColumn<LinkResult, String> statusCol = new TableColumn<LinkResult, String>("STATUS");
        statusCol.setMinWidth(100);
        statusCol.setCellValueFactory(new Callback<CellDataFeatures<LinkResult, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(CellDataFeatures<LinkResult, String> linkResultCell) {
                return linkResultCell.getValue().getStatus();
            }
         });
        TableColumn<LinkResult, Boolean> detailedCol = new TableColumn<LinkResult, Boolean>("DETAILS");
        detailedCol.setMinWidth(100);
        //detailedCol.setCellValueFactory(new Callback<CellDataFeatures<LinkResult, String>, ObservableValue<String>>() {
          //  public ObservableValue<String> call(CellDataFeatures<LinkResult, String> linkResultCell) {
            //    return linkResultCell.getValue().getStatus();
            //}
        // });
        detailedCol.setCellFactory(
                new Callback<TableColumn<LinkResult, Boolean>, TableCell<LinkResult, Boolean>>() {
 
            @Override
            public TableCell<LinkResult, Boolean> call(TableColumn<LinkResult, Boolean> p) {
                return new TableCell<LinkResult, Boolean>() {
                	Button button = new Button("detail");

                	{
                		button.setOnAction( e -> {
                			System.out.println("detailed clicked!!! ");
                		});
                	}
                	
                    //Display button if the row is not empty
                    @Override
                    protected void updateItem(Boolean t, boolean empty) {
                        super.updateItem(t, empty);
                        if(!empty){
                            setGraphic(button);
                        }
                    }
                };
           }
            });
        
        table.getColumns().add(urlNameCol);
        table.getColumns().add(statusCol);
        table.getColumns().add(detailedCol);
        
        TableCell<LinkResult, String> cell;
        
        //table.setSelectionModel(value);
        
//        TableColumn testCol = new TableColumn("test");
//      testCol.setMinWidth(100);
 
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);
        
        Group group = new Group(vbox);
        
        /**
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            //if (newSelection != null) {
            	System.out.println("cell cliccked");
                WebView wView = new WebView();
                WebEngine we = wView.getEngine();
                
            	we.load((String)table.getSelectionModel().getSelectedItem().getUrlName().get());
            	Stage stage = new Stage();
            	Scene scene = new Scene(wView, 600, 400);
            	stage.setScene(scene);
            	stage.show();
            //}
        });
        **/
        
        
        /** PERMETTE DI EDITARE LA TABELLA
        urlNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        urlNameCol.setOnEditCommit(
            new EventHandler<CellEditEvent<LinkResult, String>>() {
                @Override
                public void handle(CellEditEvent<LinkResult, String> t) {
                    String url = ((LinkResult) t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).getUrlName().getName();
                    
                	System.out.println("cell cliccked");
                    WebView wView = new WebView();
                    WebEngine we = wView.getEngine();
                    
                	we.load(url);
                	Stage stage = new Stage();
                	Scene scene = new Scene(wView, 600, 400);
                	stage.setScene(scene);
                	stage.show();
                }
            }
        );
        */
        
        return group;
	}
    
	public static void main(String[] args) {
        launch(args);
    }  
}

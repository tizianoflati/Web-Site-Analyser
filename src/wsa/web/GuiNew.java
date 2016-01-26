package wsa.web;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
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
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
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
	// Colori per i textFields
	private String[] colorList = {"chartreuse", "coral", "deeppink", "lightgreen",
			"gold", "mediumturquoise", "orangered", "snow",
	"slateblue" };
	private Integer currentWebsite = 1;
	private Integer websiteNumber = 1;
	// siteMap associa ogni bottone ad un numero che rappresenta il sito web da esplorare
	private Map<Button, Integer> siteMap = new HashMap<Button, Integer>();
	// stateMap associa sitoWeb con un suo stato (tabella, situazione bottoni personalizzata ecc..)
	private Map<Integer, WebsiteState> stateMap = new HashMap<Integer, WebsiteState>();

	private BorderPane borderPane = new BorderPane();
	


	/**Classe simulativa per i risultati crawlerResults */
	public class LinkResult {
		private final SimpleStringProperty urlName;
		private final SimpleBooleanProperty linkPage;
		private final SimpleStringProperty exception;
		private SimpleStringProperty links;
		private List<URI> uriList;

		private LinkResult(CrawlerResult result) {
			this.urlName = new SimpleStringProperty(result.uri.toString());
			this.linkPage = new SimpleBooleanProperty(result.linkPage);
			this.exception = new SimpleStringProperty(result.exc == null ? "OK" : result.exc.toString() + ": " + result.exc.getMessage());
			this.uriList = result.links;
		}


		public SimpleStringProperty getObsLinks() {
			String slinks = "";
			for(URI u : uriList)
				slinks = slinks + u.toString() + "\n";
			this.links = new SimpleStringProperty(slinks);
			return links;
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
		private VBox rightVb;
		private VBox centerVb;
		private Group group;
		private TableView<LinkResult> table = null;
		private SiteCrawler siteCrawler = null;
		private Path path = null;
		private TextField dominioText = null;
		private List<TextField> seedsList = new ArrayList<>();
		private String id;
		
		private ObservableList<LinkResult> obsList;

		
		
		/**
		 * 
		 * @param table la tabella relativo al webSite
		 * @param vb la parte destra della GUI
		 */
		public WebsiteState(String id) {
			this.id = id;
		}
		
		public void setTable(TableView<LinkResult> table){
			Label label = new Label(id);
			label.setFont(new Font("Arial", 20));

			final VBox vbox = new VBox();
			vbox.setSpacing(5);
			vbox.setPadding(new Insets(10, 0, 0, 10));
			vbox.getChildren().addAll(label, table);
			this.group = new Group(vbox);
			this.table = table;
		}

		public void setRightVBox(VBox rightVb) {
			this.rightVb = rightVb;
		}
		
		public void setCenterVBox(VBox centerVb) {
			this.centerVb = centerVb;
		}

		public void setPath(Path path){
			this.path = path;
		}
		
		public void setTextFieldDom(TextField dominio){
			this.dominioText = dominio;
		}

		public void start(Path dir){

			try {
				this.siteCrawler = WebFactoryWSA.getSiteCrawler(new URI(dominioText.getText()), dir);
				siteCrawler.start();
				Timer timer = new Timer();

				timer.schedule( new TimerTask() {				
					@Override
					public void run() {

						Optional<CrawlerResult> cr = null;
						while((cr=siteCrawler.get()).isPresent())
						{
							LinkResult lr = new LinkResult(cr.get());
							ObservableList<LinkResult> oList = table.getItems();
							oList.add(lr);
							obsList = oList;
						}
					}
				}, 0, 1000);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}

		public VBox getRightVBox() { return rightVb; }

		public VBox getCenterVBox() { return centerVb; }

		public Parent getGroup() { return group; }
		
		public Path getPath() { return path; }

		//per test
		public void showInfo() {
			System.out.println("SeedList: " + seedsList);
			System.out.println("Path: " + path);
		}

		public ObservableList<LinkResult> getData() {
			return obsList;
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
		
		//bottone di aggiunta website
		Button addSiteB = new Button("Add website");
		
		Button websiteB = new Button("Website " + websiteNumber);
		siteMap.put(websiteB, websiteNumber); 
		
		WebsiteState wss1 = new WebsiteState("Website " + websiteNumber);
		wss1.setTable(createTableView());
/*TEST*/		System.out.println("first state created");		
		stateMap.put(currentWebsite, wss1);		

		//CENTER
		//prima creazione center
		VBox currentCenter = createCenter();        	
		ScrollPane spCenter = new ScrollPane(currentCenter);
		spCenter.setFitToWidth(true);  // Per far sì che il contenitore spCenter
		spCenter.setFitToHeight(true); // occupi tutto lo spazio disponibile 
		//END CENTER
		
/*TEST*/System.out.println(stateMap);
		
		//Salva lo stato grafico iniziale
		wss1.setCenterVBox(createCenter());
		wss1.setRightVBox(createRightUi(currentCenter));

		VBox vbLeft = new VBox(addSiteB, websiteB);
		vbLeft.setPrefWidth(100);
		vbLeft.setSpacing(20);
		vbLeft.setAlignment(Pos.TOP_CENTER);
		vbLeft.setStyle("-fx-background-color: black");        
		ScrollPane spLeft = new ScrollPane(vbLeft);
		spLeft.setFitToWidth(true);
		spLeft.setFitToHeight(true);

		// END LEFT

		// RIGHT
		VBox vbRight = createRightUi(currentCenter);
		// END RIGHT

		// BOTTOM
		// END BOTTOM

		//SETTING BORDERPANE
		borderPane.setTop(vbTop);
		borderPane.setCenter(spCenter);
		borderPane.setRight(vbRight);
		borderPane.setLeft(spLeft);
		//borderPane.setBottom(hbBottom);
		borderPane.setStyle("-fx-background-color: mediumslateblue");

		// ACTIONS SETTINGS 
		
		addSiteB.setOnAction( e -> {
			websiteNumber++;
			currentWebsite = websiteNumber;
			System.out.println("websiteNumber: " + websiteNumber);
			WebsiteState nwss = new WebsiteState("Website " + websiteNumber);
			nwss.setTable(createTableView());
			System.out.println("WebsiteState " + currentWebsite + " created");
			stateMap.put(websiteNumber, nwss);
/*TEST*/		System.out.println("new state created");			
			VBox ncurrentCenter = createCenter();
			VBox currentRight = createRightUi(ncurrentCenter);
			borderPane.setCenter(ncurrentCenter);
			borderPane.setRight(currentRight);
			Button newWebsiteB = new Button("Website " + websiteNumber);        	
			
			nwss.setCenterVBox(createCenter());
			nwss.setRightVBox(createRightUi(currentCenter));

			newWebsiteB.setOnAction( f -> {

				currentWebsite = siteMap.get(newWebsiteB);
				System.out.println("current website number selected: " + currentWebsite);
				WebsiteState wss = stateMap.get(currentWebsite);
System.out.println("WebsiteState " + currentWebsite + " loaded");
				
/*TEST*/		System.out.println("WebSite  " + currentWebsite + ":");
				wss.showInfo();

				if( wss.siteCrawler != null ){
					borderPane.setCenter(new ScrollPane(wss.getGroup()));       		
				}
				else {
					borderPane.setCenter(wss.getCenterVBox());
				}
				borderPane.setRight(wss.getRightVBox());

			});
			vbLeft.getChildren().add( newWebsiteB );
			siteMap.put(newWebsiteB, websiteNumber);
		});
		
		websiteB.setOnAction( e -> {
			currentWebsite = siteMap.get(websiteB);
			System.out.println("current website number selected: " + currentWebsite);
			WebsiteState wss = stateMap.get(currentWebsite);
System.out.println("WebsiteState " + currentWebsite + " loaded");

			wss.showInfo();

			if( wss.siteCrawler != null ){
				// se l'esplorazione è iniziata...
				borderPane.setCenter(new ScrollPane(wss.getGroup()));       		
			}
			else {
				borderPane.setCenter(wss.getCenterVBox());
			}
			borderPane.setRight(wss.getRightVBox());

		}); 
		
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

	
		/**
		dominio.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			stringTest += e.getText();
			System.out.println(stringTest);
			System.out.println(e);
			System.out.println(e.getCode());
			});
		//----- da vedere
		dominio.setOnKeyPressed( e -> {
			System.out.println("key pressed!:" + dominio.getText());
			
		});
		**/
		
		System.out.println(stateMap);
		
		stateMap.get(currentWebsite).setTextFieldDom(dominio);
		
		dominio.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			stateMap.get(currentWebsite).setTextFieldDom(dominio);
			});
		
		// Campo primo uri
		Text uriText = new Text("URI ->");
		TextField uri = new TextField();
		uriText.setFont(new Font(uriText.getText(), 20));
		uriText.setFill(Color.YELLOW);
		uriText.setStroke(Color.BLACK);
		uriText.setStrokeWidth(1);
		
		stateMap.get(currentWebsite).seedsList.add(uri);

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
			
			//uris.add("http://www.multiplayer.it");
			//uris.add("http://www.multiplayer.it/ps3");
			
			System.out.println("dom = " + stateMap.get(currentWebsite).dominioText.getText());

			if( stateMap.get(currentWebsite).dominioText.getText().isEmpty() || stateMap.get(currentWebsite).seedsList.isEmpty() ) {
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

				Scene scene = new Scene(vbPop, 70, 70);
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
System.out.println("WebsiteState " + currentWebsite + " loaded");
				//..setta la nuova parte destra di GUI
				wss.setRightVBox(nvb);
				//setta gli uri forniti
				wss.showInfo();

				//START
				wss.start(wss.getPath());

				//aggiorna il borderPane con i dati nuovi
				borderPane.setRight(nvb);
				System.out.println(currentWebsite);
				ScrollPane spCenter = new ScrollPane(wss.getGroup());
				borderPane.setCenter(spCenter);
			}


		});


		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Open Resource File");

		saveB.setOnAction( (e) -> {
			final File selectedDirectory = directoryChooser.showDialog(new Stage());
			if (selectedDirectory != null) {
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
			
			stateMap.get(currentWebsite).seedsList.add(newUri);
			
			//Da qui, tutti gli HBox dei nuovi uri avranno colori randomatici
			Random random = new Random();
			hbUris.setStyle("-fx-background-color: " + colorList[random.nextInt(colorList.length)]);

			WebsiteState wss = stateMap.get(currentWebsite);
			System.out.println("WebsiteState " + currentWebsite + " loaded");
			if( wss.siteCrawler != null ) {
				Stage stage = new Stage();
				Button addseednw = new Button("Add uri");
				VBox vbnw = new VBox(hbUris, addseednw);
				vbnw.setAlignment(Pos.CENTER);
				vbnw.setSpacing(20);
				vbnw.setStyle("-fx-background-color: mediumslateblue;");
				Scene scene = new Scene(vbnw, 300, 100);
				stage.setScene(scene);
				stage.setTitle("Add uri");
				stage.show();
				addseednw.setOnAction( e2 -> {
					try {
						System.out.println(newUri.getText());
						wss.seedsList.add(newUri);
						//si blocca
						//wss.siteCrawler.addSeed(new URI(newUri.getText()));						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					stage.close();
				});

			}
			else {
				VBox nCenter = wss.getCenterVBox();
				borderPane.setCenter(nCenter);
				nCenter.getChildren().add(hbUris);
				wss.setCenterVBox(nCenter);
			}

		});

		return vb;
	}

	private TableView<LinkResult> createTableView()
	{
		TableView<LinkResult> table = new TableView<LinkResult>();
		table.setPrefWidth(Double.MAX_VALUE);

		try
		{
			URI sito1 = new URI("sito1");
			URI	sito2 = new URI("sito2");
			URI	sito3 = new URI("sito3");
			List<URI> testList = Arrays.asList(sito1,sito2,sito3);

			//			Map<URI, Integer>
			ObservableList<LinkResult> data = FXCollections.observableArrayList(
					new LinkResult(new CrawlerResult(new URI("http://www.multiplayer.it"), false, testList, null, null))
					,new LinkResult(new CrawlerResult(new URI("http://www.multiplayer.it/ps3"), false, null, null, null))
					);

			table.setItems(data);
		}
		catch (URISyntaxException e1)
		{
			e1.printStackTrace();
		}
		
		/**
		ObservableList<LinkResult> data = FXCollections.observableArrayList();
		if(stateMap.get(currentWebsite).siteCrawler.isRunning())
			data = stateMap.get(currentWebsite).getData();
			**/
		
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


		TableColumn<LinkResult, String> parallelDcol = new TableColumn<LinkResult, String>("empty");
		parallelDcol.setCellValueFactory(new Callback<CellDataFeatures<LinkResult, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<LinkResult, String> linkResultCell) {
				System.out.println(linkResultCell.getValue().getObsLinks().toString());

				return linkResultCell.getValue().getObsLinks();

			}
		});


		detailedCol.setCellFactory(
				new Callback<TableColumn<LinkResult, Boolean>, TableCell<LinkResult, Boolean>>() {
					@Override
					public TableCell<LinkResult, Boolean> call(TableColumn<LinkResult, Boolean> p) {
						return new TableCell<LinkResult, Boolean>() {
							Button button = new Button("detail");

							{              				
								button.setOnAction( e -> {
									System.out.println("detailed clicked!!! ");

									//table.getColumns().add(detailedCol);
									Stage stage = new Stage();
									LinkResult lr = (LinkResult)this.getTableRow().getItem();
									
									System.out.println("index = " + this.getIndex() + " - "+ this.getTableRow().getIndex() +  " - item:" + lr.urlName.getValue());

									TableView<LinkResult> ntable = new TableView<LinkResult>();
									ntable.setPrefWidth(Double.MAX_VALUE);
									//TableColumn col =

									Text text = new Text("Results");
									//ntable.getColumns().add(parallelDcol);
									Scene scene = new Scene(new VBox(text, ntable), 200, 200);
									stage.setScene(scene);
									stage.show();

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

		return table;
	}

	public static void main(String[] args) {
		launch(args);
	}  
}

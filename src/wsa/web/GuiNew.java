package wsa.web;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
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

import com.sun.glass.ui.Window;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
	
	public class DetailData {
		public final SimpleStringProperty urlName;
		public final SimpleStringProperty status;
		
		public DetailData (String url, String status) {
			this.urlName = new SimpleStringProperty(url);
			this.status = new SimpleStringProperty(status);			
		}
	}

	/**Classe per i risultati crawlerResults */
	public class LinkResult {
		private final SimpleStringProperty urlName;
		private final SimpleBooleanProperty linkPage;
		private final SimpleStringProperty exception;
		private final SimpleIntegerProperty outgoing;
		private final SimpleIntegerProperty incoming;
		private List<URI> uriList;
		private List<URI> uriIncomingList;

		private LinkResult(CrawlerResult result) {
			this.urlName = new SimpleStringProperty(result.uri.toString());
			this.linkPage = new SimpleBooleanProperty(result.linkPage);
			this.exception = new SimpleStringProperty(result.exc == null ? "OK" : result.exc.toString() + ": " + result.exc.getMessage());
			this.outgoing = new SimpleIntegerProperty(result.links == null ? 0 : result.links.size());
			this.incoming = new SimpleIntegerProperty(0);
			this.uriList = result.links;
			this.uriIncomingList = new ArrayList<>();
		}

/**
		public SimpleStringProperty getObsLinks() {
			String slinks = "";
			for(URI u : uriList)
				slinks = slinks + u.toString() + "\n";
			this.links = new SimpleStringProperty(slinks);
			return links;
		}
		**/
		public void add(URI uri) {
			this.uriIncomingList.add(uri);
			this.incoming.set(this.incoming.get() + 1);
		}
		
		public SimpleIntegerProperty getOutgoingLinksNumber(){
			return outgoing;
		}
		
		public SimpleBooleanProperty getLinkPage(){
			return linkPage;
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

		public String getId() {
			return urlName.get();
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
		
		private ObservableList<LinkResult> obsList = FXCollections.observableArrayList();
		private Timer timer;

		
		
		/**Costruisce un oggetto WebsiteState
		 * 
		 * @param id il nome identificativo per lo stato
		 */
		public WebsiteState(String id) {
			this.id = id;
		}
		
		/**
		 * Setta una tabella per lo stato corrente
		 * @param table la tabella da settare
		 */
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
		
		/**
		 * Setta il siteCrawler
		 * @param siteCrawler il siteCrawler da settare
		 */
		public void setSiteCrawler(SiteCrawler siteCrawler) {
			this.siteCrawler = siteCrawler;
		}

		/**
		 * Setta a parte destra della GUI
		 * @param rightVb la VBox corrispondente alla parte destra della GUI
		 */
		public void setRightVBox(VBox rightVb) {
			this.rightVb = rightVb;
		}
		
		/**
		 * Setta la parte centrale della GUI
		 * @param centerVb il VBox della parte centrale della GUI
		 */
		public void setCenterVBox(VBox centerVb) {
			this.centerVb = centerVb;
		}

		/**
		 * Setta il path
		 * @param path il path da settare
		 */
		public void setPath(Path path){
			this.path = path;
		}
		
		/**
		 * Setta il textField corrispondente al dominio
		 * @param dominio il textField corrispondente al dominio da settare
		 */
		public void setTextFieldDom(TextField dominio){
			this.dominioText = dominio;
		}

		/**
		 * Fa partire l'esplorazione
		 */
		public void start(){

				siteCrawler.start();
				this.timer = new Timer();
				
				timer.schedule( new TimerTask() {				
					@Override
					public void run() {

						Optional<CrawlerResult> cro = null;
						while((cro=siteCrawler.get()).isPresent() && cro.get().uri != null)
						{
							CrawlerResult cr = cro.get();
							for(LinkResult lr : obsList) {
								if(cr.links != null)
									if(cr.links.contains(lr.urlName.get()) )
										lr.add(cr.uri);
							}
							LinkResult lr = new LinkResult(cr);
							System.out.println("CRAWLER RESULT: " + cro.get().uri + "\t" + cro.get().links);
							obsList.add(lr);				
						}
					}
				}, 0, 1000);
			
		}
		
		/**
		 * Ritorna la parte destra della GUI
		 * @return la parte destra della GUI
		 */
		public VBox getRightVBox() { return rightVb; }

		/**
		 * Getter della parte centrale della GUI
		 * @return la parte centrale della GUI
		 */
		public VBox getCenterVBox() { return centerVb; }

		/**
		 * Ritorna il Group per la Table della GUI
		 * @return il Group per la Table della GUI
		 */
		public Parent getGroup() { return group; }
		
		/**
		 * Ritorna il path
		 * @return il path
		 */
		public Path getPath() { return path; }

		/**
		 * Ritorna i dati della tabella 
		 *@return i dati della tabella
		 */
		public ObservableList<LinkResult> getData() {
			return obsList;
		}
		
		//per test
		public void showInfo() {
			System.out.println("SeedList: " + seedsList);
			System.out.println("Path: " + path);
		}


	}

	
	public void start(Stage primaryStage) {
		Scene scene = new Scene(createUI(), 800, 600);
		primaryStage.setScene(scene);
		primaryStage.setTitle("WSA");
		primaryStage.show();
		primaryStage.setOnCloseRequest( new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
					for( WebsiteState wss : stateMap.values() ) {
						if(wss.siteCrawler != null) wss.siteCrawler.cancel();
						if(wss.timer != null) wss.timer.cancel();
					}
				//itera su statemap e chiudi i sitecrawler!!
			}
		});
	}

	/**
	 * Metodo principale per la creazione della GUI
	 * @return un Parent rappresentante la GUI
	 */
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
		stateMap.put(currentWebsite, wss1);
System.out.println("State created: website " + websiteNumber);
System.out.println("stateMap size: " + stateMap.size());
		
		
System.out.println("first state created");		
				

		//CENTER
		//prima creazione center
		VBox currentCenter = createCenter();        	
		ScrollPane spCenter = new ScrollPane(currentCenter);
		spCenter.setFitToWidth(true);  // Per far sì che il contenitore spCenter
		spCenter.setFitToHeight(true); // occupi tutto lo spazio disponibile 
		//END CENTER
		
		//Salva lo stato grafico iniziale
		wss1.setCenterVBox(currentCenter);
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
			stateMap.put(websiteNumber, nwss);
			nwss.setTable(createTableView());
System.out.println("WebsiteState " + currentWebsite + " created");			
System.out.println("new state created");			
			VBox ncurrentCenter = createCenter();
			VBox currentRight = createRightUi(ncurrentCenter);
			borderPane.setCenter(ncurrentCenter);
			borderPane.setRight(currentRight);
			Button newWebsiteB = new Button("Website " + websiteNumber);        	
			
			nwss.setCenterVBox(ncurrentCenter);
			nwss.setRightVBox(currentRight);

			newWebsiteB.setOnAction( f -> {

				currentWebsite = siteMap.get(newWebsiteB);
System.out.println("current website number selected: " + currentWebsite);
				WebsiteState wss = stateMap.get(currentWebsite);
System.out.println("WebsiteState " + currentWebsite + " loaded");
				
System.out.println("WebSite  " + currentWebsite + ":");
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

		stateMap.get(currentWebsite).setTextFieldDom(dominio);
		
		/**
		dominio.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			stateMap.get(currentWebsite).setTextFieldDom(dominio);
			});
		*/
		
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
		

		VBox vb = new VBox(addSeedB, goB);
		vb.setPrefWidth(100);
		vb.setSpacing(20);
		vb.setAlignment(Pos.TOP_CENTER);
		vb.setStyle("-fx-background-color: darkturquoise");

		goB.setOnAction( (e) -> {
			
System.out.println("dom = " + stateMap.get(currentWebsite).dominioText.getText());

			if( stateMap.get(currentWebsite).dominioText.getText().isEmpty() || stateMap.get(currentWebsite).seedsList.isEmpty() ) {
				
				createPopup("Dati insufficienti!", borderPane);
				
			}
			else {
				//Crea finestra per salvare
				Stage stage = new Stage();

				Text text = new Text("Save per salvare su disco");
				Text text2 = new Text("START per continuare");
				text.setTextAlignment(TextAlignment.CENTER);
				text2.setTextAlignment(TextAlignment.CENTER);
				
				Button start = new Button("START");
				Button saveB = new Button("Save");
				saveB.setOnAction( (eSave) -> {
					Stage saveStage = new Stage();

					DirectoryChooser directoryChooser = new DirectoryChooser();
					directoryChooser.setTitle("Open Resource File");
					
					saveStage.setOnCloseRequest( e2 -> {
						
						start.setDisable(false);
						saveB.setDisable(false);
					});
					
					final File selectedDirectory = directoryChooser.showDialog(saveStage);
					if (selectedDirectory != null) {
System.out.println(selectedDirectory.getAbsolutePath()); //Test
						stateMap.get(currentWebsite).setPath(selectedDirectory.toPath());
					}
				});
				
				
				
				HBox hbox = new HBox(saveB, start);
				hbox.setAlignment(Pos.CENTER);
				hbox.setSpacing(20);

				VBox vbPop = new VBox(text, text2, hbox);
				vbPop.setAlignment(Pos.CENTER);
				vbPop.setSpacing(10);

				Scene scene = new Scene(vbPop, 200, 200);
				stage.initModality(Modality.WINDOW_MODAL);
				stage.initOwner(borderPane.getScene().getWindow());
				stage.setScene(scene);
				stage.show();	
				
				start.setOnAction( eStart -> {
					
					URL url;
					try {
						url = new URL("http://www.google.com");
						final URLConnection conn = url.openConnection();
						conn.connect();
						createPopup("Internet signal seems ok", borderPane);
					
					//START
					WebsiteState wss = stateMap.get(currentWebsite);
					SiteCrawler siteCrawler = null;
					try
					{
						URI uri = new URI(wss.dominioText.getText());
						siteCrawler = WebFactoryWSA.getSiteCrawler(uri, wss.getPath());
						wss.setSiteCrawler(siteCrawler);
						for(TextField tf : wss.seedsList) {
							siteCrawler.addSeed(new URI(tf.getText()));
						}
					}
					catch(URISyntaxException e1)
					{
						createPopup("errore URI", borderPane);
					}
					catch(IOException e2)
					{
						createPopup("errore IO", borderPane);
					}
					catch(IllegalArgumentException e3)
					{
						createPopup("Dominio non corretto", borderPane);
						return;
					}
					
					//aggiorna la VBox per levare il tasto "save" ed aggiungere "stat"
				
					
					Button stat = new Button("Stat");
					stat.setOnAction( eStat -> {
						Stage statStage = new Stage();
					
						GridPane gridpane = new GridPane();
						ObservableList<String> names = FXCollections.observableArrayList(
					             "Visited URI", "Inner URIs", "URI errs num");
					     
						ListView<String> listView = new ListView<String>(names);
						GridPane.setConstraints(listView, 1, 1);
					     
						// don't forget to add children to gridpane
						gridpane.getChildren().addAll(listView);
				   
						Scene statScene = new Scene(gridpane,600, 600);
						listView.autosize();
						statStage.setScene(statScene);
						statStage.show();
					});
				
					VBox nvb = new VBox(addSeedB, goB, stat);
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
					//	carica lo stato associato al webSite e...
				
					TableView<LinkResult> table = createTableView(); 
					wss.setTable(table);
System.out.println("WebsiteState " + currentWebsite + " loaded");
					//..setta la nuova parte destra di GUI
					wss.setRightVBox(nvb);
					//	setta gli uri forniti
					wss.showInfo();
				
					wss.start();
					
					stage.close();

					//aggiorna il borderPane con i dati nuovi
					borderPane.setRight(nvb);
				System.out.println(currentWebsite);
					ScrollPane spCenter = new ScrollPane(wss.getGroup());
					spCenter.setFitToHeight(true);
					borderPane.setCenter(spCenter);
				}catch (Exception e1) {
					createPopup("Error with internet connection!!", borderPane);
				}
				});
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
						
						wss.siteCrawler.addSeed(new URI(newUri.getText()));						
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

	/** Crea una finestra di popup per segnalare avvisi
	 * 
	 * @param string il messaggio di avviso da visualizzare
	 */
	private void createPopup(String string, Parent parent) {
		Stage stage = new Stage();

		Text text = new Text(string);
		text.setTextAlignment(TextAlignment.CENTER);
		
		Button okButton = new Button("Ok");
		okButton.setOnAction( eOkButton -> {
			stage.close();
		});

		VBox vbPop = new VBox(text, okButton);
		vbPop.setAlignment(Pos.CENTER);
		vbPop.setSpacing(10);

		Scene scene = new Scene(vbPop, 200, 100);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(parent.getScene().getWindow());
		stage.setScene(scene);
		stage.show();		
	}

	private TableView<LinkResult> createTableView()
	{
		TableView<LinkResult> table = new TableView<LinkResult>();
		table.setPrefWidth(Double.MAX_VALUE);
			
System.out.println(stateMap.get(currentWebsite));
		ObservableList<LinkResult> obs = stateMap.get(currentWebsite).getData();
	
		/**
		try {
			List<URI> links = new ArrayList(Arrays.asList(new URI("a"),new URI("b"),new URI("c")));
			obs.add(new LinkResult(new CrawlerResult(new URI("http://www.google.it"), false, links, null, null)));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		**/
		
		table.setItems(obs);
		
		table.setEditable(true);

		TableColumn<LinkResult, String> urlNameCol = new TableColumn<LinkResult, String>("URL");
		urlNameCol.setMinWidth(300);
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
		TableColumn<LinkResult, Boolean> isIntDomCol = new TableColumn<LinkResult, Boolean>("Interno");
		isIntDomCol.setMinWidth(100);
		isIntDomCol.setCellValueFactory(new Callback<CellDataFeatures<LinkResult, Boolean>, ObservableValue<Boolean>>() {
			public ObservableValue<Boolean> call(CellDataFeatures<LinkResult, Boolean> linkResultCell) {
				return linkResultCell.getValue().getLinkPage();
			}
		});
		TableColumn<LinkResult, Number> outgoingLinksCol = new TableColumn<LinkResult, Number>("Outgoing");
		outgoingLinksCol.setMinWidth(100);
		outgoingLinksCol.setCellValueFactory(new Callback<CellDataFeatures<LinkResult, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<LinkResult, Number> linkResultCell) {
				return linkResultCell.getValue().getOutgoingLinksNumber();
			}
		});
		TableColumn<LinkResult, Number> incomingLinksCol = new TableColumn<LinkResult, Number>("Incoming");
		incomingLinksCol.setMinWidth(100);
		incomingLinksCol.setCellValueFactory(new Callback<CellDataFeatures<LinkResult, Number>, ObservableValue<Number>>() {
			public ObservableValue<Number> call(CellDataFeatures<LinkResult, Number> linkResultCell) {
				return linkResultCell.getValue().incoming;
			}
		});
		TableColumn<LinkResult, Boolean> detailedCol = new TableColumn<LinkResult, Boolean>("DETAILS");
		detailedCol.setMinWidth(100);
		//detailedCol.setCellValueFactory(new Callback<CellDataFeatures<LinkResult, String>, ObservableValue<String>>() {
		//  public ObservableValue<String> call(CellDataFeatures<LinkResult, String> linkResultCell) {
		//    return linkResultCell.getValue().getStatus();
		//}
		// });


//		TableColumn<LinkResult, String> parallelDcol = new TableColumn<LinkResult, String>("empty");
//		parallelDcol.setCellValueFactory(new Callback<CellDataFeatures<LinkResult, String>, ObservableValue<String>>() {
//			public ObservableValue<String> call(CellDataFeatures<LinkResult, String> linkResultCell) {
//				System.out.println(linkResultCell.getValue().getObsLinks().toString());
//
//				return linkResultCell.getValue().getObsLinks();
//
//			}
//		});
		
		/**
		 * Future<LoadResult> future= usare webfactoryWSA.getAsyncloader.submit nuovo uri
		 * loadresutl prendersi il future.get
		 * parsed p = resutl.parsed
		 * p.vist(mio consumer)
		 * mio consumer incrementa contatore ecc
		 * mio consumer è il numero nodi albero
		 */

		table.getSelectionModel().selectedItemProperty().addListener(
		    (observable, oldValue, newValue) -> {

                try {
                	
    		    	String urlToShow = newValue.getUrlName().get();
    			    
                    WebView wView = new WebView();
                    WebEngine we = wView.getEngine();
                    Stage stage = new Stage();
                    
                    WebsiteState wss = stateMap.get(currentWebsite);
                    String dominio = wss.dominioText.getText();
                    System.out.println("____--======--____ : " + dominio);
                    
                    LinkResult lr = table.getSelectionModel().selectedItemProperty().get();
					System.out.println("%%%%%%% : " + lr.urlName);
				
					ListView<URI> inList = new ListView<URI>();
					inList.setMinWidth(200);
					inList.setMinHeight(580);
					
					List<URI> uriIncomingList = lr.uriIncomingList;
			
					ObservableList<URI> items =FXCollections.observableArrayList(uriIncomingList);
					inList.setItems(items);
					Label inLabel = new Label("Incoming");
					VBox inVbox = new VBox(inLabel, inList);
					inVbox.setAlignment(Pos.CENTER);
					
					inVbox.setMinHeight(600);
					
					HBox hbox;
					
					List<ListView<URI>> list = new ArrayList<ListView<URI>>();
					List<VBox> vbList = new ArrayList<>();
					list.add(inList);
					vbList.add(inVbox);
					
					if(!SiteCrawlerC.checkDomain(new URI(urlToShow))){

						hbox = new HBox(wView, inVbox);
					}
					else {
						Label outLabel = new Label("Outgoing");
						
						List<URI> uriOutgoingList = lr.uriList;
						
						ListView<URI> outList = new ListView<URI>();
						outList.setMinHeight(580);
						outList.setMinWidth(200);
						
						list.add(outList);
						
						ObservableList<URI> outItems =FXCollections.observableArrayList(uriOutgoingList);
						outList.setItems(outItems);
						
						VBox outVbox = new VBox(outLabel, outList);
						outVbox.setAlignment(Pos.CENTER);
						outVbox.setMinHeight(600);
						vbList.add(outVbox);
						
						
						hbox = new HBox(wView, inVbox, outVbox);
						hbox.setAlignment(Pos.CENTER);
					}
					we.load(urlToShow);
	            	Scene scene = new Scene(hbox, 800, 600);
	            	
	            	/**
	                scene.widthProperty().addListener( new ChangeListener<Number>() {
	                    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
	                    	hbox.setMinWidth((double)newSceneWidth - wView.getWidth());
	                    	for(ListView<URI> lv : list){
	                    		lv.setMinWidth(hbox.widthProperty().get()/2);
	                    	}
	                    		
	                    }
	                });
	                **/
	                scene.heightProperty().addListener( new ChangeListener<Number>() {
	                    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
	                    	hbox.setMinHeight((double)newSceneHeight);
	                    	for(VBox vb : vbList) {
	                    		vb.setMinHeight((double)newSceneHeight-20);
	                    	}
	                    	for(ListView<URI> lv : list){
	                    		lv.setMinHeight((double)newSceneHeight-20);
	                    	}
	                    }
	                });
	            	
	            	stage.setScene(scene);
	            	stage.show();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
                

            }
		);

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
System.out.println("URI LIST:" + lr.uriList.size() + " " + lr.uriList);
									TableView<DetailData> detailTable = new TableView<DetailData>();
									detailTable.setPrefWidth(Double.MAX_VALUE);
									//TableColumn col =
									
									TableColumn<DetailData, String> urlNameCol = new TableColumn<DetailData, String>("URL");
									urlNameCol.setMinWidth(100);
									urlNameCol.setCellValueFactory( new Callback<TableColumn.CellDataFeatures<DetailData,String>, ObservableValue<String>>() {
										@Override
										public ObservableValue<String> call(
												CellDataFeatures<DetailData, String> param) {
											return param.getValue().urlName;
										}
									});
									
									TableColumn<DetailData, String> statusCol = new TableColumn<DetailData, String>("DOWNLOADED");
									statusCol.setMinWidth(100);
									statusCol.setCellValueFactory( new Callback<TableColumn.CellDataFeatures<DetailData, String>, ObservableValue<String>>() {
										@Override
										public ObservableValue<String> call(
												CellDataFeatures<DetailData, String> param) {
											return param.getValue().status;
										}
									});
									
									detailTable.getColumns().add(urlNameCol);
									detailTable.getColumns().add(statusCol);
									
									ObservableList<DetailData> details = FXCollections.observableArrayList();
									for( URI u : lr.uriList) {
										SiteCrawler sc = stateMap.get(currentWebsite).siteCrawler;
										//System.out.println(sc.isRunning());
										Boolean loaded = sc.getLoaded().contains(u);
										Boolean err = sc.getErrors().contains(u);
										String risult = "";
										if( !loaded ) risult = "not loaded";
										else if( loaded && !err) risult = "downloaded";
										else risult = "error";
										details.add( new DetailData(u.toString(), risult) );
									}
									detailTable.setItems(details);
									
									Text text = new Text("Details table");
									//ntable.getColumns().add(parallelDcol);
									Scene scene = new Scene(new VBox(text, detailTable), 200, 200);
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
		table.getColumns().add(isIntDomCol);
		table.getColumns().add(outgoingLinksCol);
		table.getColumns().add(detailedCol);
		table.getColumns().add(incomingLinksCol);

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

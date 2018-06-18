package gui;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import clienteOdysseyV2.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class MainWindowController {
	 /**
     * Paginas de canciones en memoria
     */
    TablePage[] tablePages = new TablePage[3];

    /**
     * Lista de canciones en memoria
     */
    ObservableList<Metadata> tableList = FXCollections.observableArrayList();

    /**
     * Boton de reproducir/pausar
     */
    @FXML
    private ImageView playPauseBtn;

    /**
     * Deslizador para la reproduccion
     */
    @FXML
    private JFXSlider songSlider;

    @FXML
    private JFXTextField searchTextfield;

    /**
     * Lista de amigos
     */
    @FXML
    private JFXListView<?> friendsList;

    /**
     * Tabla con la lista de canciones
     */
    @FXML
    private JFXTreeTableView<Metadata> songList;

    @FXML
    private JFXComboBox<SortConfig> sortCombo;

    @FXML
    private JFXTextField beforeGuess;

    @FXML
    private Label guessingAnswer;

    @FXML
    private JFXTextField afterGuess;

    /**
     * Columna con el nombre
     */
    private JFXTreeTableColumn<Metadata, String> nameColumn = new JFXTreeTableColumn<>("Name");

    /**
     * Columna con el artista
     */
    private JFXTreeTableColumn<Metadata, String> artistColumn = new JFXTreeTableColumn<>("Artist");

    /**
     * Columna con el anno
     */
    private JFXTreeTableColumn<Metadata, String> yearColumn = new JFXTreeTableColumn<>("Year");


    /**
     * Columna con el album
     */
    private JFXTreeTableColumn<Metadata, String> albumColumn = new JFXTreeTableColumn<>("Album");


    /**
     * Columna con el genero
     */
    private JFXTreeTableColumn<Metadata, String> genreColumn = new JFXTreeTableColumn<>("Genre");

    int currentPage;

    ScrollBar scrollBar;

    int currentlyPlaying;

    String sortBy = "name";

    String sortWith = "quickSort";

    @FXML
    private JFXProgressBar volumeVisualizer;

    /**
     * Configuracion inicial de la vista
     */
    @FXML
    private void initialize(){
        nameColumn.prefWidthProperty().bind(songList.widthProperty().divide(4));
        nameColumn.setStyle("-fx-alignment: CENTER;");
        nameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Metadata, String> param) -> {
            ObservableValue<String> property = new ReadOnlyObjectWrapper<String>(param.getValue().getValue().name);
            return property;
        });

        artistColumn.prefWidthProperty().bind(songList.widthProperty().divide(5));
        artistColumn.setStyle("-fx-alignment: CENTER;");
        artistColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Metadata, String> param) -> {
            ObservableValue<String> property = new ReadOnlyObjectWrapper<String>(param.getValue().getValue().artist);
            return property;
        });

        albumColumn.prefWidthProperty().bind(songList.widthProperty().divide(4));
        albumColumn.setStyle("-fx-alignment: CENTER;");
        albumColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Metadata, String> param) -> {
            ObservableValue<String> property = new ReadOnlyObjectWrapper<String>(param.getValue().getValue().album);
            return property;
        });

        yearColumn.prefWidthProperty().bind(songList.widthProperty().divide(10));
        yearColumn.setStyle("-fx-alignment: CENTER;");
        yearColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Metadata, String> param) -> {
            ObservableValue<String> property = new ReadOnlyObjectWrapper<String>(param.getValue().getValue().year);
            return property;
        });

        genreColumn.prefWidthProperty().bind(songList.widthProperty().divide(6));
        genreColumn.setStyle("-fx-alignment: CENTER;");
        genreColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Metadata, String> param) -> {
            ObservableValue<String> property = new ReadOnlyObjectWrapper<String>(param.getValue().getValue().genre);
            return property;
        });

        final TreeItem<Metadata> root = new RecursiveTreeItem<>(tableList, RecursiveTreeObject::getChildren);

        songList.setRoot(root);
        songList.setShowRoot(false);
        songList.setEditable(false);
        songList.getColumns().setAll(nameColumn,artistColumn,albumColumn,yearColumn, genreColumn);
        songList.setPlaceholder(new Label("Song library is empty. Click on Upload to add new songs."));

        updateSongs();

        songSlider.setMin(0);
        songSlider.setMax(100);
        songSlider.setValue(0);
        MusicPlayer.getInstance().setSlider(songSlider);

        sortCombo.setCellFactory((sortCombo)->{
            return new ListCell<SortConfig>(){
                @Override
                protected void updateItem(SortConfig item, boolean empty){
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.attribute.substring(0, 1).toUpperCase() + item.attribute.substring(1));
                    }
                }
            };
        });

        sortCombo.setConverter(new StringConverter<SortConfig>() {
            @Override
            public String toString(SortConfig object) {
                return object.attribute.substring(0, 1).toUpperCase() + object.attribute.substring(1);
            }

            @Override
            public SortConfig fromString(String string) {
                return null;
            }
        });

        sortCombo.getItems().add(new SortConfig("name", "quickSort"));
        sortCombo.getItems().add(new SortConfig("artist", "radixSort"));
        sortCombo.getItems().add(new SortConfig("album", "bubbleSort"));

        sortCombo.getSelectionModel().select(0);

        VisualizerThread visualizer = new VisualizerThread(MusicPlayer.getInstance(), volumeVisualizer);
        visualizer.start();

        songList.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                if(scrollBar == null){
                    scrollBar = getVerticalScrollbar(songList);

                    scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                    if(newValue.doubleValue() == scrollBar.getMin() && currentPage != 1){
//                        currentPage--;
//                        tableList.removeAll(tablePages[2].songs);
//                        tablePages[2] = tablePages[1];
//                        tablePages[1] = tablePages[0];
//                        tablePages[0] = populateTable(currentPage - 1);
//                        tableList.addAll(0, tablePages[0].songs); // No funciona, anade al final
//                        songList.refresh();
//                    }
                            if(newValue.doubleValue() == scrollBar.getMax() && tablePages[2].songs.size() == 10){
                                double oldMax = scrollBar.getMax();
                                currentPage++;
//                        tableList.removeAll(tablePages[0].songs);
                                tablePages[0] = tablePages[1];
                                tablePages[1] = tablePages[2];
                                tablePages[2] = populateTable(currentPage + 1);
                                tableList.addAll(tablePages[2].songs);
                                songList.refresh();
                                scrollBar.setValue(oldMax);
                            }
                        }
                    });
                }
            }
        });
    }

    @FXML
    void search(ActionEvent event) {
        if(searchTextfield.getText().isEmpty()) {
            updateSongs();
            return;
        }
        tableList.clear();
        Document request = DocumentHelper.createDocument();
        Element root = request.addElement("request").addAttribute("opcode", "6");
        root.addElement("search").addText(searchTextfield.getText());

        ResponseHandler searchHandler = NioClient.getInstance().send(request.asXML().getBytes());

        System.out.println(searchHandler.getStrResponse());
        try {
            Document response = searchHandler.getXmlResponse();
            Element responseRoot = response.getRootElement();
            Element songs = responseRoot.elementIterator("songs").next();
            for (Element song : songs.elements()) {
                Metadata newSong = new Metadata();

                newSong.name = song.elementIterator("name").next().getText();
                newSong.album = song.elementIterator("album").next().getText();
                newSong.artist = song.elementIterator("artist").next().getText();
                newSong.genre = song.elementIterator("genre").next().getText();
                newSong.year = song.elementIterator("year").next().getText();
                newSong.lyrics = song.elementIterator("lyrics").next().getText();
                tableList.add(newSong);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Handler para reproducir la siguiente cancion
     * @param event
     */
    @FXML
    void nextSong(ActionEvent event) {
        Metadata metadata = tableList.get(++currentlyPlaying);
        songList.getSelectionModel().select(currentlyPlaying);
        MusicPlayer.getInstance().play(metadata, 0);
    }

    /**
     * Handler para reproducir/pausar la cancion
     * @param event
     */
    @FXML
    void playPauseSong(ActionEvent event) {
        MusicPlayer player = MusicPlayer.getInstance();
        if(player.isPlaying()){
            player.pause();
            playPauseBtn.setImage(new Image("org/tec/datosII/OdysseyClient/UI/icons/play.png"));
        }else if(player.isPaused()){
            player.unpause();
            playPauseBtn.setImage(new Image("org/tec/datosII/OdysseyClient/UI/icons/pause.png"));
        }else{
            TreeItem<Metadata> treeItem = songList.getSelectionModel().getSelectedItem();
            if(treeItem == null){
                return;
            }
            MusicPlayer.getInstance().play(treeItem.getValue(), 0);
            currentlyPlaying = songList.getSelectionModel().getSelectedIndex();
            playPauseBtn.setImage(new Image("org/tec/datosII/OdysseyClient/UI/icons/pause.png"));
        }

    }

    /**
     * Handler para reproducir la cancion desde la lista
     * @param event
     */
    @FXML
    void playSong(MouseEvent event) {
        if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {

            TreeItem<Metadata> treeItem = songList.getSelectionModel().getSelectedItem();
            if(treeItem == null){
                return;
            }
            MusicPlayer.getInstance().play(treeItem.getValue(), 0);
            currentlyPlaying = songList.getSelectionModel().getSelectedIndex();
            playPauseBtn.setImage(new Image("org/tec/datosII/OdysseyClient/UI/icons/pause.png"));
        }
    }

    /**
     * Handler para reproducir la cancion anterior
     * @param event
     */
    @FXML
    void prevSong(ActionEvent event) {
        Metadata metadata = tableList.get(--currentlyPlaying);
        songList.getSelectionModel().select(currentlyPlaying);
        MusicPlayer.getInstance().play(metadata, 0);
    }

    /**
     * Handler para cargar nuevas paginas a la lista
     * @param event
     */
    @FXML
    void scrollHandler(ScrollEvent event) {

    }

    /**
     * Handler para reordenar la canciones
     * @param event
     */
    @FXML
    void sort(ActionEvent event) {
        SortConfig config = sortCombo.getValue();
        sortBy = config.attribute;
        sortWith = config.algorithm;
        updateSongs();
    }

    @FXML
    void sliderChanged(MouseEvent event){
        MusicPlayer.getInstance().forward((int) songSlider.getValue());
        playPauseBtn.setImage(new Image("org/tec/datosII/OdysseyClient/UI/icons/pause.png"));
    }

    /**
     * Handler para seleccionar las canciones a subir al servidor
     * @param event
     */
    @FXML
    void uploadSong(ActionEvent event){
        FileChooser browser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Audio files", "*.mp3");
        browser.getExtensionFilters().setAll(filter);
        browser.setTitle("Select songs to upload");
        List<File> files = browser.showOpenMultipleDialog(App.getRootStage());
        if(files != null) {
            MusicPlayer.getInstance().pause();
            for (File file : files) {
                uploadToServer(file);
                System.out.println(file.getName());
            }
        }
        updateSongs();
    }

    /**
     * Metodo para subir un archivo al servidor con toda su metadata asociada
     * @param file
     */
    void uploadToServer(File file){

        Metadata metadata = new Metadata(file.toPath().toString());

        metadata.addLyrics();

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("request").addAttribute("opcode", "3");

        root.addElement("name").addText(metadata.name);
        root.addElement("artist").addText(metadata.artist);
        root.addElement("year").addText(metadata.year);
        root.addElement("album").addText(metadata.album);
        root.addElement("genre").addText(metadata.genre);
        root.addElement("lyrics").addText(metadata.lyrics);

        Element cover = root.addElement("cover");
        if(metadata.cover != null) {
            String imageString = null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(metadata.cover, null), "png", bos);
                byte[] imageBytes = bos.toByteArray();

                String encodedFile = Base64.getEncoder().encodeToString(imageBytes);
                cover.addText(encodedFile);

                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        Element content = root.addElement("content");

        try {
            byte[] binaryFile = Files.readAllBytes(file.toPath());

            String encodedFile = Base64.getEncoder().encodeToString(binaryFile);
            content.addText(encodedFile);
        }catch (Exception e){
            e.printStackTrace();
        }

        String request = document.asXML();

        NioClient client = NioClient.getInstance();
        ResponseHandler handler = client.send(request.getBytes());

    }

    /**
     * Handler para el menu de cada cancion
     * @param event
     */
    @FXML
    void contextMenu(ContextMenuEvent event){
        ContextMenu altMenu = new ContextMenu();
        MenuItem properties = new MenuItem("Properties");
        properties.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                try {
                    Metadata selected = songList.getSelectionModel().getSelectedItem().getValue();
                    PropertiesDialog dialog = new PropertiesDialog();
                    Metadata updated = dialog.showAndWait(selected);
                    if(updated != null){
                        Document document = DocumentHelper.createDocument();
                        Element root = document.addElement("request").addAttribute("opcode", "9");
                        root.addElement("name").addText(selected.name);
                        root.addElement("artist").addText(selected.artist);
                        root.addElement("album").addText(selected.album);
                        root.addElement("year").addText(selected.year);
                        root.addElement("genre").addText(selected.genre);
                        root.addElement("lyrics").addText(selected.lyrics);
                        root.addElement("newName").addText(updated.name);
                        root.addElement("newArtist").addText(updated.artist);
                        root.addElement("newYear").addText(updated.year);
                        root.addElement("newAlbum").addText(updated.album);
                        root.addElement("newGenre").addText(updated.genre);
                        root.addElement("newLyrics").addText(updated.lyrics);
                        Element cover = root.addElement("newCover");
                        if(updated.cover != null) {
                            String imageString = null;
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            ImageIO.write(SwingFXUtils.fromFXImage(updated.cover, null), "png", bos);
                            byte[] imageBytes = bos.toByteArray();

                            String encodedFile = Base64.getEncoder().encodeToString(imageBytes);
                            cover.addText(encodedFile);

                            bos.close();
                        }

                        String xmlRequest = document.asXML();
                        System.out.println(xmlRequest);

                        ResponseHandler handler = NioClient.getInstance().send(document.asXML().getBytes());
                        System.out.println(handler.getStrResponse());
                        updateSongs();
                    }
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });

        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Document document = DocumentHelper.createDocument();
                Element root = document.addElement("request").addAttribute("opcode", "8");

                Metadata song = songList.getSelectionModel().getSelectedItem().getValue();

                root.addElement("name").addText(song.name);
                root.addElement("artist").addText(song.artist);
                root.addElement("year").addText(song.year);
                root.addElement("album").addText(song.album);
                root.addElement("genre").addText(song.genre);

                NioClient.getInstance().send(document.asXML().getBytes());

                updateSongs();
            }
        });

        altMenu.getItems().add(delete);

        altMenu.getItems().add(properties);

        altMenu.show(App.getRootStage(), event.getScreenX(), event.getScreenY());
    }

    /**
     * Handler para cargar una pagina de canciones a memoria
     * @param pageNumber Numero de la pagina a solicitar
     * @return TablePage con los datos de las canciones
     */
    private TablePage populateTable(int pageNumber){
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("request").addAttribute("opcode", "4");
        root.addElement("sortBy").addText(sortBy);
        root.addElement("sortWith").addText(sortWith);
        root.addElement("page").addText(String.valueOf(pageNumber));

        NioClient client = NioClient.getInstance();
        String request = document.asXML();
        ResponseHandler handler = client.send(request.getBytes());

        TablePage page = null;
        try {
            page = new TablePage();
            Document response = handler.getXmlResponse();
            Element responseRoot = response.getRootElement();
            page.pageNumber = pageNumber;
            page.totalSongs = Integer.parseInt(responseRoot.elementIterator("numberOfSongs").next().getText());
            page.pages = Integer.parseInt(responseRoot.elementIterator("pages").next().getText());
            page.pageSize = Integer.parseInt(responseRoot.elementIterator("pageSize").next().getText());

            Element songs = responseRoot.elementIterator("songs").next();
            for (Element song : songs.elements()) {
                Metadata newSong = new Metadata();

                newSong.name = song.elementIterator("name").next().getText();
                newSong.album = song.elementIterator("album").next().getText();
                newSong.artist = song.elementIterator("artist").next().getText();
                newSong.genre = song.elementIterator("genre").next().getText();
                newSong.year = song.elementIterator("year").next().getText();
                newSong.lyrics = song.elementIterator("lyrics").next().getText();
                page.songs.add(newSong);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }


        return page;
    }

    /**
     * Handler para actualizar la lista de canciones
     */
    private void updateSongs(){
        tableList.clear();
        currentPage = 1;

        tablePages[0] = populateTable(0);
        tableList.addAll(tablePages[0].songs);

        tablePages[1] = populateTable(1);
        tableList.addAll(tablePages[1].songs);

        tablePages[2] = populateTable(2);
        tableList.addAll(tablePages[2].songs);
    }

    private ScrollBar getVerticalScrollbar(JFXTreeTableView<?> table) {
        ScrollBar result = null;
        for (Node n : table.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }
        return result;
    }

    @FXML
    void startGuess(ActionEvent event) {
        String before = beforeGuess.getText();
        String after = afterGuess.getText();

        Document request = DocumentHelper.createDocument();
        Element root = request.addElement("request").addAttribute("opcode", "7");
        root.addElement("left").addText(before);
        root.addElement("right").addText(after);

        ResponseHandler handler = NioClient.getInstance().send(request.asXML().getBytes());

        System.out.println(handler.getStrResponse());

        try {
            Document response = handler.getXmlResponse();
            Element words = response.getRootElement().elementIterator("words").next();

            List<String> population = new ArrayList<>(Integer.parseInt(response.getRootElement().elementIterator("numberOfWords").next().getText()));
            for(Element word: words.elements()){
                population.add(word.getText());
            }

            LyricsGuessing.population = population;

            if(population.size() > 0) {
                guessingAnswer.setText(population.get(0));
            }
        }catch (Exception ex){}

    }

    @FXML
    void closeGuess(ActionEvent event) {
        LyricsGuessing.evolve(guessingAnswer.getText(), 1, 1);
        if(LyricsGuessing.population.size() > 0) {
            guessingAnswer.setText(LyricsGuessing.population.get(0));
        }
    }

    @FXML
    void farGuess(ActionEvent event) {
        LyricsGuessing.evolve(guessingAnswer.getText(), -1, 1);
        if(LyricsGuessing.population.size() > 0) {
            guessingAnswer.setText(LyricsGuessing.population.get(0));
        }
    }

    @FXML
    void maybeGuess(ActionEvent event) {
        LyricsGuessing.evolve(guessingAnswer.getText(), 0, 1);
        if(LyricsGuessing.population.size() > 0) {
            guessingAnswer.setText(LyricsGuessing.population.get(0));
        }
    }

    @FXML
    void rightGuess(ActionEvent event) {
        searchTextfield.setText(guessingAnswer.getText());
        search(new ActionEvent());
    }
}

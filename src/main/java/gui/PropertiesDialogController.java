package gui;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import clienteOdysseyV2.Metadata;

public class PropertiesDialogController {
	/**
     * Textfield con el nombre de la cancion
     */

    @FXML
    private JFXTextField nameTextfield;
    /**
     * Textfield con el nombre del artista
     */

    @FXML
    private JFXTextField artistTextfield;

    /**
     * Textfield con el ano
     */

    @FXML
    private JFXTextField yearTextfield;

    /**
     * Textfield con el album
     */

    @FXML
    private JFXTextField albumTextfield;

    /**
     * Textfield con el genero
     */

    @FXML
    private JFXTextField genreTextfield;

    @FXML
    private JFXTextArea lyricsText;

    /**
     * Imagen de portada del album
     */
    @FXML
    private ImageView coverArt;

    boolean confirm = false;

    Metadata original;

    /**
     * Handler de cancelar
     * @param event
     */
    @FXML
    void cancelBtn(ActionEvent event) {
        Stage stage = (Stage) nameTextfield.getScene().getWindow();
        stage.close();
    }

    /**
     * Handler para cambiar la imagen
     * @param event
     */
    @FXML
    void changeCoverArt(MouseEvent event) {

    }

    /**
     * Handler para guardar los datos
     * @param event
     */
    @FXML
    void saveBtn(ActionEvent event) {
        Stage stage = (Stage) nameTextfield.getScene().getWindow();
        stage.close();
        if(nameTextfield.getText().equals(original.name) &&
                artistTextfield.getText().equals(original.artist) &&
                yearTextfield.getText().equals(original.year) &&
                albumTextfield.getText().equals(original.album) &&
                genreTextfield.getText().equals(original.genre) &&
                lyricsText.getText().equals(original.lyrics) &&
                coverArt.getImage() == original.cover){
            confirm = false;
        }else {
            confirm = true;
        }
    }

    /**
     * Carga los datos de la cancion a la interfaz
     * @param metadata Metadata de la cancion
     */
    public void load(Metadata metadata){
        original = metadata;
        nameTextfield.setText(metadata.name);
        artistTextfield.setText(metadata.artist);
        yearTextfield.setText(metadata.year);
        albumTextfield.setText(metadata.album);
        genreTextfield.setText(metadata.genre);
        lyricsText.setText(metadata.lyrics);
        coverArt.setImage(metadata.cover);
    }

    /**
     * Obtiene los datos de la cancion editados
     * @return Metadata de la cancion
     */
    public Metadata retrieve() {
        if(confirm) {
            Metadata metadata = new Metadata();
            metadata.name = nameTextfield.getText();
            metadata.artist = artistTextfield.getText();
            metadata.year = yearTextfield.getText();
            metadata.album = albumTextfield.getText();
            metadata.genre = genreTextfield.getText();
            metadata.lyrics = lyricsText.getText();
            metadata.cover = coverArt.getImage();
            return metadata;
        }
        return null;
    }
}

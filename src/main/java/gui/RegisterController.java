package gui;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import clienteOdysseyV2.App;


import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;


public class RegisterController {
	 /**
     * Controlador de la ventana inicial
     */
    private InitController initController;

    /**
     * Establece el controlador de la ventana inicial
     * @param controller Controlador inicial
     */
    public void setInitController(InitController controller){
        this.initController = controller;
    }

    /**
     * Label de error
     */
    @FXML
    private Label errorLabel;

    /**
     * Texfield con el nombre
     */
    @FXML
    private JFXTextField firstNameTextfield;

    /**
     * Textfield con el apellido
     */
    @FXML
    private JFXTextField lastNameTextfield;

    /**
     * Texfield con el nombre de usuario
     */
    @FXML
    private JFXTextField userTextfield;

    /**
     * Selector de fecha de nacimiento
     */
    @FXML
    private JFXDatePicker birthdayPicker;

    /**
     * Textfield con la contrasena
     */
    @FXML
    private JFXPasswordField passwordTextfield;

    /**
     * Texfield para repetir la contrasena
     */
    @FXML
    private JFXPasswordField repeatPasswordTextfield;

    /**
     * CheckBox de Clasica
     */
    @FXML
    private JFXCheckBox classicCheck;

    /**
     * CheckBox de Reggaeton
     */
    @FXML
    private JFXCheckBox reggaetonCheck;

    /**
     * CheckBox de Pop
     */
    @FXML
    private JFXCheckBox popCheck;

    /**
     * CheckBox de Electronica
     */
    @FXML
    private JFXCheckBox electronicCheck;

    /**
     * CheckBox de Indie
     */
    @FXML
    private JFXCheckBox indieCheck;

    /**
     * CheckBox de Jazz
     */
    @FXML
    private JFXCheckBox jazzCheck;

    /**
     * CheckBox de Rock
     */
    @FXML
    private JFXCheckBox rockCheck;

    /**
     * CheckBox de Metal
     */
    @FXML
    private JFXCheckBox metalCheck;

    /**
     * CheckBox de Country
     */
    @FXML
    private JFXCheckBox countryCheck;

    /**
     * Cambiar a ventana de login
     * @param event
     * @throws Exception
     */
    @FXML
    void changeToLogin(ActionEvent event) throws Exception{
        initController.loadLogin();
    }

    /**
     * Ejecuta el registro en el servidor
     * @param event
     * @throws Exception
     */
    @FXML
    void register(ActionEvent event) throws Exception{
        String fname = firstNameTextfield.getText();
        String lname = lastNameTextfield.getText();
        String user = userTextfield.getText();
        LocalDate birthday = birthdayPicker.getValue();
        String password = passwordTextfield.getText();
        String confirmPass = repeatPasswordTextfield.getText();

        JFXCheckBox[] genres = {classicCheck, reggaetonCheck, popCheck, electronicCheck, indieCheck, jazzCheck, rockCheck, metalCheck, countryCheck};

        LinkedList<String> selectedGenres = new LinkedList<String>();

        for (JFXCheckBox genre : genres) {
            if(genre.isSelected()){
                selectedGenres.add(genre.getText());
            }
        }

        Authenticator auth = new Authenticator();

        if(fname.isEmpty() || lname.isEmpty() || user.isEmpty() || password.isEmpty() || selectedGenres.isEmpty() || confirmPass.isEmpty()) {
            errorLabel.setText("All fields are required.");
        }else if(!password.equals(confirmPass)){
            errorLabel.setText("Passwords don't match");
        }else if(auth.register(fname, lname, user, password, birthday, selectedGenres.toArray(new String[selectedGenres.size()]))){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
            Parent page = loader.load();
            Scene mainScene = new Scene(page, App.width, App.height);

            App.getRootStage().setScene(mainScene);
            App.getRootStage().sizeToScene();
        }else{
            errorLabel.setText("Username is already taken.");
        }
    }
}

package gui;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import clienteOdysseyV2.App;
import clienteOdysseyV2.Authenticator;
public class LoginController {

    /**
     * Controlador de la ventana inicial
     */
    private InitController initController;

    /**
     * Establecer el controlador de la ventana inicial
     * @param controller Controlador inicial
     */
    public void setInitController(InitController controller){
        this.initController = controller;
    }

    /**
     * Textfield del nombre de usuario
     */
    @FXML
    private JFXTextField userTextfield;

    /**
     * Textfiel de la contrasena
     */
    @FXML
    private JFXPasswordField passwordTextfield;

    /**
     * Label de error
     */
    @FXML
    private Label errorLabel;

    /**
     * Cambiar ventana a registro
     * @param event
     * @throws Exception
     */
    @FXML
    void changeToRegister(ActionEvent event) throws Exception {
        initController.loadRegister();
    }

    /**
     * Cambiar enfoque a la casilla de contrasena
     * @param event
     */
    @FXML
    void focusPass(ActionEvent event){
        passwordTextfield.requestFocus();
    }

    /**
     * Ejecutar login
     * @param event
     * @throws Exception
     */
    @FXML
    void login(ActionEvent event) throws Exception{
        String user = userTextfield.getText();
        String password = passwordTextfield.getText();

        Authenticator auth = new Authenticator();

        if(user.isEmpty() || password.isEmpty()){
            errorLabel.setText("All fields are required.");
        }else if(auth.login(user, password)){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
            Parent page = loader.load();
            Scene mainScene = new Scene(page, App.width, App.height);

            App.getRootStage().setScene(mainScene);
            App.getRootStage().sizeToScene();
        }else{
            errorLabel.setText("Username or password is incorrect.");
        }
    }
}

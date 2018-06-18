package gui;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
public class InitController {
	/**
     * Ventana de entrada de datos
     */
    @FXML
    private AnchorPane inputPane;
    /**
     * Loader de la ventana de login
     */
    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
    /**
     * Escena de login
     */
    Node loginScene;

    /**
     * Loader de la ventana de registro
     */
    FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("register.fxml"));
    /**
     * Escena de registro
     */
    Node registerScene;

    /**
     * Constructor con la carga de las ventanas de login y registro
     * @throws Exception
     */
    public InitController() throws Exception{
        loginScene = (Node) loginLoader.load();
        LoginController loginController = loginLoader.getController();
        loginController.setInitController(this);

        registerScene = (Node) registerLoader.load();
        RegisterController registerController = registerLoader.getController();
        registerController.setInitController(this);
    }

    /**
     * Carga de ventana de login en pantalla
     * @throws Exception
     */
    public void loadLogin() throws Exception{
        inputPane.getChildren().setAll(loginScene);
    }

    /**
     * Carga de ventana de registro en pantalla
     * @throws Exception
     */
    public void loadRegister() throws Exception{
        inputPane.getChildren().setAll(registerScene);
    }
}

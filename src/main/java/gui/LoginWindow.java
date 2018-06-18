package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class LoginWindow {
	/**
     * Carga la escena con la ventana inicial
     * @param width Ancho de la ventana
     * @param height Alto de la ventana
     * @return Scene de la ventana
     * @throws Exception
     */
    public Scene load(int width, int height) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("init.fxml"));
        Parent root = loader.load();
        Scene loginScene = new Scene(root, width, height);

        InitController controller = loader.getController();
        controller.loadLogin();

        return loginScene;
    }
}

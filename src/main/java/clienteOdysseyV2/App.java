package clienteOdysseyV2;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import gui.LoginWindow;
import java.net.InetAddress;

public class App extends Application {
    /**
     * Stage principal de la aplicacion
     */
    private static Stage rootStage;
    /**
     * Ancho por defecto de la ventana
     */
    public static int width = 1280;
    /**
     * Alto por defecto de la ventana
     */
    public static int height = 720;
    /**
     * Instancia de la aplicacion
     */
    private static App instance;

    public App(){
        instance = this;
    }

    /**
     * Metodo de inicio de la aplicacion
     * @param primaryStage stage inicial a cargar
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        setRootStage(primaryStage);
        primaryStage.setTitle("Odissey");

        primaryStage.setOnCloseRequest(event->{System.exit(0);});

        LoginWindow loginWindow = new LoginWindow();

        primaryStage.setScene(loginWindow.load(width, height));
        primaryStage.show();
    }

    /**
     * Metodo para obtener el stage principal de la aplicacion
     * @return stage principal
     */
    public static Stage getRootStage() {
        return rootStage;
    }

    /**
     * Metodo para establecer el stage principal de la aplicacion
     * @param rootStage
     */
    public static void setRootStage(Stage rootStage) {
        App.rootStage = rootStage;
    }

    /**
     * Metodo inicial del programa
     * @param args
     */
    public static void main(String[] args){
        try{
            NioClient client = NioClient.getInstance();
            client.setUp(InetAddress.getLocalHost(), 2000);

        } catch (Exception e) {
            e.printStackTrace();
        }
        launch(args);
    }

}
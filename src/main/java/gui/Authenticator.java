package gui;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import clienteOdysseyV2.NioClient;
import clienteOdysseyV2.ResponseHandler;

import java.io.IOException;
import java.time.LocalDate;

public class Authenticator {
	 /* Login de un usuario con el servidor
     * @param userStr Nombre de usuario
     * @param passwordStr Contrasena
     * @return boolean que indica si se concedio el acceso
     */
    public boolean login(String userStr, String passwordStr) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("request").addAttribute("opcode", "1");
        root.addElement("username").addText(userStr);
        root.addElement("password").addText(passwordStr);

        String request = document.asXML();

        NioClient client = NioClient.getInstance();

        ResponseHandler loginHandler = client.send(request.getBytes());
        try {
            Document response = loginHandler.getXmlResponse();
            String status = response.getRootElement().elementIterator("status").next().getText();
            if(status.equals("true")){
                return true;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Registro de un nuevo usuario en el servidor
     * @param fnameStr Nombre
     * @param lnameStr Apellido
     * @param userStr Nombre de usuario
     * @param passwordStr Contrasena
     * @param bdayDate Cumpleanos
     * @param genresStr Generos favoritos
     * @return boolean que indica si la creacion del usuario fue exitosa
     */
    public boolean register(String fnameStr, String lnameStr, String userStr, String passwordStr, LocalDate bdayDate, String[] genresStr){
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("request").addAttribute("opcode", "2");
        root.addElement("first_name").addText(fnameStr);
        root.addElement("last_name").addText(lnameStr);
        root.addElement("username").addText(userStr);
        root.addElement("birthday").addText(bdayDate.toString());
        root.addElement("password").addText(passwordStr);

        String request = document.asXML();

        NioClient client = NioClient.getInstance();
        ResponseHandler registerHandler = client.send(request.getBytes());

        try {
            Document response = registerHandler.getXmlResponse();
            String status = response.getRootElement().elementIterator("status").next().getText();
            if(status.equals("true")){
                return true;
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }
}

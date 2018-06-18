package clienteOdysseyV2;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
public class ResponseHandler {
	 /**
     * Respuesta
     */
    private byte[] rsp = null;

    /**
     * Manejo de la respuesta
     * @param rsp respuesta
     * @return
     */
    public synchronized boolean handleResponse(byte[] rsp) {
        this.rsp = rsp;
        this.notify();
        return true;
    }

    /**
     * Esperar respuesta
     */
    public synchronized void waitForResponse() {
        while(this.rsp == null) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Obtener la respuesta en formato XML
     * @return dom4j.Document respuesta
     * @throws Exception
     */
    public Document getXmlResponse() throws Exception{
        return DocumentHelper.parseText(new String(rsp));
    }

    /**
     * Obtener los bytes de la respuesta
     * @return byte[] respuesta
     */
    public byte[] getResponse(){
        return rsp;
    }

    /**
     * Obtener un string con la respuesta
     * @return String respuesta
     */
    public String getStrResponse(){
        return new String(rsp);
    }
}

package clienteOdysseyV2;

import org.dom4j.Document;
import org.dom4j.Element;

import java.io.OutputStream;
import java.util.Base64;

public class StreamThread extends Thread {
    OutputStream stream;
    Document request;
    Element chunkNumber;
    private int chunk;
    private int totalChunks;
    private boolean paused = false;

    public StreamThread(OutputStream stream, Document request, Element chunkNumber, int initialChunk, int totalChunks){
        this.stream = stream;
        this.request = request;
        this.chunkNumber = chunkNumber;
        this.chunk = initialChunk;
        this.totalChunks = totalChunks;
    }

    @Override
    public void run(){
        while(this.chunk < this.totalChunks && !paused) {
            this.chunkNumber.setText(String.valueOf(chunk));

            String request = this.request.asXML();

            NioClient client = NioClient.getInstance();
            ResponseHandler handler = client.send(request.getBytes());

            try {
                System.out.println(handler.getStrResponse());
                Document response = handler.getXmlResponse();


                String audio = response.getRootElement().elementIterator("content").next().getText();

                byte[] decodedAudio = Base64.getDecoder().decode(audio);
                if(!paused) {
                    stream.write(decodedAudio);
                }
            }catch (Exception ex){
            }

            chunk++;
        }
    }

    public int pause(){
        this.paused = true;
        return chunk;
    }

    public int getChunk(){
        return chunk;
    }
}
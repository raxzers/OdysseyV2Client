package clienteOdysseyV2;

import com.Ostermiller.util.CircularByteBuffer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.stream.Stream;

public class PlayerThread extends Thread {
    private Document request;
    private int initialChunk;
    private boolean paused = true;
    private int pausedChunk;
    private int bufferSize = 983040;
    private int totalChunks;
    private double amplitude = 0;


    public DoubleProperty currentPercent = new SimpleDoubleProperty(0);

    public PlayerThread(Document request, int initialChunk){
        this.request = request;
        this.initialChunk = initialChunk;
    }

    @Override
    public void run(){
        paused = false;

        Element root = request.getRootElement();

        Element chunkNumber = root.addElement("chunk").addText(String.valueOf(initialChunk));

        String request = this.request.asXML();

        NioClient client = NioClient.getInstance();
        ResponseHandler handler = client.send(request.getBytes());

        System.out.println(handler.getStrResponse());

        try {
            Document response = handler.getXmlResponse();

            if(response.getRootElement().elementIterator("error").next().getText().equals("false")) {
                String audio = response.getRootElement().elementIterator("content").next().getText();

                CircularByteBuffer buffer = new CircularByteBuffer(bufferSize);

                buffer.getOutputStream().write(Base64.getDecoder().decode(audio));

                InputStream stream = buffer.getInputStream();

                totalChunks = Integer.parseInt(response.getRootElement().elementIterator("chunks").next().getText());

                StreamThread streaming = new StreamThread(buffer.getOutputStream(), this.request, chunkNumber, initialChunk + 1, totalChunks);
                streaming.start();

                AudioInputStream in = AudioSystem.getAudioInputStream(stream);
                AudioInputStream din = null;
                AudioFormat baseFormat = in.getFormat();
                AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false);

                din = AudioSystem.getAudioInputStream(decodedFormat, in);
                // Play now.
                rawplay(decodedFormat, din, streaming);
                in.close();

                this.pausedChunk = streaming.pause();
            }else{
                System.out.println("Cancion no encontrada");
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void rawplay(AudioFormat targetFormat, AudioInputStream din, StreamThread stream) throws IOException, LineUnavailableException
    {
        byte[] data = new byte[4096];
        SourceDataLine line = getLine(targetFormat);
        if (line != null)
        {
            // Start
            line.start();
            int nBytesRead = 0, nBytesWritten = 0;
            while (nBytesRead != -1 && paused == false)
            {
                nBytesRead = din.read(data, 0, data.length);
                if (nBytesRead != -1) nBytesWritten = line.write(data, 0, nBytesRead);
                amplitude = 0;
                for (int j = 0; j < data.length; j = j +2 ){
                    if (data[j] > data[j+1])
                        amplitude = amplitude + data[j] - data[j+1];
                    else amplitude = amplitude + data[j + 1] - data[j];
                }
                amplitude = amplitude / data.length * 2;
                currentPercent.setValue(100 * (stream.getChunk() - 2) / (totalChunks - 2));
            }
            // Stop
            line.drain();
            line.stop();
            line.close();
            din.close();
        }
    }

    private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
    {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        res = (SourceDataLine) AudioSystem.getLine(info);
        res.open(audioFormat);
        return res;
    }

    public int pause() {
        this.paused = true;
        while (isAlive()){
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.pausedChunk - 2;
    }

    public int getTotalChunks(){
        return totalChunks;
    }

    public double getAmplitude(){
        return amplitude;
    }
}
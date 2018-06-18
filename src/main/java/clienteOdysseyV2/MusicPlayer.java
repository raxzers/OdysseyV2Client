package clienteOdysseyV2;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javazoom.jl.player.Player;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;


public class MusicPlayer {

	 private static MusicPlayer instance;

	    /**
	     * Hilo unico del reproductor
	     */
	    PlayerThread playerThread;

	    int currentChunk;

	    Metadata currentSong;

	    Slider slider;

	    /**
	     * Constructor privado del reproductor
	     */
	    private MusicPlayer(){}

	    /**
	     * Singleton para obtener la instancia del reproductor
	     * @return la instancia del reproductor
	     */
	    public static MusicPlayer getInstance(){
	        if(instance == null){
	            instance = new MusicPlayer();
	        }
	        return instance;
	    }

	    /**
	     * Reproduce una cancion
	     * @param song Metadata de la cancion a reproducir
	     * @param chunk Bloque desde el cual reproducir la cancion
	     */
	    public void play(Metadata song, int chunk){
	        currentSong = song;
	        if(playerThread != null && playerThread.isAlive()){
	            pause();
	        }
	        Document document = DocumentHelper.createDocument();
	        Element root = document.addElement("request").addAttribute("opcode", "5");

	        root.addElement("name").addText(song.name);
	        root.addElement("artist").addText(song.artist);
	        root.addElement("year").addText(song.year);
	        root.addElement("album").addText(song.album);
	        root.addElement("genre").addText(song.genre);

	        playerThread = new PlayerThread(document, chunk);
	        playerThread.currentPercent.addListener(new ChangeListener<Number>() {
	            @Override
	            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
	                slider.adjustValue(newValue.doubleValue());
	            }
	        });
	        playerThread.start();
	    }

	    public void pause(){
	        if(playerThread != null) {
	            currentChunk = playerThread.pause();
	        }
	    }

	    public void unpause(){
	        play(currentSong, currentChunk);
	    }



	    public boolean isPlaying() {
	        if(playerThread != null){
	            return playerThread.isAlive();
	        }else{
	            return false;
	        }
	    }

	    public boolean isPaused(){
	        return currentSong != null;
	    }

	    public void setSlider(Slider slider){
	        this.slider = slider;
	    }

	    public void forward(int slider){
	        int chunk = playerThread.getTotalChunks() * slider / 100;
	        play(currentSong, chunk);
	    }

	    public double getAmplitude(){
	        if(this.playerThread != null){
	            return playerThread.getAmplitude();
	        }else{
	            return -1;
	        }
	    }
	
	
	
}

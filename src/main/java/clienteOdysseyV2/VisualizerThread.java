package clienteOdysseyV2;
import com.jfoenix.controls.JFXProgressBar;

public class VisualizerThread extends Thread {
    private MusicPlayer player;
    private JFXProgressBar visualizer;
    private double oldMax = 1;

    public VisualizerThread(MusicPlayer player, JFXProgressBar bar){
        this.player = player;
        this.visualizer = bar;

    }

    @Override
    public void run(){
        while(isAlive()){
            double amplitude = player.getAmplitude();
            if (amplitude == -1) {
                try {
                    sleep(1000 / 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            this.visualizer.setProgress(amplitude / 100);//oldMax);

            try {
                sleep(1000 / 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
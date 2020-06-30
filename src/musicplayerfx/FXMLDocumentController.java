/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayerfx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import jaco.mp3.player.MP3Player;
import java.io.File;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.swing.JFileChooser;
import sun.audio.AudioPlayer;

/**
 *
 * @author xKral_Tr
 */
public class FXMLDocumentController implements Initializable {
    
    
    List<String> lstFile;

    String musicFile;
    
    DecimalFormat df = new DecimalFormat("#.##");
    
    public double zaman;
    
    MediaPlayer mediaPlayer;
    Media sound;

    int indexx;
    int queue;

    LinkedList<String> deneme = new LinkedList<String>();

    boolean first_song = false;

    int index;
    String[] dizi = new String[20];
    int temp = 0;

    SongStack stck = new SongStack();

    List<String> lsttFile;

    String _musicFile;
    String fileTemp;

    int sayac = 1;

    @FXML
    public Label nowPlayingSong, timeTotall,current;

    @FXML
    private AnchorPane pane_settings;

    @FXML
    private JFXListView<String> playList;

    @FXML
    private JFXButton btnn_settings, btnn_close, btnn_close_settings, btn_pause, btn_play,btn_volm_down,btn_volm_mute,btn_volm_up;

    @FXML
    private MaterialDesignIconView btn_close, btn_close_settings, btn_settings;

    private static FadeTransition fadeOut = new FadeTransition();
    private static FadeTransition fadeIn = new FadeTransition();

    @FXML
    private void handleButtonAction(MouseEvent event) {

        if (event.getSource() == btnn_close) {
            System.exit(0);
        } else {
            if (event.getSource() == btnn_settings) {

                showTransition(pane_settings);

            } else if (event.getSource() == btnn_close_settings) {

                hideTransition(pane_settings);

            }
        }
    }

    @FXML
    private void playBtn(MouseEvent event) throws InterruptedException {
        System.out.println("Now playing...");      
        System.out.println(stck.zaman);
        btn_play.setVisible(false);
        btn_pause.setVisible(true);
        Song();
    }

    @FXML
    private void nextBtn(MouseEvent event) {
        System.out.println("Next song...");
        nextSong();
    }

    @FXML
    private void prevBtn(MouseEvent event) {
        System.out.println("Prev song...");
        prevSong();
    }

    @FXML
    private void pauseBtn(MouseEvent event) {
        System.out.println("burdaPause");
        btn_pause.setVisible(false);
        btn_play.setVisible(true);

        puaseMusic();

    }

    @FXML
    private void repeatBtn(MouseEvent event) {
  
        goruntule();
    }

    @FXML
    private void queueBtn(MouseEvent event) {
        addToQueue(fileTemp);
    }

    @FXML
    private void deleteBtn(MouseEvent event) {
        playList.getItems().remove(fileTemp);
        deleteSong();
    }

    @FXML
    private void folderBtn(ActionEvent event) {

        /*FileChooser fc = new FileChooser();
         fc.getExtensionFilters().add(new ExtensionFilter("mp3",lstFile));
         File f = fc.showOpenDialog(null);*/
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new ExtensionFilter("mp3", lsttFile));
        List<File> f = fc.showOpenMultipleDialog(null);

        for (File file : f) {
            _musicFile = file.getAbsolutePath();
            fileTemp = file.getAbsolutePath();
            push(_musicFile);
            playList.getItems().add(_musicFile);

            if (f != null) {
                System.out.println("seçilen:" + file.getName());
            }
        }
    }

    @FXML
    private void displaySelected(MouseEvent event) {

        String Selectedsong = playList.getSelectionModel().getSelectedItem();
        fileTemp = Selectedsong;
        if (Selectedsong == null || Selectedsong.isEmpty()) {

        } else {
            
            //Selectedsong = ""+Selectedsong
            findIndex(Selectedsong);
            if (event.getClickCount() == 2) {
                findIndexx(fileTemp);
                btn_play.setVisible(false);
                btn_pause.setVisible(true);
            }
        }
    }

    @FXML
    private void keydeneme(KeyEvent event) {
        if (event.getCode().toString().equals("ESCAPE")) {
            System.exit(0);
        } else if (event.getCode().toString().equals("S")) {
            if (sayac == 1) {
                btn_play.setVisible(false);
                btn_pause.setVisible(true);
                Song();
                sayac = 2;
            } else if (sayac == 2) {
                btn_pause.setVisible(false);
                btn_play.setVisible(true);
                puaseMusic();
                sayac = 1;
            }
        }
    }

    @FXML
    private void volumeDownBtn(MouseEvent event) {
        volumeDownControl(0.1);
    }

    @FXML
    private void volumeUpBtn(MouseEvent event) {
        volumeUpControl(0.1);
    }

    @FXML
    private void volumeMuteBtn(MouseEvent event) {
        volumeControl(0.0);
    }

    public void push(String sa) {
        if (index == 20) {
            System.out.println("Doldu");
        } else {
            index++;
            dizi[index] = sa;
            deneme.addLast(sa);

        }
    }

    public void fileStack() {

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("word files", lstFile));
        File f = fc.showOpenDialog(null);
        musicFile = f.getAbsolutePath();
        push(musicFile);

    }
    
    public String song() {
        if (index < 0) {
            System.out.println("Boş");
            return null;
        } else {
            System.out.println("habu:" + temp);
            return deneme.get(temp++).toString();
            //return dizi[temp++];
        }
    }

    public void Song() {
        
        if( first_song == true){
            if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING) == false){
                mediaPlayer.play();
            }  
        }
        if (first_song == false) {
            playMusic(song());
            first_song = true;
        }
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING) == true) {
            mediaPlayer.stop();
            playMusic(song());
        }
    }
    
    public void playMusic(String muısicLocation) {
        
        sound = new Media(new File(muısicLocation).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);  
        mediaPlayer.setOnReady(new Runnable() {

        @Override
        public void run() {
            
            mediaPlayer.play();
            
            zaman = mediaPlayer.getTotalDuration().toMinutes();
            timeTotall.setText(df.format(zaman));
            System.out.println(muısicLocation);
            File Filetemp = new File(muısicLocation);
            nowPlayingSong.setText(Filetemp.getName());
            btn_volm_down.setVisible(true);
            btn_volm_up.setVisible(true);
            btn_volm_mute.setVisible(true);
            
            }
        }); 
       
        
        mediaPlayer.setOnEndOfMedia(()-> {mediaPlayer.stop(); Song();});      
    }
    
    public void puaseMusic(){
        
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING) == true){
            mediaPlayer.pause();
        }
        else if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING) == false){
            mediaPlayer.play();
        }
    }

    public void findIndex(String filePath) {

        indexx = deneme.indexOf(filePath);
        temp = indexx;

    }
    
    public void findIndexx(String filePath){
        
        indexx = deneme.indexOf(filePath);       
        temp = indexx;      
        Song();
        
    }
    
    public void addToQueue(String filePath){
        
        queue = temp +1;
        deneme.add(queue, filePath);
        
    }
    
    public void deleteSong(){
        
        System.out.println(deneme.get(temp)); 
        deneme.remove((temp));
        goruntule();
        
    }

    public void prevSong() {

        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING) == true) {
            mediaPlayer.stop();
            temp = temp - 2;
            System.out.println(temp);
            playMusic(song());
        } else {

            playMusic(song());
        }
    }

    public void nextSong() {
        if (mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING) == true) {
            mediaPlayer.stop();
            playMusic(song());
        } else {
            playMusic(song());
        } 
    }
    
    public String nowPlaying() {
        return deneme.get(temp-1);  
    }
    
    public void goruntule() {

        System.out.println("Sıradaki Şarkılar:" + deneme);
        /*for (int i = 0; i < index+1; i++) {
         System.out.println((i+1)+".:"+dizi[i]);
         }*/
    }

    public void volumeDownControl(Double valueToPlusMinus) {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixers) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lineInfos = mixer.getTargetLineInfo();

            for (Line.Info lineInfo : lineInfos) {
                Line line = null;
                boolean opened = true;
                try {
                    line = mixer.getLine(lineInfo);
                    opened = line.isOpen() || line instanceof Clip;
                    if (!opened) {
                        line.open();
                    }
                    FloatControl volControl = (FloatControl) line.getControl(FloatControl.Type.VOLUME);

                    float currentVolume = volControl.getValue();
                    Double volumeToCut = valueToPlusMinus;
                    float changedCalc = (float) ((float) currentVolume - (double) volumeToCut);
                    volControl.setValue(changedCalc);
                } catch (LineUnavailableException lineException) {

                } catch (IllegalArgumentException illException) {

                } finally {
                    if (line != null && !opened) {
                        line.close();
                    }
                }
            }
        }
    }

    public void volumeUpControl(Double valueToPlusMinus) {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixers) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lineInfos = mixer.getTargetLineInfo();

            for (Line.Info lineInfo : lineInfos) {
                Line line = null;
                boolean opened = true;
                try {
                    line = mixer.getLine(lineInfo);
                    opened = line.isOpen() || line instanceof Clip;
                    if (!opened) {
                        line.open();
                    }
                    FloatControl volControl = (FloatControl) line.getControl(FloatControl.Type.VOLUME);

                    float currentVolume = volControl.getValue();
                    Double volumeToCut = valueToPlusMinus;
                    float changedCalc = (float) ((float) currentVolume + (double) volumeToCut);
                    volControl.setValue(changedCalc);
                } catch (LineUnavailableException lineException) {

                } catch (IllegalArgumentException illException) {

                } finally {
                    if (line != null && !opened) {
                        line.close();
                    }
                }
            }
        }
    }

    public void volumeControl(Double valueToPlusMinus) {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixers) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lineInfos = mixer.getTargetLineInfo();

            for (Line.Info lineInfo : lineInfos) {
                Line line = null;
                boolean opened = true;
                try {
                    line = mixer.getLine(lineInfo);
                    opened = line.isOpen() || line instanceof Clip;
                    if (!opened) {
                        line.open();
                    }
                    FloatControl volControl = (FloatControl) line.getControl(FloatControl.Type.VOLUME);

                    float currentVolume = volControl.getValue();
                    Double volumeToCut = valueToPlusMinus;
                    float changedCalc = (float) ((double) volumeToCut);
                    volControl.setValue(changedCalc);
                } catch (LineUnavailableException lineException) {

                } catch (IllegalArgumentException illException) {

                } finally {
                    if (line != null && !opened) {
                        line.close();
                    }
                }
            }
        }
    }  

    private void showTransition(AnchorPane anchorPane) {
        fadeIn.setNode(anchorPane);
        fadeIn.setDuration(Duration.millis(1000));
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        anchorPane.setVisible(true);
        fadeIn.play();
    }

    private void hideTransition(AnchorPane anchorPane) {
        fadeOut.setNode(anchorPane);
        fadeOut.setDuration(Duration.millis(1000));
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        anchorPane.setVisible(false);
        fadeOut.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lsttFile = new ArrayList<>();
        lsttFile.add("*.mp3");
        lstFile = new ArrayList<>();
        lstFile.add("*.mp3");
        index = -1;    
      
    }
}

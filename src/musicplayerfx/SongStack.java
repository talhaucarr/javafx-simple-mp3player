/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package musicplayerfx;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

/**
 *
 * @author xKral_Tr
 */
public class SongStack {
    
    
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

    public SongStack() {

        lstFile = new ArrayList<>();
        lstFile.add("*.mp3");

        index = -1;
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
            if (mediaPlayer.getStatus().equals(Status.PLAYING) == false){
                mediaPlayer.play();
            }  
        }
        if (first_song == false) {
            playMusic(song());
            first_song = true;
        }
        if (mediaPlayer.getStatus().equals(Status.PLAYING) == true) {
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
            System.out.println("başlangıc");
            
            songTime();
            
            zaman = mediaPlayer.getTotalDuration().toMinutes();
            
            System.out.println(zaman);
            
            }
        });      
        mediaPlayer.setOnEndOfMedia(()-> {mediaPlayer.stop(); Song();});
        
    }
    
    public void puaseMusic(){
        
        if (mediaPlayer.getStatus().equals(Status.PLAYING) == true){
            mediaPlayer.pause();
        }
        else if (mediaPlayer.getStatus().equals(Status.PLAYING) == false){
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

        if (mediaPlayer.getStatus().equals(Status.PLAYING) == true) {
            mediaPlayer.stop();
            temp = temp - 2;
            System.out.println(temp);
            playMusic(song());
        } else {

            playMusic(song());
        }
    }

    public void nextSong() {

        if (mediaPlayer.getStatus().equals(Status.PLAYING) == true) {
            mediaPlayer.stop();
            playMusic(song());
        } else {
            playMusic(song());
        }
        
    }
    
    public String nowPlaying() {

        return deneme.get(temp-1);
        
    }
    
    public void songTime(){
        
        
        System.out.println(mediaPlayer.getTotalDuration().toMinutes());

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

}

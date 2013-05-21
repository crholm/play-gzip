package se.rzz.play.module.gz.gzip;

import play.Play;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: kungar
 * Date: 5/20/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class GzipFileThread implements Runnable {
    private static GzipFileThread ourInstance = new GzipFileThread();
    private HashSet<String> excludes = new HashSet<>();


    public static GzipFileThread getInstance() {
        return ourInstance;
    }

    private GzipFileThread() {

        try{
            List<String> excludesList = Play.application().configuration().getStringList("gzip.exclude");

            for(String s : excludesList){
                excludes.add(s);
            }


        }catch (NullPointerException e){}

        new Thread(this).start();



    }


    public boolean shouldBeExcluded(String relativePath){

        if(excludes.isEmpty()){
            return false;
        }


        String arrayPath[] = relativePath.substring(1).split("/");

        String tmp = "";
        for(String part : arrayPath){
            tmp += "/" + part ;
            if(excludes.contains(tmp)){
                return true;
            }
        }

        return false;
    }



    private boolean running = true;

    private boolean usingList1 = true;
    private LinkedList<File> list1 = new LinkedList<File>();
    private LinkedList<File> list2 = new LinkedList<File>();


    public void addFileForCompression(File file){
        synchronized (this){
            if(usingList1){
                list2.offer(file);
            }else{
                list1.offer(file);
            }
            this.notifyAll();
        }

    }




    @Override
    public void run() {
        while(running){
            while(!list1.isEmpty()){
                compressFile( list1.poll() );
            }

            synchronized (this){
                usingList1 = false;
            }

            while(!list2.isEmpty()){
                compressFile( list2.poll() );
            }

            synchronized (this){
                if(list1.isEmpty() && list2.isEmpty()){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {}
                }
                usingList1 = true;
            }

        }
    }


    public void kill(){
        running = false;
    }

    private void compressFile(File rawFile){
        File gzFile = new File(rawFile.getAbsolutePath()+".gz");

        if(( (rawFile.exists() && !gzFile.exists()) ||
                (rawFile.exists() && gzFile.exists() && rawFile.lastModified() > gzFile.lastModified()) )){


            try {


                InputStream in = new FileInputStream(rawFile);

                OutputStream out = new GZIPOutputStream(new FileOutputStream(gzFile, false));

                byte[] buf = new byte[10000];
                int len;

                while((len = in.read(buf)) != -1){
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }





}

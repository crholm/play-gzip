package se.rzz.play.module.gz.controllers;

import se.rzz.play.module.gz.gzip.GzipFileThread;
import play.Play;
import play.api.mvc.Action;
import play.api.mvc.AnyContent;
import play.mvc.Controller;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: kungar
 * Date: 5/20/13
 * Time: 5:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class GzipAssets extends Controller {

    private static long maxSize = Long.MAX_VALUE;
    private static long minSize = -1;

    public static Action<AnyContent> at(String path, String file){


        if(Play.isDev()){
            return controllers.Assets.at(path, file);
        }

        if(GzipFileThread.getInstance().shouldBeExcluded(path + "/" + file)){
            return controllers.Assets.at(path, file);
        }


        try {
            minSize = Play.application().configuration().getBytes("gzip.min.file.size");
        }catch (NullPointerException e){};

        try {
            maxSize = Play.application().configuration().getBytes("gzip.max.file.size");
        }catch (NullPointerException e){};


        File rawFile = Play.application().getFile(path + "/" + file);
        File gzFile = Play.application().getFile(path + "/" + file + ".gz");

        if(rawFile.exists() && !rawFile.isDirectory() && maxSize > rawFile.length() && minSize < rawFile.length()){

            if( !gzFile.exists() ){
                GzipFileThread.getInstance().addFileForCompression(rawFile);

            }else if( gzFile.exists() && rawFile.lastModified() > gzFile.lastModified() ){
                gzFile.delete();
                GzipFileThread.getInstance().addFileForCompression(rawFile);
            }

        }

        return controllers.Assets.at(path, file);
    }


}

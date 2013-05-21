#play-gzip
This is a simple plugin that helps with gzip-ing outgoing data from your play application.



##Importing plugin

    import sbt._
    import Keys._
    import PlayProject._
  	
  	object ApplicationBuild extends Build {
  	
  	    val appName         = "YourAppName"
  	    val appVersion      = "1.0-SNAPSHOT"
  	
  	    val appDependencies = Seq(
  	      // Add your project dependencies here,
  	      "se.rzz" %% "play-gzip" % "1.2"
  	    )
  	
  	    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
  	      // Add your own project settings here   
  	      resolvers += Resolver.url("play-gzip (release)", url("http://crholm.github.com/releases/"))(Resolver.ivyStylePatterns)
  	    )
  	
  	}
    
##Usage
As of now there are three ways of using play-gzip

###Action composition through Annotation
Add the annotation `@Gzip` before the controller which response you wish to gzip
####Examlpe
    
    import se.rzz.play.module.gz.gzip.Gzip;
    
    public class Application extends Controller {
        
        @Gzip
        public static Result index() {
    
            return ok(index.render("Your new application is ready."));
        }
    }


###Composition
For more fine grained controll one can simply call for gziping the result before returning it
####Example

    import se.rzz.play.module.gz.gzip.GzipEncoder;

    public class Application extends Controller {
        public static Result index() {
    
            return GzipEncoder.encode(
                      ok(index.render("Your new application is ready.")
            );
        }
    }


* Note: If `Content-Type` in response is not set play-gzip will assume that it is `text/html;charset=UTF-8`

* Note: play-gzip never returns gziped content unless the request `Accept-Encoding` contains `gzip`

* Note: play-gzip will set `Content-Encoding` to gzip for result


###GzipAsstes Controller
There is a included controller that in much acts like the regular Assets controller and depends on it. 
The core function of GzipAssets is that it will during runtime gzip your static assets and save them on disk along side 
the original asset.
####Example routes

    #GET    /assets/*file   controllers.Assets.at(path="/public", file)
    GET     /assets/*file   se.rzz.play.module.gz.controllers.GzipAssets.at(path="/public", file)
    
####Example controller    

    import se.rzz.play.module.gz.controllers.GzipAssets;
    
    public class Application extends Controller {
        public static Action<AnyContent> index() {
    
            return GzipAssets.at("/public", "index.html");
        }
    }
    
#### Example application.conf
    
    #Your conf here
    
    gzip.max.file.size=50kB             #Optional, default value Long.MAX_VALUE
    gzip.min.file.size=2kB              #Optional, default value 0
    gzip.exclude=[                      #Optional, default all files will be conpressed
                    "/public/img",
                    "/public/js/libs/jquery.min.js"
                 ]
    
* Note: GzipAssets only work in production since it relays on Asstes to send the compressed files and Assets only does so
in production mode

* Note: Config only relate to GzipAssets

* Note: Config is optional and without it GzipAssets will conpress all files regardless of size or location.

* Note: Only files with the file size within the intervall gzip.min.file.size to gzip.max.file.size will be compressed

* Note: gzip.exclude contain list with relativ path to assets not to be compressed, both specific files or dirs are valid

* Note: GzipAssets compresses assets in a seperate thread and are initiated by the request for a asset. 
Hence the first request for an asset might deliver a uncompressed version. 

* Note: GzipAssets also "detect" changes to the uncompressed asset and re-compress it. 
It might however deliver the old version on the first request for the asset



##Versions
`1.2`





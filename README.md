#play-gzip


This is a simple plugin that helps with gzip-ing outgoing data from your play application.
##Versions
`1.1.1` `1.1.0`

##Importing plugin

  import sbt._
    import Keys._
  	import PlayProject._
  	
  	object ApplicationBuild extends Build {
  	
  	    val appName         = "authtut"
  	    val appVersion      = "1.0-SNAPSHOT"
  	
  	    val appDependencies = Seq(
  	      // Add your project dependencies here,
  	      "se.rzz" %% "play-gzip" % "1.1.2"
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

    @Gzip
    public static Result index() {

        return ok(index.render("Your new application is ready."));
    }


###Composition
For more fine grained controll one can simply call for gziping the result before returning it
####Example

    import se.rzz.play.module.gz.gzip.GzipEncoder;

    public static Result index() {

        return GzipEncoder.encode(
                  ok(index.render("Your new application is ready.")
        );
    }


Note: If `Content-Type` in response is not set play-gzip will assume that it is `text/html;charset=UTF-8`

Note: play-gzip never returns gziped content unless the request `Accept-Encoding` contains `gzip`

Note: play-gzip will set `Content-Encoding` to gzip for result






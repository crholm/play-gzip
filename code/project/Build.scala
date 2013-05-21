import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "play-gzip"
  val appVersion      = "1.2"

  val appDependencies = Seq(
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
      organization := "se.rzz"
  )

}

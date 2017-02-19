package com.github.dolcalmi

import sbt._
import sbt.Keys._
import sbt.complete.DefaultParsers._

object ScalaMigrationsPlugin extends AutoPlugin {

  override def requires: Plugins = sbt.plugins.JvmPlugin
  override def trigger = noTrigger
  object autoImport {
    //  available tasks
    val createMigration = inputKey[Unit]("Create a new migration file")

    //settings
    lazy val  migrationsConfigFile = settingKey[File]("Path to the configuration file with the migrations settings")
  }

  import autoImport._

  lazy val defaultSettings: Seq[Setting[_]] = Seq(
    migrationsConfigFile := file("config/migrations.conf"),
    createMigration := {
      val logger: Logger = streams.value.log
      val args: Seq[String] = spaceDelimited("<arg>").parsed
      val migrationName: String = args.headOption.getOrElse("")
      val configFile = migrationsConfigFile.value
      if (migrationName.isEmpty) {
        logger.error("You should call this with an argument, e.g.: $ sbt 'create my_migration_name'")
        System.exit(1)
      } else {
        logger.info(s"Creating migration for '${migrationName}'....")
        new DbMigrator(configFile, logger).createMigration(migrationName)
        System.exit(0)
      }
    }
  )

  override def projectSettings: Seq[Setting[_]] = defaultSettings
}

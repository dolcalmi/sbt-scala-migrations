package com.github.dolcalmi

import sbt._
import sbt.Keys._
import sbt.complete.DefaultParsers._

object ScalaMigrationsPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins = plugins.JvmPlugin

  object autoImport {
    //  available tasks
    val create = inputKey[Unit]("Create a new migration file")

    val thisIsTrue: SettingKey[Boolean] = settingKey[Boolean]("Defines whether or not this is true")
    val helloWorldText: SettingKey[String] = settingKey[String]("The text to show for the task helloWorld")
    val new: TaskKey[String] = taskKey[String]("Prints the helloworld message to the console")
  }

  import autoImport._

  lazy val defaultSettings: Seq[Setting[_]] = Seq(
    configFile := file("config/migrations.conf"),
    thisIsTrue := false,
    helloWorldText := "Hi there!",
    helloWorld := HelloWorld.runHelloWorld.value,
    create := {
      val args: Seq[String] = spaceDelimited("<arg>").parsed
      val migrationName = args.headOption.getOrElse("")

      if (migrationName.isEmpty) {
        streams.value.log.error("You should call this with an argument, e.g.: $ sbt 'create my_migration_name'")
        // Workaround for SBT bug where this task is called multiple times from single invocation
        System.exit(1)
      }

      streams.value.log.info(s"Creating migration for '${migrationName}'....")
      new DbMigrator(configFile.value, streams.value.log).createMigration(migrationName)
      // Workaround for SBT bug where this task is called multiple times from single invocation
      System.exit(0)
    }
  )

  override def projectSettings: Seq[Setting[_]] =
    defaultSettings
  }

  object HelloWorld {
    import ScalaMigrationsPlugin.autoImport._
    def runHelloWorld:  Def.Initialize[Task[String]] = Def.task {
      val logger: Logger = streams.value.log
      val _thisIsTrue: Boolean = thisIsTrue.value
      val _helloWorldText: String = helloWorldText.value

      logger.info(
        s"""
        |Running task: helloWorld
        |========================
          |thisIsTrue: ${_thisIsTrue}
          |helloWorldText: ${_helloWorldText}
          """.stripMargin)

          _helloWorldText
        }
      }

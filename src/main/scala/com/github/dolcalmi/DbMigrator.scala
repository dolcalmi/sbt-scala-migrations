package com.github.dolcalmi

import com.imageworks.migration.{Migrator, DatabaseAdapter, Vendor, ConnectionBuilder}

import com.typesafe.config.ConfigFactory
import sbt.{Logger, _}

class DbMigrator(configFile: File, logger: Logger) {

  val env = sys.props.getOrElse("SCALA_ENV", sys.env.getOrElse("SCALA_ENV", "development"))

  logger.info(s"Loading config file: $configFile for environment: $env")

  val config = ConfigFactory.parseFile(configFile).resolve().getConfig(env)
  val migrationsConfig = config.getConfig("migrations")

  val migrationsPackage = migrationsConfig.getConfig("package")
  val migrationsDir = migrationsConfig.getConfig("directory")

  val driverClassName = migrationsConfig.getString("db.driver")
  val dbUrl = migrationsConfig.getString("db.url")
  val dbUser = migrationsConfig.getString("db.user")
  val dbPassword = migrationsConfig.getString("db.password")

  val vendor = Vendor.forDriver(driverClassName)
  val databaseAdapter = DatabaseAdapter.forVendor(vendor, None)
  val connectionBuilder = new ConnectionBuilder(dbUrl, dbUser, dbPassword)

  val migrator = new Migrator(connectionBuilder, databaseAdapter)

  def createMigration(name: String): Boolean = {
    val now = LocalDateTime.now
    val creationDate: String = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    val migrationFileName: String = s"$migrationsDir/Migrate_${creationDate}_$name.cql"
    logger.info(s"Creating migration file: $migrationFileName")
    val migrationTemplate: String = s"""
package com.imageworks.vnp.dao.migrations

import com.imageworks.migration.{Limit, Migration, Name, NotNull, OnDelete, Restrict, Unique}

/**
 * authoredAt: ${System.currentTimeMillis}
 */

class ${migrationFileName} extends Migration
{
  def up() {

  }

  def down() {

  }
}
    """
    new PrintWriter(migrationFileName) {
      write(migrationTemplate)
      close()
    }
    logger.success(s"Created migration '$migrationFileName'")
    true
  }
}


// database {
//   driver = "slick.driver.MySQLDriver$"
//   db {
//     driver = "com.mysql.jdbc.Driver"
//     url = "jdbc:mysql://localhost/coquito_dev?characterEncoding=utf8"
//     user = "coquito_dev_user"
//     password = "coquito_dev_p4ssw0rd"
//   }
// }

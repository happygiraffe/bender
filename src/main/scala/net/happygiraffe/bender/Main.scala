package net.happygiraffe.bender

import org.apache.commons.configuration.CompositeConfiguration
import org.apache.commons.configuration.Configuration
import org.apache.commons.configuration.PropertiesConfiguration

object Main {

  def main(args: Array[String]) = {
    val cf = new CompositeConfiguration()
    if (args.length > 0)
      cf.append(new PropertiesConfiguration(args(0)))
    cf.append(defaultConfiguration())

    val bot = new Bender()
    bot.setVerbose(cf.getBoolean("verbose", true))
    bot.connect(cf.getString("host"), cf.getInt("port"))
    for (channel <- cf.getStringArray("channel"))
      bot.joinChannel(channel)
  }

  private def defaultConfiguration() : Configuration =
    new PropertiesConfiguration(Main.getClass.getResource("default.properties"))
}

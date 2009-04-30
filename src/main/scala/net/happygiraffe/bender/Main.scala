package net.happygiraffe.bender

import org.apache.commons.configuration.PropertiesConfiguration

object Main {

  def main(args: Array[String]) = {
    if (args.length != 1)
      usage()
    val cf = new PropertiesConfiguration(args(0))
    val bot = new Bender()
    bot.setVerbose(cf.getBoolean("verbose", true))
    bot.connect(cf.getString("host"), cf.getInt("port"))
    for (channel <- cf.getStringArray("channel"))
      bot.joinChannel(channel)
  }

  def usage() {
    Console.err.println("usage: Main bender.properties")
    exit(2)
  }
}

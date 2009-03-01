package net.happygiraffe.bender

object Main {

  def main(args: Array[String]) = {
    val bot = new Bender()
    bot.setVerbose(true)
    bot.connect("localhost", 6667)
    bot.joinChannel("#chat")
  }

}

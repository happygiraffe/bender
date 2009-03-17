package net.happygiraffe.bender

import java.net.URI
import java.net.URISyntaxException

object WatchCommand extends Command {
  def getDescription() : String =
    "start monitoring a feed"
    
  def respond(bot: Bender, args: String) : Iterable[String] = {
    if (args == null || args == "")
      return List("watch what exactly?")
    try {
      val uri = new URI(args.trim().split("\\s+")(0))
      bot.feeds += uri
      return List("okey-dokey")
    } catch {
      case ex: URISyntaxException =>
        return List("that doesn't look like a URI")
    }
  }
}
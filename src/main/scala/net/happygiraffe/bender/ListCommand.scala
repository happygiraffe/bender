package net.happygiraffe.bender

import java.net.URI
import java.net.URISyntaxException

object ListCommand extends Command {
  def getDescription() : String =
    "list all monitored feeds"
    
  def respond(bot: Bender, args: String) : Iterable[String] = {
    if (bot.feeds.isEmpty)
      return List("I'm not listening to any feeds.")
    else
      for (feed <- bot.feeds)
        yield feed.toString()
  }
}
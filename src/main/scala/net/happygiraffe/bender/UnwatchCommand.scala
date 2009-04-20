package net.happygiraffe.bender

import java.net.URI
import java.net.URISyntaxException

object UnwatchCommand extends Command {
  def getDescription() : String =
    "stop monitoring a feed"

  def respond(bot: Bender, channel: String, args: String) : Iterable[String] = {
    if (args == null || args == "")
      return List("sure...")
    try {
      val uri = new URI(args.trim().split("\\s+")(0))
      bot.feeder ! ("unwatch", WatchedFeed(channel, uri))
      return List("I never saw a thing!")
    } catch {
      case ex: URISyntaxException =>
        return List("that doesn't look like a URI")
    }
  }
}
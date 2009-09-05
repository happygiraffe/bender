package net.happygiraffe.bender

import java.net.URI
import java.net.URISyntaxException

object ListCommand extends Command {
  def getDescription() : String =
    "list all monitored feeds"

  def respond(bot: Bender, channel: String, args: String) : Iterable[String] = {
    // This is interesting…  "!?" returns Any, so we have to match to find out
    // If we're the right type.  Except that we can't say Set[URI] thanks to
    // marvellous erasure.
    bot.feeder !? ListFeeds match {
      case feeds: Set[_] =>
        if (feeds.isEmpty)
          return List("I'm not listening to any feeds.")
        else
          for (feed <- feeds.asInstanceOf[Set[WatchedFeed]])
            yield feed.uri.toString()
      case x => return List("I sure didn't expect to see a " + x)
    }
  }
}
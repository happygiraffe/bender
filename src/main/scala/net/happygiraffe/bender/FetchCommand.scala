package net.happygiraffe.bender

import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher
import java.net.URI
import scala.collection.jcl.Conversions._

object FetchCommand extends Command {
  val feedInfoCache = HashMapFeedInfoCache.getInstance()

  def getDescription() : String =
    "go and fetch all the feeds being monitored"

  def respond(bot: Bender, args: String) : Iterable[String] = {
    val fetcher = new HttpURLFeedFetcher(feedInfoCache)
    val feeds = (bot.feeder !? "list").asInstanceOf[Set[URI]];
    // Oh, this is fun.
    for {
      // Rome only supports URLs, not URIs.  *sigh #1*
      feedUrl <- feeds.map(_.toURL())
      // Go fetch!  Good boy!
      feed = fetcher.retrieveFeed(feedUrl)
      // Rome isn't generified. *sigh #2*
      entries = feed.getEntries.asInstanceOf[java.util.List[SyndEntry]]
      // The implicit defs in jcl.Conversions apply here
      entry <- entries
    } yield messageFor(entry)
  }

  private def messageFor(entry: SyndEntry) : String =
    entry.getTitle() + " :: " + entry.getLink()
}

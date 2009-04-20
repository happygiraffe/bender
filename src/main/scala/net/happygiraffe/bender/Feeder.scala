package net.happygiraffe.bender

import com.sun.syndication.feed.synd.SyndEntry
import com.sun.syndication.feed.synd.SyndFeed
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher
import java.net.URI
import java.net.URL
import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.jcl.Conversions._
import scala.collection.mutable

/**
 * Background class for managing the bot's feeds
 */
class Feeder(bot: Bender) extends Actor {
  private final val feedInfoCache = HashMapFeedInfoCache.getInstance()
  private final val fetcher = new HttpURLFeedFetcher(feedInfoCache)

  private final val feeds = mutable.Set[URI]()

  def watch(feed: URI) : Unit = feeds += feed

  def unwatch(feed: URI) : Unit = feeds -= feed

  /**
   * Return an immutable set of URIs being monitored.
   */
  def list(): Set[URI] = Set.empty ++ feeds

  /**
   * Retrieve the contents of a feed.  Sadly, Rome only supports URLs, not URIs.
   */
  private def fetchFeed(feed: URI) : SyndFeed =
    fetcher.retrieveFeed(feed.toURL())

  /**
   * Return the entries of a feed.  Sadly, Rome isn't generified.
   */
  private def feedEntries(feed: SyndFeed) : Iterable[SyndEntry] =
    feed.getEntries.asInstanceOf[java.util.List[SyndEntry]]

  /**
   * Fetch all feeds
   */
  private def fetch(): Unit = {
    for (feedUri <- feeds; entry <- feedEntries(fetchFeed(feedUri)))
      bot.messenger ! ("message", messageFor(entry))
  }

  private def messageFor(entry: SyndEntry) : String =
    entry.getTitle() + " :: " + entry.getLink()


  def act() {
    loop {
      react {
        case ("watch", feed: URI)   => watch(feed)
        case ("unwatch", feed: URI) => unwatch(feed)
        case "list"                 => reply(list())
        case "fetch"                => fetch()
      }
    }
  }
}
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
class Feeder(messenger: Actor) extends Actor {
  val periodSeconds = 60 * 60;
  private def periodicFetch() {
    val feeder = self
    actor {
      loop {
        Thread.sleep(periodSeconds * 1000)
        feeder ! "fetch"
      }
    }
  }

  private final val feedInfoCache = HashMapFeedInfoCache.getInstance()
  private final val fetcher = new HttpURLFeedFetcher(feedInfoCache)

  private final val feeds = mutable.Set[WatchedFeed]()

  private def watch(feed: WatchedFeed) : Unit = feeds += feed

  private def unwatch(feed: WatchedFeed) : Unit = feeds -= feed

  /**
   * Return an immutable set of URIs being monitored.
   */
  private def list(): Set[WatchedFeed] = Set.empty ++ feeds

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
    for (watchedFeed <- feeds; entry <- feedEntries(fetchFeed(watchedFeed.uri)))
      messenger ! ("message", watchedFeed, messageFor(entry))
  }

  private def messageFor(entry: SyndEntry) : String =
    entry.getTitle() + " :: " + entry.getLink()


  def act() {
    loop {
      react {
        case ("watch", feed: WatchedFeed)   => watch(feed)
        case ("unwatch", feed: WatchedFeed) => unwatch(feed)
        case "list"                         => reply(list())
        case "fetch"                        => fetch()
        case "init"                         => periodicFetch()
      }
    }
  }
}

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

  private final val fetcher =
    new HttpURLFeedFetcher(HashMapFeedInfoCache.getInstance())

  private final val feeds = mutable.Set[WatchedFeed]()

  private final val seenEntries = mutable.Set[String]()

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
   * Have we seen this entry before?  If you ask about an entry, we record it
   * as seen.
   */
  private def seen(entry: SyndEntry) : Boolean = {
    val rv = seenEntries.contains(entry.getUri())
    seenEntries += entry.getUri
    rv
  }

  /**
   * Fetch all feeds
   */
  private def fetch(): Unit = {
    for {
      watchedFeed <- feeds
      entry <- feedEntries(fetchFeed(watchedFeed.uri))
      if !seen(entry)
    } messenger ! ("message", watchedFeed, messageFor(entry))
  }

  private def messageFor(entry: SyndEntry) : String =
    entry.getTitle() + " :: " + entry.getLink()

  /**
   * Override for initialisation.
   */
  override def start(): Actor = {
      val rv = super.start()
      // NB: Have to use "this" rather than "self" as that returns an
      // ActorProxy at this point in time.
      this ! "init"
      rv
  }

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

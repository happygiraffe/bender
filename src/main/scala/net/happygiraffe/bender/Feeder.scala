package net.happygiraffe.bender

import java.net.URI
import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable

/**
 * Background class for managing the bot's feeds
 */
class Feeder(bot: Bender) extends Actor {

  val feeds = mutable.Set[URI]()

  def watch(feed: URI) : Unit = feeds += feed

  def unwatch(feed: URI) : Unit = feeds -= feed

  /**
   * Return an immutable set of URIs being monitored.
   */
  def list(): Set[URI] = Set.empty ++ feeds

  def act() {
    loop {
      react {
        case ("watch", feed: URI)   => watch(feed)
        case ("unwatch", feed: URI) => unwatch(feed)
        case "list"                 => reply(list())
      }
    }
  }
}

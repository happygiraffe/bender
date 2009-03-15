package net.happygiraffe.bender

import scala.collection.mutable.ListBuffer
import scala.collection.immutable.TreeSet

object HelpCommand extends Command {
  def getDescription() : String =
    "all the commands I understand"
    
  def respond(bot: Bender, args: String) : List[String] = {
    // s1 less than s2.  Dunno why it's not builtin.
    def ltStr(s1: String, s2: String) : Boolean = (s1 compareTo s2) < 0
    for (command <- bot.commands.keys.toList.sort(ltStr))
      yield (command + ": " + bot.commands(command).getDescription())
  }
}
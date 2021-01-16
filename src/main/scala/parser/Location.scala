package dev.alexnader.genome_map
package parser

object Location {
    case class Regular(start: Int, stop: Int) extends Location {
        override def toString: String = s"$start..$stop"
    }
    case class Complement(start: Int, stop: Int) extends Location {
        override def toString: String = s"complement($start..$stop)"
    }
}

sealed trait Location


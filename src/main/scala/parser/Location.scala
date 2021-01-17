package dev.alexnader.genome_map
package parser

import ca.ualberta.stothard.cgview.CgviewConstants

object Location {
    case class Regular(start: Int, stop: Int) extends Location {
        override def strand: Int = CgviewConstants.DIRECT_STRAND
        override def decoration: Int = CgviewConstants.DECORATION_CLOCKWISE_ARROW

        override def toString: String = s"$start..$stop"
    }
    case class Complement(start: Int, stop: Int) extends Location {
        override def strand: Int = CgviewConstants.REVERSE_STRAND
        override def decoration: Int = CgviewConstants.DECORATION_COUNTERCLOCKWISE_ARROW

        override def toString: String = s"complement($start..$stop)"
    }
}

sealed trait Location {
    val start: Int
    val stop: Int

    def strand: Int
    def decoration: Int
}


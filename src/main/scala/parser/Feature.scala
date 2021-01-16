package dev.alexnader.genome_map
package parser

case class Feature(location: Location, kind: String, entries: Map[String, String])

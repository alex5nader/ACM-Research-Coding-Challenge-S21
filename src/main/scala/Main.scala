package dev.alexnader.genome_map

import parser.GenBank

import ca.ualberta.stothard.cgview.CgviewIO

import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters.ListHasAsScala

object Main extends App with GenBank.Parser {
    val input = Files.readAllLines(Paths.get("Genome.gb")).asScala.mkString("\n")

    parse(genbank, input) match {
        case Success(data, _) =>
            val cgview = CgviewTest.visualize(data, { cgview =>
                cgview.setTitle(data.fields("SOURCE").description)

                cgview.setBackboneRadius(160)

                cgview.setLabelPlacementQuality(0)
                cgview.setLabelLineLength(30)
                cgview.setLabelLineThickness(0.5f)

                cgview.setDrawLegends(true)
            })

            CgviewIO.writeToSVGFile(cgview, "circular_genome_map.svg", false)
        case Failure(msg, _) => println(s"failure: $msg")
        case Error(msg, _) => println(s"error: $msg")
    }
}

package dev.alexnader.genome_map

import parser.GenBankParser

import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters.ListHasAsScala

object Main extends App with GenBankParser {
//    CgviewTest.test()

    val input = Files.readAllLines(Paths.get("Genome.gb")).asScala.mkString("\n")

    parse(phrase(genbank), input) match {
        case Success(data, _) =>
            println("success")
            for (field <- data.fields) {
                println(field.name)

                field.description.split("\n").foreach(x => println("\t" + x))

                for (entry <- field.entries) {
                    println("\t" + entry._1)
                    entry._2.split("\n").foreach(x => println("\t\t" + x))
                }
                println()
            }

            for (feature <- data.features) {
                println(s"${feature.kind} @ ${feature.location}")

                feature.entries.foreach { case (k, v) => println(s"$k: $v") }

                println()
            }

            println(data.genome)
        case Failure(msg, _) => println(s"failure: $msg")
        case Error(msg, _) => println(s"error: $msg")
    }
//
//    parse(field("[^\n]*".r), input) match {
//        case Success(matched, test) =>
//            println("success")
//            println(test)
//            println(matched)
//        case Failure(msg, _) => println(s"failure: $msg")
//        case Error(msg, _) => println(s"error: $msg")
//    }
}

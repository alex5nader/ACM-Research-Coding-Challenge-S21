package dev.alexnader.genome_map
package parser

import scala.collection.mutable
import scala.util.parsing.combinator._

case class GenBank(fields: List[Field], features: List[Feature], genome: String)

trait GenBankParser extends RegexParsers with PackratParsers {
    override def skipWhitespace: Boolean = false

    def label: Parser[String] = """[A-Z]+""".r

    def fieldHeader: Parser[String] =
        label <~ " +".r flatMap { label => if (label == "FEATURES") { failure("End of fields") } else { success(label) } }

    def fieldLine: Parser[String] =
        ".*".r <~ "\n"

    def fieldDescription: Parser[String] =
        repsep(fieldLine, """ {3,}""".r) ^^ { _.mkString("\n") }

    def fieldEntry: Parser[(String, String)] =
        "  " ~ """[A-Z]*""".r ~ " +".r ~ ".*".r ~ "\n" ^^ { case _ ~ label ~ _ ~ value ~ _ => (label, value) }

    def fieldEntries: Parser[Map[String, String]] =
        fieldEntry.* ^^ (entries => {
            val map = new mutable.ArrayBuffer[(String, mutable.ArrayBuffer[String])]

            entries foreach { case (label, value) =>
                if (label.isEmpty) {
                    map.last._2 += value
                } else {
                    map.addOne((label, mutable.ArrayBuffer.newBuilder.addOne(value).result()))
                }
            }

            map.map { case (label, lines) => (label, lines.mkString("\n")) }.toMap
        })

    def field: Parser[Field] =
        fieldHeader ~ fieldDescription ~ fieldEntries ^^ { case header ~ description ~ entries => Field(header, description, entries)}

    def regularLocation: Parser[Location.Regular] =
        """\d+""".r ~ ".." ~ """\d+""".r ^^ { case start ~ _ ~ stop => Location.Regular(start.toInt, stop.toInt) }

    def complementLocation: Parser[Location.Complement] =
        "complement(" ~ regularLocation ~ ")" ^^ { case _ ~ regular ~ _ => Location.Complement(regular.start, regular.stop) }

    def location: Parser[Location] =
        regularLocation | complementLocation

    def stringLiteral: Parser[String] =
        "\"" ~> """[^"]*""".r <~ "\""

    def intLiteral: Parser[Int] =
        """\d+""".r ^^ { _.toInt }

    def featureEntryValue: Parser[String] =
        stringLiteral | intLiteral ^^ { _.toString }

    def featureEntry: Parser[(String, String)] =
        " +".r ~ "/" ~ "[^=]*".r ~ "=" ~ featureEntryValue ~ "\n" ^^ { case _ ~ _ ~ name ~ _ ~ value ~ _ => (name, value) }

    def featureEntries: Parser[Map[String, String]] =
        featureEntry.* ^^ (_.toMap)

    def feature: Parser[Feature] =
        " +".r ~ """\w+""".r ~ " +".r ~ location ~ "\n" ~ featureEntries ^^ { case _ ~ kind ~ _ ~ location ~ _ ~ entries => Feature(location, kind, entries) }

    def featuresHeader: Parser[Unit] =
        ("FEATURES" ~ ".*".r ~ "\n").map(_ => ())

    def features: Parser[List[Feature]] =
        featuresHeader ~> feature.*

    def originHeader: Parser[Unit] =
        ("ORIGIN" ~ ".*".r ~ "\n").map(_ => ())

    def originLine: Parser[String] =
        " +".r ~> """\d+""".r ~> (" " ~> "[atcg]+".r).* <~ "\n" ^^ { _.mkString }

    def origin: Parser[String] =
        originHeader ~> originLine.* ^^ { _.mkString }

    def genbank: Parser[GenBank] =
        field.* ~ features ~ origin ~ "//" ^^ { case fields ~ features ~ origin ~ _ => GenBank(fields, features, origin) }
}

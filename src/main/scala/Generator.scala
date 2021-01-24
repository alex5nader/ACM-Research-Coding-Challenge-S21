package dev.alexnader.genome_map

import parser.GenBank

import ca.ualberta.stothard.cgview._

import java.awt.Color
import scala.collection.mutable
import scala.util.Random

object Generator {
    def visualize(data: GenBank, applySettings: Cgview => Unit): Cgview = {
        val cgview = new Cgview(data.genome.length)
        applySettings(cgview)

        val legend = new Legend(cgview)
        legend.setPosition(CgviewConstants.LEGEND_UPPER_RIGHT)

        val legendItems = mutable.HashMap[String, LegendItem]()

        val slots = mutable.HashMap[Int, mutable.Map[String, FeatureSlot]]()

        data.features
            .filter { _.kind != "source" }
            .foreach { feature =>
                val db_xref = feature.entries("db_xref")
                val slot = slots.getOrElseUpdate(feature.location.strand, mutable.HashMap()).getOrElseUpdate(db_xref, new FeatureSlot(cgview, feature.location.strand))

                val r = new Random(db_xref.hashCode)
                val s = r.nextInt(50) / 100f + 0.5f
                val b = r.nextInt(50) / 100f + 0.5f
                // calculate H last for maximum entropy
                val color = Color.getHSBColor(r.nextInt(360) / 360f, s, b)

                val id = feature.entries.get("protein_id")

                id match {
                    case Some(id) =>
                        val display = new Feature(slot, id)
                        display.setDecoration(feature.location.decoration)
                        display.setColor(color)


                        // the constructor has side effects
                        new FeatureRange(display, feature.location.start, feature.location.stop)
                    case None =>
                }

                val legendItem = legendItems.getOrElseUpdate(db_xref, new LegendItem(legend))
                feature.entries.get("product").tapEach { product =>
                    legendItem.setLabel(id match {
                        case Some(id) => s"$product ($id)"
                        case None => product
                    })
                }

                legendItem.setSwatchColor(color)
                legendItem.setDrawSwatch(CgviewConstants.SWATCH_SHOW)
            }

        cgview
    }
}

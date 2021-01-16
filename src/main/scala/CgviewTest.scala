package dev.alexnader.genome_map

import ca.ualberta.stothard.cgview._

object CgviewTest {
    def test(): Unit = {
        val length = 9000
        val cgview = new Cgview(length)

        cgview.setWidth(600)
        cgview.setHeight(600)
        cgview.setBackboneRadius(160)
        cgview.setTitle("Example")
        cgview.setLabelPlacementQuality(10)
        cgview.setShowWarning(true)
        cgview.setLabelLineLength(8)
        cgview.setLabelLineThickness(0.5f)

        val slot = new FeatureSlot(cgview, CgviewConstants.DIRECT_STRAND)

        for (_ <- 1 to 100) {
            val j = ((length - 2) * Math.random()).round.toInt + 1

            val feature = new Feature(slot, "label")

            val range = new FeatureRange(feature, j, j+1)
            range.setDecoration(CgviewConstants.DECORATION_CLOCKWISE_ARROW)
        }

        CgviewIO.writeToPNGFile(cgview, "test_maps/CgviewTest0.png")
    }
}

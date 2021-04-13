package xmlParser.implementations.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class DrawingUtil {
    public static BufferedImage flipYCoordinate(BufferedImage bufferedImage) {
        AffineTransform tx = AffineTransform.getScaleInstance(1,-1);
        tx.translate(0, -bufferedImage.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(bufferedImage, null);
    }
}

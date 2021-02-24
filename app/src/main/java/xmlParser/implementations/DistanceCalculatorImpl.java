package xmlParser.implementations;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public class DistanceCalculatorImpl implements xmlParser.framework.DistanceCalculator{

    private String targetCoordCode;
    private String sourceCoordCode;
    private MathTransform mathTransform;

    public DistanceCalculatorImpl(String sourceCoordCode, String targetCoordCode) throws FactoryException {
        this.sourceCoordCode = sourceCoordCode;
        this.targetCoordCode = targetCoordCode;
        CoordinateReferenceSystem crsSource = CRS.decode(this.sourceCoordCode);
        CoordinateReferenceSystem crsTarget = CRS.decode(this.targetCoordCode);
        this.mathTransform = CRS.findMathTransform(crsSource, crsTarget);
    }

    public double calculateDistance(Coordinate coord1, Coordinate coord2) throws TransformException {
        JTS.transform(coord1, coord1, this.mathTransform);
        JTS.transform(coord2, coord2, this.mathTransform);
        return Math.sqrt(Math.pow(coord1.x - coord2.x, 2) + Math.pow(coord1.y - coord2.y, 2));
    }

    public String getSourceCoordCode() {
        return sourceCoordCode;
    }

    public void setSourceCoordCode(String sourceCoordCode) {
        this.sourceCoordCode = sourceCoordCode;
    }

    public String getTargetCoordCode() {
        return targetCoordCode;
    }

    public void setTargetCoordCode(String targetCoordCode) {
        this.targetCoordCode = targetCoordCode;
    }


    public MathTransform getMathTransform() {
        return mathTransform;
    }

    public void setMathTransform(MathTransform mathTransform) {
        this.mathTransform = mathTransform;
    }


}

package XMLParser.Implementations;

import XMLParser.XMLParser;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public class DistanceCalculatorImpl implements XMLParser.Framework.DistanceCalculator {

    private String trgtCoordCode;
    private String srcCoordCode;
    private MathTransform mathTransform;

    public DistanceCalculatorImpl(String srcCoordCode, String trgtCoordCode) throws FactoryException {
        this.srcCoordCode = srcCoordCode;
        this.trgtCoordCode = trgtCoordCode;
        CoordinateReferenceSystem crsSource = CRS.decode(this.srcCoordCode);
        CoordinateReferenceSystem crsTarget = CRS.decode(this.trgtCoordCode);
        this.mathTransform = CRS.findMathTransform(crsSource, crsTarget);
    }

    public double calculateDistance(Coordinate coord1, Coordinate coord2) throws TransformException {
        JTS.transform(coord1, coord1, this.mathTransform);
        JTS.transform(coord2, coord2, this.mathTransform);
        return Math.sqrt(Math.pow(coord1.x - coord2.x, 2) + Math.pow(coord1.y - coord2.y, 2));
    }

    public String getSrcCoordCode() {
        return srcCoordCode;
    }

    public void setSrcCoordCode(String srcCoordCode) {
        this.srcCoordCode = srcCoordCode;
    }

    public String getTrgtCoordCode() {
        return trgtCoordCode;
    }

    public void setTrgtCoordCode(String trgtCoordCode) {
        this.trgtCoordCode = trgtCoordCode;
    }


    public MathTransform getMathTransform() {
        return mathTransform;
    }

    public void setMathTransform(MathTransform mathTransform) {
        this.mathTransform = mathTransform;
    }


}

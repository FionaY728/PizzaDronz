package uk.ac.ed.inf;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.ed.inf.Domain.LngLatImplemenation;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

public class LnglatTest {
    //unit test for LngLatImplemenation
    @Test
    public void testIsInRegion1() {
        LngLatImplemenation lngLatImpl1 = new LngLatImplemenation();
        LngLat position = new LngLat(2, 2);
        NamedRegion region = new NamedRegion("Test Region", new LngLat[]{new LngLat(0, 0), new LngLat(0, 5), new LngLat(5, 5), new LngLat(5, 0)});
        boolean expectedIsInRegion = true;
        boolean actualIsInRegion = lngLatImpl1.isInRegion(position, region);
        Assert.assertEquals(expectedIsInRegion, actualIsInRegion);
    }
    @Test public void testRegion2() {
        LngLatImplemenation testInterface = new LngLatImplemenation();
        LngLat A1 = new LngLat(-200, -200);
        LngLat A2 = new LngLat(200, -200);
        LngLat A3 = new LngLat(200, 200);
        LngLat A4 = new LngLat(-200, 200);
        LngLat[] polyA = {A1, A2, A3, A4};
        NamedRegion region1 = new NamedRegion("reg1", polyA);
        LngLat B1 = new LngLat(31, -44);
        LngLat B2 = new LngLat(31, -65);
        LngLat B3 = new LngLat(55, -62);
        LngLat B4 = new LngLat(60, -42);
        LngLat[] polyB = {B1, B2, B3, B4};
        NamedRegion region2 = new NamedRegion("reg2", polyB);
        LngLat C1 = new LngLat(34, -47);
        LngLat C2 = new LngLat(34, -61);
        LngLat C3 = new LngLat(41, -52);
        LngLat[] polyC = {C1, C2, C3};
        NamedRegion region3 = new NamedRegion("reg3", polyC);
        LngLat D1 = new LngLat(-104, 35);
        LngLat D2 = new LngLat(-72, 18);
        LngLat D3 = new LngLat(-54, -48);
        LngLat D4 = new LngLat(65, -40);
        LngLat D5 = new LngLat(53, -80);
        LngLat D6 = new LngLat(-135, -58);
        LngLat[] polyD = {D1, D2, D3, D4, D5, D6};
        NamedRegion region4 = new NamedRegion("reg4", polyD);
        LngLat E1 = new LngLat(-106, 11);
        LngLat E2 = new LngLat(-129, -56);
        LngLat E3 = new LngLat(-56, -65);
        LngLat[] polyE = {E1, E2, E3};
        NamedRegion region5 = new NamedRegion("reg5", polyE);

        LngLat x = new LngLat(-100, 20);
        LngLat y = new LngLat(50, -50);

        NamedRegion[] allRegion = {region1, region2, region3};

        NamedRegion regionX = null;
        NamedRegion regionY = null;

        for (NamedRegion region : allRegion) {
            if (testInterface.isInRegion(x, region)) {
                regionX = region;
            }
            if (testInterface.isInRegion(y, region)) {
                regionY = region;
            }
        }

        System.out.println(regionX.name() + "  " + regionY.name());
    }
    @Test
    public void testNextPosition() {
        LngLatImplemenation lngLatImplemenation = new LngLatImplemenation();
        LngLat startPosition = new LngLat(0, 0);
        double angle = 45;
        LngLat expectedNextPosition = new LngLat(SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(Math.toRadians(angle)), SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(Math.toRadians(angle)));
        LngLat actualNextPosition = lngLatImplemenation.nextPosition(startPosition, angle);
        Assert.assertEquals(expectedNextPosition.lng(), actualNextPosition.lng(), 0.0001);
        Assert.assertEquals(expectedNextPosition.lat(), actualNextPosition.lat(), 0.0001);
    }
    @Test
    public void testRegion(){
        LngLatImplemenation lngLatImplemenation = new LngLatImplemenation();


        LngLat testPoint = new LngLat(-5.5,4.5);
        LngLat p1 = new LngLat(-3,5);
        LngLat p2 = new LngLat(-4,4);
        LngLat p3 = new LngLat(-5,4);
        LngLat p4 = new LngLat(-6,5);
        LngLat p5 = new LngLat(-4,6);
        LngLat[] vertices = {p1,p2,p3,p4,p5};
        NamedRegion testRegion = new NamedRegion("testRegion",vertices);


        boolean expectedIsInRegion = true;
        boolean actualIsInRegion = lngLatImplemenation.isInRegion(testPoint, testRegion);
        Assert.assertEquals(expectedIsInRegion, actualIsInRegion);
    }
    @Test

    public void testIsCloseTo() {
        LngLatImplemenation lngLatImplemenation = new LngLatImplemenation();
        LngLat startPosition = new LngLat(0, 0);
        LngLat otherPosition = new LngLat(3, 4);
        boolean expectedIsClose = false;
        boolean actualIsClose = lngLatImplemenation.isCloseTo(startPosition, otherPosition);
        Assert.assertEquals(expectedIsClose, actualIsClose);
    }
    @Test
    public void testIsInRegion() {
        LngLatImplemenation lngLatImplemenation = new LngLatImplemenation();
        LngLat position = new LngLat(2, 2);
        NamedRegion region = new NamedRegion("Test Region", new LngLat[]{new LngLat(0, 0), new LngLat(0, 5), new LngLat(5, 5), new LngLat(5, 0)});
        boolean expectedIsInRegion = true;
        boolean actualIsInRegion = lngLatImplemenation.isInRegion(position, region);
        Assert.assertEquals(expectedIsInRegion, actualIsInRegion);
    }
    @Test
    public void testDistanceTo() {
        LngLatImplemenation lngLatImplemenation = new LngLatImplemenation();
        LngLat startPosition = new LngLat(0, 0);
        LngLat endPosition = new LngLat(3, 4);
        double expectedDistance = 5.0;
        double actualDistance = lngLatImplemenation.distanceTo(startPosition, endPosition);
        Assert.assertEquals(expectedDistance, actualDistance, 0.0001);
    }

}

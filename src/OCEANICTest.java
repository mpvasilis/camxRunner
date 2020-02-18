import org.junit.Test;

import static org.junit.Assert.*;

public class OCEANICTest {


    @Test
    public void downloadOceanicChlorophyllData() {
        OCEANIC oc = new OCEANIC();
        String output= oc.downloadOceanicChlorophyllData("2015001","");
        oc.convert2NETCDF(output, "out1", "out2");
    }
}
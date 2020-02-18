import org.junit.Test;

import javax.print.attribute.standard.JobOriginatingUserName;
import java.io.IOException;

import static org.junit.Assert.*;

public class O3MAPTest {

    @Test
    public void downloadO3data() {
        CAMxSIMULATION camxSimulation= new CAMxSIMULATION();

        WRF wrf = new WRF("wrfout_d05_2036-03-05_00_00_00.nc");
        try {
            wrf.readWRFOutput();

        } catch (IOException e) {
            e.printStackTrace();
        }
        O3MAP o3MAP = new O3MAP(camxSimulation);
       // o3MAP.createJobFile(wrf);

        String JOBFILE = "./src_v3/o3map << EOF \n" +
                "Coordinate project |LAMBERT\n" +
                "xo,yo,cln,clt,t1,t2|1026,-1170,10,52,30,45\n" +
                "dx,dy              |2.,2.\n" +
                "nx,ny              |42,42\n" +
                "Output filename    |/opt/bin/camx/inputs_mine/o3."+wrf.getSIMULATION_START_DATE_format2()+".txt\n" +
                "Numbr of TOMS files|" +wrf.getSimulationDays()+"\n";

        for (int i = 0; i <wrf.getSimulationDays(); i++) {
            JOBFILE=JOBFILE+ "Bday,Eday,TOMS file|"+wrf.getSIMULATION_START_DATE_format2(i)+","+wrf.getSIMULATION_START_DATE_format2(i)+","+wrf.getSIMULATION_START_DATE_format2(i)+".txt \n";
        }
        JOBFILE = JOBFILE+ "EOF";
        System.out.println(JOBFILE);
    }
}
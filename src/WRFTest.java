import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import static org.junit.Assert.*;

public class WRFTest {

    @Test
    public void readWRFOutputTest() {
        WRF wrf = new WRF("wrfout_d05_2036-03-05_00_00_00.nc");
        try {
            wrf.readWRFOutput();

        } catch (IOException e) {
            e.printStackTrace();
        }
        String starttime= wrf.getSIMULATION_START_DATE();
        System.out.println(starttime);
        String endtime= wrf.getSIMULATION_END_DATE();
        System.out.println(endtime);

        System.out.println(wrf.getSIMULATION_START_DATE_format2());
        System.out.println(wrf.getSIMULATION_END_DATE_format2());

        System.out.println( wrf.getSimulationDays());
        System.out.println( wrf.dayswithcountStart());
        System.out.println( wrf.dayswithcountEnd());
        System.out.println( wrf.getYear()+wrf.getJulianMonth());
        System.out.println( wrf. getforeachdy());
        if(wrf.isFuture()){
            System.out.println("Future");
        }

        try {
            wrf.createLandMask("landmask.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println( wrf. getMonthString(0));
        System.out.println( wrf. getMonthString(30));

        System.out.println( wrf. getJulianDate(0));
        System.out.println( wrf. getJulianDate(1));


    }
    @Test
    public void procTest() {


        Runtime rt = Runtime.getRuntime();
        String[] commands = {"C:\\Program Files (x86)\\PuTTY\\putty.exe"};
        Process proc = null;
        try {
            proc = rt.exec(commands);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));
        String s = null;
        while (true) {
            try {
                if (!((s = stdInput.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(s);
        }
        while (true) {
            try {
                if (!((s = stdError.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(s);
        }
        System.out.println(proc.exitValue());

    }

}
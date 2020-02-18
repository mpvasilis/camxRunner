/*
 * Copyright (c) 2020. Vasileios Balafas
 */

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CAMxPMTest {

    @Test
    public void readCAMxOutput() {
        CAMxPM camxpm = new CAMxPM("CAMx.thess.150102.avrg.grd01.nc");
        try {
            camxpm.readCAMxOutput(0,"PM2.5");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPost() {
        CAMxPM camxpm = new CAMxPM("CAMx.thess.150102.avrg.grd01.nc");
        try {
            camxpm.readCAMxOutput(0,"PM2.5");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
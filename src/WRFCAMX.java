import java.io.*;
import java.util.Map;

public class WRFCAMX extends CAMxSIMULATION{

    private String jobfile_dir=PREPROCESSORS_DIR+"wrfcamx/";
    private String jobfile="4_wrfcamx.run";
    private String module_dir=PREPROCESSORS_DIR+"wrfcamx/";
    private String work_dir="";
    private String input_dir="";
    private String output_dir="";

    CAMxSIMULATION caMxSIMULATION;

    public WRFCAMX(CAMxSIMULATION caMxSIMULATION) {

        this.caMxSIMULATION=caMxSIMULATION;

    }

    public void createJobFile(WRF wrf){
        String WRFOUTFILE = wrf.getNcfile();
        String STARTDAY = wrf.getSIMULATION_START_DATE_format2();
        String ENDDAY =  wrf.getSIMULATION_END_DATE_format2();
        String xo = caMxSIMULATION.getXo();
        String yo = caMxSIMULATION.getYo();

        String days="";
        for (int i = 0; i < wrf.getSimulationDays() ; i++) {
            days=days+wrf.getSIMULATION_START_DATE_format2(i)+" ";
        }

        try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {
            String JOBFILE = "#!/bin/csh\n" +
                    "setenv NO_STOP_MESSAGE yes\n" +
                    "#\n" +
                    "set PATH = \""+wrf.getDIR()+"\"\n" +
                    "set OUT  = \"/opt/bin/camx/met_mine_new\"\n" +
                    "if (! -d $OUT) mkdir $OUT\n" +
                    " \n" +
                    "foreach DAY ("+days+")\n" +
                    "if( $DAY == \""+STARTDAY+"\" ) then\n" +
                    "   set START = $DAY\"00\"\n" +
                    "else\n" +
                    "   set START = $DAY\"00\"\n" +
                    "endif\n" +
                    "set FIN   = $DAY\"24\"\n" +
                    " \n" +
                    module_dir+"/src/wrfcamx << ieof \n" +
                    "Diagnostic Fields  |T\n" +
                    "KV Method          |ALL\n" +
                    "Minimum Kv         |0.1\n" +
                    "Project            |LAMBERT\n" +
                    "Subgrid convecton  |DIAG\n" +
                    "Subgrid stratiform |T\n" +
                    "Start/end date     |$START $FIN\n" +
                    "WRF output freq    |60\n" +
                    "Grid time zone     |0\n" +
                    "CAMx grid size     |40, 40, 29\n" +
                    "CAMx Grid spacing  |2. 2.\n" +
                    "CAMx orig & params |"+xo+"., "+yo+"., 10., 52., 35., 65.\n" +
                    "Layer mapping      |1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29\n" +
                    "CAMx LU file       |$OUT/camx.lu.bin\n" +
                    "CAMx 3D file       |$OUT/camx.3d.$DAY.bin\n" +
                    "CAMx 2D file       |$OUT/camx.2d.$DAY.bin\n" +
                    "CAMx Kv file       |$OUT/camx.kv.$DAY.bin\n" +
                    "CAMx Cld/rain file |$OUT/camx.cr.$DAY.bin\n" +
                    "Make pre-6.3 snow? |T\n" +
                    "v6.3+ In snow age  |\n" +
                    "v6.3+ Init snow age|\n" +
                    "v6.3+ Out snow age |\n" +
                    "WRF filename       |" +WRFOUTFILE+"\n"+
                    "ieof\n" +
                    "#151125 151125\n" +
                    "end";

            out.println(JOBFILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"/bin/csh",jobfile_dir+jobfile};
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
    }
}

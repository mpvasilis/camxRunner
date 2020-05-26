import java.io.*;
import java.util.Map;

public class MOZART2CAMX extends CAMxSIMULATION {

    private String jobfile_dir=PREPROCESSORS_DIR+"mozart2camx/jobs/";
    private String jobfile="6_mozart2camx.run";
    private String module_dir=PREPROCESSORS_DIR+"mozart2camx/";
    private String work_dir="";
    private String input_dir="";
    private String output_dir="";

    private String RUNDATE;
    private String RUNHOURS;
    private String RUNCITY ;
    private String CORES ;
    private String SCENARIO ;

    public MOZART2CAMX(String RUNDATE, String RUNHOURS, String RUNCITY, String CORES, String SCENARIO) {
        this.RUNDATE = RUNDATE;
        this.RUNHOURS = RUNHOURS;
        this.RUNCITY = RUNCITY;
        this.CORES = CORES;
        this.SCENARIO = SCENARIO;
    }

    public void createJobFile(WRF wrf){


        String YEAR = wrf.getYear();
        String JDBEGs =  wrf.getSIMULATION_START_DATE_format2();
        String JDENDS =  wrf.getSIMULATION_END_DATE_format2();
        String month = wrf.getMonthString(Integer.parseInt(wrf.getMonth()));
        try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {

            String JOBFILE = "#!/bin/csh -f\n" +
                    "setenv PROMPTFLAG N\n" +
                    "setenv IOAPI_ISPH 20\n" +
                    "set PATH = "+module_dir+"/outputs/\n" +
                    "set MET =  /opt/bin/camx/met_mine_new\n" +
                    "set OUTPATH = /opt/bin/camx/inputs_mine_new\n" +
                    "mkdir -p $OUTPATH\n" +
                    "set JDBEGs = ("+JDBEGs+")\n" +
                    "set JDENDs = ("+JDENDS+")\n" +
                    "foreach i (1)\n" +
                    "set JDATE = $JDBEGs[$i]\n" +
                    "set JDEND = $JDENDs[$i]\n" +
                    "while ($JDATE <= $JDEND)\n" +
                    "set DATE = $JDATE\n" +
                    "# DEFINE OUTPUT FILE NAMES\n" +
                    "setenv OUTFILEBC  $OUTPATH/bc.cb6r2.$DATE.bin\n" +
                    "setenv OUTFILEIC  $OUTPATH/ic.cb6r2.$DATE.hr0.bin\n" +
                    "setenv OUTFILETC  $OUTPATH/tc.cb6r2.$DATE.bin\n" +
                    "setenv OUTFILE3D  $OUTPATH/3d.cb6r2.$DATE.bin\n" +
                    "# DEFINE INPUT MOZART FILES\n" +
                    "# IF MORE THAN 1 MOZART FILE IS NEEDED, ADD setenv INFILE2\n" +
                    "set NINFILE = 1\n" +
                    "setenv INFILE1    $PATH/OUTFILE3D."+YEAR+"."+month+"\n" +
                    "setenv MOZART_EVAL no\n" +
                     module_dir+"/src/mozart2camx_CB05_CF__GEOS5 << IEOF \n" +
                    "CAMx5,CAMx6,CMAQ   |CAMx 6\n" +
                    "ProcessDateYYYYMMDD|20$DATE\n" +
                    "Output BC file?    |.true.\n" +
                    "Output IC file?    |.true.\n" +
                    "If IC, starting hr |0\n" +
                    "Output TC file?    |.false.\n" +
                    "Max num MZRT files |$NINFILE\n" +
                    "CAMx 3D met file   |$MET/camx.3d.$DATE.bin\n" +
                    "CAMx 2D met file   |$MET/camx.2d.$DATE.bin\n" +
                    "IEOF\n" +
                    "mv OUTFILEIC $OUTFILEIC\n" +
                    "mv OUTFILEBC $OUTFILEBC\n" +
                    "mv OUTFILETC $OUTFILETC\n" +
                    "mv OUTFILE3D $OUTFILE3D\n" +
                    "@ JDATE ++\n" +
                    "if ($JDATE == 2011366 ) set JDATE = 2012001\n" +
                    "if ($JDATE == 2012367 ) set JDATE = 2013001\n" +
                    "end # while\n" +
                    "end # foreach";

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
        String mozartDataset = "/opt/bin/camx_preprocessors/mozart2camx/data/Clustering/h"+runID+".nc";
        File mozartDatasetFile = new File(mozartDataset);
        mozartDatasetFile.delete();

    }
}

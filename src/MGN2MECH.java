import java.io.*;
import java.util.Map;

public class MGN2MECH extends CAMxSIMULATION {

    private String jobfile_dir=PREPROCESSORS_DIR+"MEGAN/MEGANv2.10/work";
    private String jobfile= this.getClass().getSimpleName()+".run";
    private String module_dir=PREPROCESSORS_DIR+"MEGAN/MEGANv2.10";
    private String work_dir="";
    private String input_dir=PREPROCESSORS_DIR+"MEGAN/MEGANv2.10/Input";
    private String output_dir=PREPROCESSORS_DIR+"MEGAN/MEGANv2.10/Output";

    private String RUNDATE;
    private String RUNHOURS;
    private String RUNCITY ;
    private String CORES ;
    private String SCENARIO ;

    public MGN2MECH(String RUNDATE, String RUNHOURS, String RUNCITY, String CORES, String SCENARIO) {
        this.RUNDATE = RUNDATE;
        this.RUNHOURS = RUNHOURS;
        this.RUNCITY = RUNCITY;
        this.CORES = CORES;
        this.SCENARIO = SCENARIO;
    }

    public void createJobFile(WRF wrf){
        DATEFUN df = new DATEFUN(RUNDATE);

        String WRFOUTFILE = wrf.getNcfile();
        String STARTDAY = wrf.getSIMULATION_START_DATE_format2();
        String ENDDAY =  wrf.getSIMULATION_END_DATE_format2();
        int NCOLS = wrf.getGrid_size_x();
        int NROWS = wrf.getGrid_size_y();

        try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {
            String JOBFILE = "#! /bin/csh -f\n" +
                    "########################################################################\n" +
                    "source /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/setcase.csh\n" +
                    "## Directory setups\n" +
                    "setenv NO_STOP_MESSAGE 1\n"+
                    "setenv PRJ thess \n" +
                    "setenv PROMPTFLAG N\n" +
                    "# Program directory\n" +
                    "setenv PROG   mgn2mech.wmap\n" +
                    "setenv EXEDIR $MGNEXE\n" +
                    "setenv EXE    /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/src/MGN2MECH/$PROG\n" +
                    "# Input map data directory\n" +
                    "setenv INPDIR $MGNINP/trial\n" +
                    "# Intermediate file directory\n" +
                    "setenv INTDIR $MGNOUT/emproc_output\n" +
                    "# Output directory\n" +
                    "setenv OUTDIR $MGNOUT/mgn2mech_output\n" +
                    "# MCIP input directory\n" +
                    "setenv METDIR \"/opt/bin/camx_preprocessors/MCIP/output\"\n" +
                    "# Log directory\n" +
                    "setenv LOGDIR $MGNLOG/$PROG\n" +
                    "if ( ! -e $LOGDIR ) mkdir -p $LOGDIR\n" +
                    "########################################################################\n" +
                    "set dom = 36\n" +
                    "set JD = " + wrf.getStartJulianDate()+"\n" +
                    "while ($JD <= "+wrf.getEndJulianDate()+")\n" +
                    "########################################################################\n" +
                    "# Set up time and date to process\n" +
                    "setenv SDATE $JD        #start date\n" +
                    "setenv STIME 0\n" +
                    "setenv RLENG 240000\n" +
                    "setenv TSTEP 10000\n" +
                    "########################################################################\n" +
                    "########################################################################\n" +
                    "# Set up for MECHCONV\n" +
                    "setenv RUN_SPECIATE   Y    # run MG2MECH\n" +
                    "setenv RUN_CONVERSION Y    # run conversions?\n" +
                    "                           # run conversions MEGAN to model mechanism\n" +
                    "                           # units are mole/s\n" +
                    "setenv SPCTONHR       Y    # speciation output unit in tonnes per hour\n" +
                    "                           # This will convert 138 species to tonne per\n" +
                    "                           # hour or mechasnim species to tonne per hour.\n" +
                    "                           \n" +
                    "# If RUN_CONVERSION is set to \"Y\", one of mechanisms has to be selected.\n" +
                    "#setenv MECHANISM    RADM2\n" +
                    "#setenv MECHANISM    RACM\n" +
                    "#setenv MECHANISM    CBMZ\n" +
                    "#setenv MECHANISM    CB05\n" +
                    "setenv MECHANISM    CB6\n" +
                    "#setenv MECHANISM    SOAX\n" +
                    "#setenv MECHANISM    SAPRC99\n" +
                    "#setenv MECHANISM    SAPRC99Q\n" +
                    "#setenv MECHANISM    SAPRC99X\n" +
                    "# Grid name\n" +
                    "setenv GDNAM3D ${PRJ} \n" +
                    "# EFMAPS NetCDF input file\n" +
                    "setenv EFMAPS  $INPDIR/EFMAPS.${PRJ}${dom}.ncf\n" +
                    "# PFTS16 NetCDF input file\n" +
                    "setenv PFTS16  $INPDIR/PFTS16.${PRJ}${dom}.ncf\n" +
                    "# MEGAN ER filename\n" +
                    "setenv MGNERS $INTDIR/ER.$GDNAM3D.${SDATE}.ncf\n" +
                    "# Output filename\n" +
                    "setenv MGNOUT $OUTDIR/MEGANv2.10.$GDNAM3D.$MECHANISM.$SDATE.ncf\n" +
                    "########################################################################\n" +
                    "## Run speciation and mechanism conversion\n" +
                    "if ( $RUN_SPECIATE == 'Y' ) then\n" +
                    "   rm -f $MGNOUT\n" +
                    "   $EXE | tee $LOGDIR/log.run.$PROG.$GDNAM3D.$MECHANISM.$SDATE.txt\n" +
                    "endif\n" +
                    "@ JD++\n" +
                    "end  # End while JD";


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

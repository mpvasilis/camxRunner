import java.io.*;
import java.util.Map;

public class EMPROC extends CAMxSIMULATION{

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

    public EMPROC(String RUNDATE, String RUNHOURS, String RUNCITY, String CORES, String SCENARIO) {
        this.RUNDATE = RUNDATE;
        this.RUNHOURS = RUNHOURS;
        this.RUNCITY = RUNCITY;
        this.CORES = CORES;
        this.SCENARIO = SCENARIO;
    }

    public void createJobFile(WRF wrf){
        DATEFUN df = new DATEFUN(RUNDATE);

        String WRFOUTFILE = wrf.getNcfile();
        String YEAR = wrf.getYear();


        try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {

            String JOBFILE = "#! /bin/csh -f\n" +
                    "########################################################################\n" +
                    "source /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/setcase.csh\n" +
                    "## Directory setups\n" +
                    "setenv PRJ thess \n" +
                    "setenv PROMPTFLAG N\n" +
                    "# Program directory\n" +
                    "setenv PROG   emproc\n" +
                    "setenv EXEDIR $MGNEXE\n" +
                    "setenv EXE    /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/src/EMPROC/emproc\n" +
                    "#Input map data directory\n" +
                    "setenv INPDIR /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/Input/trial\n" +
                    "# MCIP input directory\n" +
                    "setenv METDIR /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/Output/met2mgn_output\n" +
                    "# Intermediate file directory\n" +
                    "#setenv INTDIR $MGNINT\n" +
                    "setenv INTDIR /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/Output/emproc_output\n" +
                    "# Output directory\n" +
                    "#setenv OUTDIR $MGNOUT\n" +
                    "setenv OUTDIR /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/Output/emproc_output\n" +
                    "# Log directory\n" +
                    "setenv LOGDIR $MGNLOG/$PROG\n" +
                    "if ( ! -e $LOGDIR ) mkdir -p $LOGDIR\n" +
                    "########################################################################\n" +
                    "set dom = 36 \n" +
                    "set JD = " + wrf.getStartJulianDate()+"\n" +
                    "while ($JD <= "+wrf.getEndJulianDate()+")\n" +
                    "########################################################################\n" +
                    "# Set up time and date to process\n" +
                    "setenv SDATE $JD        #start date\n" +
                    "setenv STIME 0\n" +
                    "setenv RLENG 240000\n" + 
                    "########################################################################\n" +
                    "########################################################################\n" +
                    "# Set up for MEGAN\n" +
                    "setenv RUN_MEGAN   Y       # Run megan?\n" +
                    "# By default MEGAN will use data from MGNMET unless specify below\n" +
                    "setenv ONLN_DT     Y       # Use online daily average temperature\n" +
                    "                           # No will use from EFMAPS\n" +
                    "setenv ONLN_DS     Y       # Use online daily average solar radiation\n" +
                    "                           # No will use from EFMAPS\n" +
                    "# Grid definition\n" +
                    "setenv GRIDDESC \"/opt/bin/camx_preprocessors/MCIP/output/GRIDDESC\"\n" +
                    "setenv GDNAM3D thess\n" +
                    "# EFMAPS\n" +
                    "setenv EFMAPS $INPDIR/EFMAPS.${PRJ}${dom}.ncf\n" +
                    "# PFTS16\n" +
                    "setenv PFTS16 $INPDIR/PFTS16.${PRJ}${dom}.ncf\n" +
                    "# LAIS46\n" +
                    "setenv LAIS46 $INPDIR/LAIS46.${PRJ}${dom}.ncf\n" +
                    "# MGNMET\n" +
                    "setenv MGNMET $METDIR/MET.MEGAN_$GDNAM3D.${SDATE}.ncf\n" +
                    "# Output\n" +
                    "setenv MGNERS $INTDIR/ER.$GDNAM3D.${SDATE}.ncf\n" +
                    "########################################################################\n" +
                    "## Run MEGAN\n" +
                    "if ( $RUN_MEGAN == 'Y' ) then\n" +
                    "   rm -f $MGNERS\n" +
                    "    $EXE\n" +
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

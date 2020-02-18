import java.io.*;
import java.util.Map;

public class IOAPI2UAM extends CAMxSIMULATION{

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

    public IOAPI2UAM(String RUNDATE, String RUNHOURS, String RUNCITY, String CORES, String SCENARIO) {
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
                    "######################################################################\n" +
                    "#\n" +
                    "#   IOAPI2UAM converts CMAQ 1-D emissions files (I/O API) to CAMx\n" +
                    "#   low-level emissions files (UAM-IV). Since UAM-IV format limits\n" +
                    "#   length of species names up tp 10 characters, last 6 characters\n" +
                    "#   (normally blanks) of CMAQ species names are truncated. Emission\n" +
                    "#   rate is converted from mol/s (or g/s) to mol/hr (or g/hr).It also\n" +
                    "#   shifts time-zone from GMT to user-selected local time.\n" +
                    "#\n" +
                    "#   INPUT ENVIRONMENTAL VARIABLES:\n" +
                    "#\n" +
                    "#      INFILE1      - Logical name for input file 1 (current day)\n" +
                    "#      INFILE2      - Logical name for input file 2 (next day;\n" +
                    "#                      required only if additional data is needed\n" +
                    "#                      due to time zone shifting; map projection\n" +
                    "#                      consistency won't be checked)\n" +
                    "#      OUTFILE      - Logical name for output file\n" +
                    "#      TZONE        - Output time-zone (8 for PST, etc.)\n" +
                    "#      SDATE        - Output start date (YYJJJ)\n" +
                    "#      STIME        - Output start time (HHMMSS) in TZONE\n" +
                    "#      RLENG        - Output run length (HHMMSS; 240000 for a CAMx\n" +
                    "#                      daily emissions input)\n" +
                    "#\n" +
                    "######################################################################\n" +
                    "## Directory setups\n" +
                    "source /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/setcase.csh\n" +
                    "# Program directory\n" +
                    "setenv EXEDIR /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/src/IOAPI2UAM\n" +
                    "# Input directory\n" +
                    "setenv INPDIR /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/Output/mgn2mech_output\n" +
                    "# Output directory\n" +
                    "setenv OUTDIR /opt/bin/camx/emiss\n" +
                    "if ( ! -e $OUTDIR ) mkdir -p $OUTDIR\n" +
                    "# Log directory\n" +
                    "set LOGDIR = logdir/ioapi2uam_cst\n" +
                    "mkdir -p $LOGDIR\n" +
                    "setenv MECHANISM    CB6\n" +
                    "foreach dom (36 12)\n" +
                    "foreach scen ( J4 )\n" +
                    "setenv GDNAM3D thess\n" +
                    "set STJD = ("+wrf.getStartJulianDate()+")\n" +
                    "set EDJD = ("+wrf.getEndJulianDate()+")\n" +
                    "foreach i (1)\n" +
                    "set jd = $STJD[$i]\n" +
                    "while ( $jd <= $EDJD[$i])\n" +
                    "  if ($jd == 2005366) set jd = 2006001\n" +
                    "  setenv RLENG   230000\n" +
                    "  @ nd = $jd + 1\n" +
                    "  if ($jd == $EDJD[$i]) then\n" +
                    "    setenv INFILE1 $INPDIR/MEGANv2.10.$GDNAM3D.$MECHANISM.${jd}.ncf\n" +
                    "    setenv INFILE2 $INPDIR/MEGANv2.10.$GDNAM3D.$MECHANISM.${nd}.ncf\n" +
                    "    setenv OUTFILE $OUTDIR/MEGANv3.$GDNAM3D.$MECHANISM.${jd}.camx\n" +
                    "    setenv RLENG   230000\n" +
                    "    setenv TZONE   0\n" +
                    "    setenv SDATE   $jd\n" +
                    "    setenv STIME   0\n" +
                    "  else\n" +
                    "    setenv INFILE1 $INPDIR/MEGANv2.10.$GDNAM3D.$MECHANISM.${jd}.ncf\n" +
                    "    setenv OUTFILE $OUTDIR/new.MEGANv3.$GDNAM3D.$MECHANISM.${jd}.camx\n" +
                    "    setenv RLENG   240000\n" +
                    "    setenv TZONE   0\n" +
                    "    setenv SDATE   $jd\n" +
                    "    setenv STIME   000000\n" +
                    "  endif\n" +
                    "  rm -f $OUTFILE\n" +
                    "  $EXEDIR/ioapi2uam | tee $LOGDIR/log.run.ioapi2uam.$MECHANISM.$GDNAM3D.$scen.$jd.nostress.txt\n" +
                    "  @ jd++\n" +
                    "end  # End while\n" +
                    "end  # End foreach i\n" +
                    "end # scen\n" +
                    "end  # End foreach dom";


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

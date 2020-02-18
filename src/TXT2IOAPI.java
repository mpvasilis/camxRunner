import java.io.*;
import java.util.Map;

public class TXT2IOAPI extends CAMxSIMULATION {

    private String jobfile_dir=PREPROCESSORS_DIR+"MEGAN/MEGANv2.10/work/";
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

    public TXT2IOAPI(String RUNDATE, String RUNHOURS, String RUNCITY, String CORES, String SCENARIO) {
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
                    "## Common setups\n" +
                    "source /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/setcase.csh\n" +
                    "setenv PRJ thess \n" +
                    "setenv DOM 36 \n" +
                    "setenv PROMPTFLAG N\n" +
                    "setenv PROG   new_txt2ioapi\n" +
                    "setenv EXEDIR $MGNEXE\n" +
                    "setenv EXEC   \"$EXEDIR/$PROG\"\n" +
                    "setenv GRIDDESC $MGNRUN/GRIDDESC\n" +
                    "setenv GDNAM3D thess \n" +
                    "## File setups\n" +
                    "## Inputs\n" +
                    "#setenv EFSTXTF $MGNINP/MAP2/EF210_${PRJ}${DOM}.csv\n" +
                    "#setenv PFTTXTF $MGNINP/MAP2/PFT210_${PRJ}${DOM}.csv\n" +
                    "#setenv LAITXTF $MGNINP/MAP2/LAI210_${PRJ}${DOM}.csv\n" +
                    "setenv EFSTXTF $MGNINP/MAP2/EF210.csv\n" +
                    "setenv PFTTXTF $MGNINP/MAP2/PFT210.csv\n" +
                    "setenv LAITXTF $MGNINP/MAP2/LAI210.csv\n" +
                    "## Outputs\n" +
                    "setenv EFMAPS  $MGNINP/trial/EFMAPS.${PRJ}${DOM}.ncf\n" +
                    "setenv PFTS16  $MGNINP/trial/PFTS16.${PRJ}${DOM}.ncf\n" +
                    "setenv LAIS46  $MGNINP/trial/LAIS46.${PRJ}${DOM}.ncf\n" +
                    "## Run control\n" +
                    "setenv RUN_EFS T       # [T|F]\n" +
                    "setenv RUN_LAI T       # [T|F]\n" +
                    "setenv RUN_PFT T       # [T|F]\n" +
                    "########################################################################\n" +
                    "## Run TXT2IOAPI\n" +
                    "rm -f $EFMAPS $LAIS46 $PFTS16\n" +
                    "if ( ! -e $MGNLOG/$PROG ) mkdir -p $MGNLOG/$PROG\n" +
                    "$EXEC | tee $MGNLOG/$PROG/log.run.$PROG.${PRJ}${DOM}.txt";

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

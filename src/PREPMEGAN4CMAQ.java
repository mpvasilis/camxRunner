import java.io.*;
import java.util.Map;

public class PREPMEGAN4CMAQ extends CAMxSIMULATION {

    private String jobfile_dir=PREPROCESSORS_DIR+"MEGAN/prepmegan4cmaq_2014-06-02/";
    private String jobfile="7_prepmegan4cmaq.run";
    private String module_dir=PREPROCESSORS_DIR+"MEGAN/prepmegan4cmaq_2014-06-02/";
    private String work_dir="";
    private String input_dir="";
    private String output_dir="";

    private String RUNDATE;
    private String RUNHOURS;
    private String RUNCITY ;
    private String CORES ;
    private String SCENARIO ;

    public PREPMEGAN4CMAQ(String RUNDATE, String RUNHOURS, String RUNCITY, String CORES, String SCENARIO) {
        this.RUNDATE = RUNDATE;
        this.RUNHOURS = RUNHOURS;
        this.RUNCITY = RUNCITY;
        this.CORES = CORES;
        this.SCENARIO = SCENARIO;
    }

    public void createJobFile(WRF wrfoutput, WRF wrfinput, int runID){
       if(runID==0) { LAI_input_dir="/hdd2/bouras/camx_preprocessors/MEGAN/prepmegan4cmaq_2014-06-02/input_global"; } else{
           LAI_input_dir="/opt/bin/camx_preprocessors/MEGAN/prepmegan4cmaq_2014-06-02/input_global";
       }

        try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {

            String JOBFILE = "cd " +module_dir+ "\n" +
                    "/opt/bin/camx_preprocessors/MEGAN/prepmegan4cmaq_2014-06-02/prepmegan4cmaq_lai.x < prepmegan4cmaq.inp #> lai.log  \n" +
                    "/opt/bin/camx_preprocessors/MEGAN/prepmegan4cmaq_2014-06-02/prepmegan4cmaq_pft.x < prepmegan4cmaq.inp #> pft.log  \n" +
                    "/opt/bin/camx_preprocessors/MEGAN/prepmegan4cmaq_2014-06-02/prepmegan4cmaq_ef.x < prepmegan4cmaq.inp #> ef.log  ";
            out.println(JOBFILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try (PrintWriter out = new PrintWriter(jobfile_dir+"prepmegan4cmaq.inp")) {

            String JOBFILE = "&control\n" +
                    "domains = 1,\n" +
                    "start_lai_mnth = 1,\n" +
                    "end_lai_mnth   = 12,\n" +
                    "wrf_dir   = '"+wrfinput.getDIR()+"',\n" +
                    "megan_dir = '"+LAI_input_dir+"',\n" +
                    "out_dir = '/opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/Input/MAP2',\n" +
                    "/\n" +
                    "&windowdefs\n" +
                    "x0 = 1,\n" +
                    "y0 = 1,\n" +
                    "ncolsin = 40,\n" +
                    "nrowsin = 40,\n" +
                    "/";
            out.println(JOBFILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        Runtime rt = Runtime.getRuntime();
        String[] commands = {RUNC_CMD,jobfile_dir+jobfile};
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

import java.io.*;
import java.util.Map;

public class NFC2IOAPI extends CAMxSIMULATION {

    private String jobfile_dir=PREPROCESSORS_DIR+"mozart2camx/jobs/";
    private String jobfile="5_nfc2ioapi.run";
    private String module_dir=PREPROCESSORS_DIR+"mozart2camx/";
    private String work_dir="";
    private String input_dir="";
    private String output_dir="";


    private String RUNDATE;
    private String RUNHOURS;
    private String RUNCITY ;
    private String CORES ;
    private String SCENARIO ;
    private String runID;

    public NFC2IOAPI(String RUNDATE, String RUNHOURS, String RUNCITY, String CORES, String SCENARIO,String runID) {
        this.RUNDATE = RUNDATE;
        this.RUNHOURS = RUNHOURS;
        this.RUNCITY = RUNCITY;
        this.CORES = CORES;
        this.SCENARIO = SCENARIO;
        this.runID = runID;
    }

    public String prepareMozartdataforFutureRun(String dataset, String year) throws IOException {
        EmissInvCopy copyfun = new EmissInvCopy();
        String newfile= "/opt/bin/camx_preprocessors/mozart2camx/data/Clustering/h"+runID+".nc";
        File src = new File(dataset);
        File dest = new File(newfile);
        copyfun.copyFile(src,dest);

        try {
            Process p = Runtime.getRuntime().exec("python3 /opt/bin/netcdf-change-dates.py "+newfile+" "+year);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newfile;

    }

    public void createJobFile(WRF wrf){
        String mozartDataset="/opt/bin/camx_preprocessors/mozart2camx/data/Clustering/h0001.nc";
      /* // if(runID.equals("0")){
        //     mozartDataset="/hdd2/bouras/camx_preprocessors/mozart2camx/data/2015/mozart4geos5.2015.jan.nc";
       // }

        if(true){//Integer.parseInt(wrf.getYear())>2018
            try {
                mozartDataset = prepareMozartdataforFutureRun(mozartDataset, wrf.getYear());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } */
        mozartDataset= "/opt/bin/camx_preprocessors/mozart2camx/data/Clustering/h"+runID+".nc";

        try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {

            String JOBFILE = "#!/bin/csh -f\n" +
                    "#\n" +
                    "#   INFILE    - MOZART output file (input to NCF2IOAPI).\n" +
                    "#   OUTFILE2D - I/O API file with 2D variables of INFILE (output from NCF2IOAPI).\n" +
                    "#   OUTFILE3D - I/O API file with 3D variables of INFILE (output from NCF2IOAPI).\n" +
                    "#\n" +
                    "#   MOZART_LAYER_LIMIT - The MOZART layer which serves as top layer for BC outputs;\n" +
                    "#                        Set this to avoid stratospheric impact.\n" +
                    "#\n" +
                    "setenv IOAPI_ISPH 19\n" +
                    "setenv MOZART_LAYER_LIMIT 35\n" +
                    "#mkdir -p ../outputs\n" +
                    "foreach YYYY ("+wrf.getYear()+")\n" +
                    "foreach MMM ("+wrf.getMonthString(0).toLowerCase()+")\n" +
                    "setenv OUTFILE3D "+module_dir+"outputs/OUTFILE3D.$YYYY.$MMM\n" +
                    "setenv INFILE "+mozartDataset+"\n" +
                    " \n" +
                    module_dir+"/ncf2ioapi_mozart/NCF2IOAPI\n" +
                    "end\n" +
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

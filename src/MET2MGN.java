import java.io.*;
import java.util.Map;

public class MET2MGN extends CAMxSIMULATION {

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

    public MET2MGN(String RUNDATE, String RUNHOURS, String RUNCITY, String CORES, String SCENARIO) {
        this.RUNDATE = RUNDATE;
        this.RUNHOURS = RUNHOURS;
        this.RUNCITY = RUNCITY;
        this.CORES = CORES;
        this.SCENARIO = SCENARIO;
    }

    public void createJobFile(WRF wrf){

        try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {
            String JOBFILE = "#!/bin/csh\n" +
                    "#\n" +
                    "# MET2MGN v2.10 \n" +
                    "# --\n" +
                    "#\n" +
                    "#\n" +
                    "# TPAR2IOAPI v2.03a \n" +
                    "# --added 26-category landuse capability for mm5camx (number of landuse categories defined by NLU) \n" +
                    "# --added capability for LATLON and UTM projections\n" +
                    "# --added capability for MCIP v3.3 input (2m temperatures)\n" +
                    "# --bug in PAR processing subroutine fixed where first few hours in GMT produced zero PAR\n" +
                    "# --added code to fill missing par data (if valid data exists for the hours surrounding it)\n" +
                    "#\n" +
                    "# TPAR2IOAPI v2.0\n" +
                    "# --added capability for MM5 or MCIP input\n" +
                    "# \n" +
                    "#\n" +
                    "#        RGRND/PAR options:\n" +
                    "#           setenv MM5RAD  Y   Solar radiation obtained from MM5\n" +
                    "#           OR \n" +
                    "#           setenv MCIPRAD Y   Solar radiation obtained from MCIP\n" +
                    "#                  --MEGAN will internally calculate PAR for each of these options and user needs to  \n" +
                    "#                    specify `setenv PAR_INPUT N' in the MEGAN runfile\n" +
                    "#           OR\n" +
                    "#           setenv SATPAR Y (satellite-derived PAR from UMD GCIP/SRB files)\n" +
                    "#                  --user needs to specify `setenv PAR_INPUT Y' in the MEGAN runfile\n" +
                    "#\n" +
                    "#        TEMP options:\n" +
                    "#           setenv CAMXTEMP Y         2m temperature, calculated from mm5camx output files\n" +
                    "#           OR\n" +
                    "#           setenv MM5MET  Y         2m temperature, calculated from MM5 output files\n" +
                    "#                                     Note: 2m temperature is calculated since the P-X/ACM PBL\n" +
                    "#                                     MM5 configuration (most commonly used LSM/PBL scheme for AQ \n" +
                    "#                                     modeling purposes) does not produce 2m temperatures.\n" +
                    "#           OR\n" +
                    "#           setenv MCIPMET Y         temperature obtained from MCIP\n" +
                    "#              -setenv TMCIP  TEMP2   2m temperature, use for MCIP v3.3 or newer\n" +
                    "#              -setenv TMCIP  TEMP1P5 1.5m temperature, use for MCIP v3.2 or older\n" +
                    "#\n" +
                    "#        TZONE   time zone for input mm5CAMx files \n" +
                    "#        NLAY    number of layers contained in input mm5CAMx files \n" +
                    "#        NLU     number of landuse categories contained in CAMx landuse file \n" +
                    "#\n" +
                    "############################################################\n" +
                    "############################################################\n" +
                    "# Episodes\n" +
                    "############################################################\n" +
                    "set dom = 36 \n" +
                    "set STJD = " + wrf.getStartJulianDate()+"\n" +
                    "set EDJD = " + wrf.getEndJulianDate()+"\n" +
                    "setenv EPISODE_SDATE "+ wrf.getStartJulianDate()+"\n" +
                    "setenv EPISODE_STIME  000000    \n" +
                    "############################################################\n" +
                    "#set for grid\n" +
                    "############################################################\n" +
                    "setenv GRIDDESC \"/opt/bin/camx_preprocessors/MCIP/output/GRIDDESC\"\n" +
                    "setenv GDNAM3D thess\n" +
                    "############################################################\n" +
                    "# Setting up directories and common environment variable\n" +
                    "############################################################\n" +
                    "source /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/setcase.csh\n" +
                    "setenv PROG met2mgn\n" +
                    "setenv EXE /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/src/MET2MGN/$PROG\n" +
                    "set logdir = logdir/$PROG\n" +
                    "if ( ! -e $logdir) mkdir -p $logdir\n" +
                    "set INPPATH     = /opt/bin/camx_preprocessors/MCIP/output\n" +
                    "set OUTPATH     = /opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/Output/met2mgn_output\n" +
                    "if (! -e $OUTPATH) mkdir $OUTPATH\n" +
                    "setenv PFILE $OUTPATH/PFILE\n" +
                    "rm -fv $PFILE\n" +
                    "############################################################\n" +
                    "# Looping\n" +
                    "############################################################\n" +
                    "set JDATE = $STJD\n" +
                    "set DD = 21 \n" +
                    "set DDm1 = 20\n" +
                    "while ($JDATE <= $EDJD)\n" +
                    "if ($JDATE == 2008367) set JDATE = 2009001\n" +
                    "@ jdy  = $JDATE - 2000000\n" +
                    "@ DD++\n" +
                    "@ JDATEm1 = $JDATE - 1\n" +
                    "if ($JDATEm1 == 2008000) set JDATEm1 = 2007365\n" +
                    "@ jdym1  = $JDATEm1 - 2000000\n" +
                    "@ DDm1++\n" +
                    "#set start/end dates\n" +
                    "if ($JDATE == $EPISODE_SDATE) then \n" +
                    "setenv STDATE ${jdy}01\n" +
                    "else\n" +
                    "setenv STDATE ${jdy}00 \n" +
                    "endif\n" +
                    "setenv ENDATE ${jdy}24\n" +
                    "setenv MM5MET N\n" +
                    "setenv MM5RAD N\n" +
                    "#set if using MCIP output files\n" +
                    "setenv MCIPMET Y\n" +
                    "setenv TMCIP  TEMP2          #MCIP v3.3 or newer\n" +
                    "#setenv TMCIP  TEMP1P5       #MCIP v3.2 or older\n" +
                    "setenv MCIPRAD Y \n" +
                    "if ($JDATE == $EPISODE_SDATE) then\n" +
                    "  setenv METCRO2Dfile1 \"/opt/bin/camx_preprocessors/MCIP/output/METCRO2D_thess\"\n" +
                    "else\n" +
                    "  setenv METCRO2Dfile1 \"/opt/bin/camx_preprocessors/MCIP/output/METCRO2D_thess\"\n" +
                    "  setenv METCRO2Dfile2 \"/opt/bin/camx_preprocessors/MCIP/output/METCRO2D_thess\"\n" +
                    "endif\n" +
                    "setenv METCRO3Dfile  \"/opt/bin/camx_preprocessors/MCIP/output/METCRO3D_thess\"\n" +
                    "setenv METDOT3Dfile  \"/opt/bin/camx_preprocessors/MCIP/output/METDOT3D_thess\"\n" +
                    "setenv OUTFILE $OUTPATH/MET.MEGAN_thess.$JDATE.ncf\n" +
                    "rm -rf $OUTFILE\n" +
                    "$EXE |tee $logdir/log.$PROG._thess.$JDATE.txt \n" +
                    "@ JDATE++\n" +
                    "end  # End while JDATE";


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

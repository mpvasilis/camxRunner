import java.io.*;
import java.util.Map;

public class CAMx extends CAMxSIMULATION {

    private String jobfile_dir="/opt/bin/camx/runfiles/";
    private String module_dir="/opt/bin/camx";
    private String work_dir="";
    private String input_dir="";
    private String output_dir="";

    private String CORES ;

    CAMxSIMULATION caMxSIMULATION;

    public CAMx(CAMxSIMULATION caMxSIMULATION,String CORES) {

        this.caMxSIMULATION=caMxSIMULATION;
        this.CORES=CORES;

    }

    public void createJobFile(WRF wrf){

        try (PrintWriter out = new PrintWriter(jobfile_dir+"main.camx.run")) {
            String JOBFILE = "#!/bin/csh\n" +
                    "#\n" +
                    "# CAMx 6.50\n" +
                    "#\n" +
                    "#setenv NCPUS " + CORES+ "\n" +
                    "#setenv OMP_NUM_THREADS " + CORES+"\n" +
                    "#setenv OMP_STACKSIZE 254M\n" +
                    "set EXEC      = \"/usr/bin/mpirun -np " + CORES+" /opt/bin/camx/CAMx.v6.50.openMPI.NCF4.gfortran\"\n" +
                    "#\n" +
                    "set RUN     = \"thess\"\n" +
                    "set INPUT   = \"/opt/bin/camx/inputs_mine_new\"\n" +
                    "set MET     = \"/opt/bin/camx/met_mine_new\"\n" +
                    "set EMIS    = \"/opt/bin/camx/emiss\"\n" +
                    "set PTSRCE  = \"/opt/bin/camx/elevated_point_sources\"\n" +
                    "set OUTPUT  = \"/opt/bin/camx/outputs_mine_new\"\n" +
                    "#\n" +
                    "mkdir -p $OUTPUT\n" +
                    "#\n" +
                    "#  --- set the dates and times ----\n" +
                    "#\n" +
                    "set RESTART = \"NO\"\n" +
                    "set LASTDAY = "+wrf.getEndJulianDate().substring(4)+"\n" +
                    "foreach today ("+wrf.CAMX_getalldates()+")\n" +
                    "set JUL = $today:e\n" +
                    "set CAL = $today:r\n" +
                    "if( ! { $JUL == $LASTDAY } ) then\n" +
                    " set HOURS    = 2400\n" +
                    "else\n" +
                    " set HOURS    = 2300\n" +
                    "endif\n" +
                    "echo $JUL\n" +
                    "echo $CAL \n" +
                    "set YESTERDAY = `echo ${CAL} | awk '{printf(\"%2.2d\",$1-1)}'`\n" +
                    "#\n" +
                    "if( ${RESTART} == \"NO\" ) then\n" +
                    "        set RESTART = \"false\"\n" +
                    "else\n" +
                    "        set RESTART = \"true\"\n" +
                    "endif\n" +
                    "#\n" +
                    "#  --- Create the input file (always called CAMx.in)\n" +
                    "#\n" +
                    "cat << ieof > CAMx.in\n" +
                    " &CAMx_Control\n" +
                    " Run_Message      = 'CAMx Simulation -- CF CB6R4 $RUN',\n" +
                    "!--- Model clock control ---\n" +
                    " Time_Zone        = 0,                 ! (0=UTC,5=EST,6=CST,7=MST,8=PST)\n" +
                    " Restart          = .${RESTART}.,\n" +
                    " Start_Date_Hour  = "+wrf.getStartYear()+","+wrf.getStartMonth()+",${CAL},0000,   ! (YYYY,MM,DD,HHmm)\n" +
                    " End_Date_Hour    = "+wrf.getEndYear()+","+wrf.getEndMonth()+",+${CAL},${HOURS},   ! (YYYY,MM,DD,HHmm)\n" +
                    " Maximum_Timestep    = 10.,            ! minutes\n" +
                    " Met_Input_Frequency = 60.,            ! minutes\n" +
                    " Ems_Input_Frequency = 60.,            ! minutes\n" +
                    " Output_Frequency    = 60.,            ! minutes\n" +
                    "!--- Map projection parameters ---\n" +
                    " Map_Projection = 'LAMBERT',  ! (LAMBERT,POLAR,UTM,LATLON)\n" +
                    " UTM_Zone       = 0,\n" +
                    " Longitude_Pole = 10.,      ! deg (west<0,south<0)\n" +
                    " Latitude_Pole  = 52.,      ! deg (west<0,south<0)\n" +
                    " True_Latitude1 = 35.,      ! deg (west<0,south<0)\n" +
                    " True_Latitude2 = 65.,      ! deg (west<0,south<0)\n" +
                    "!--- Parameters for the master (first) grid ---\n" +
                    " Number_of_Grids      = 1,\n" +
                    " Master_SW_XCoord     = "+caMxSIMULATION.getXo()+".,         ! km or deg, SW corner of cell(1,1)\n" +
                    " Master_SW_YCoord     = "+caMxSIMULATION.getYo()+".,        ! km or deg, SW corner of cell (1,1)\n" +
                    " Master_Cell_XSize    = 2.,            ! km or deg\n" +
                    " Master_Cell_YSize    = 2.,            ! km or deg\n" +
                    " Master_Grid_Columns  = 40,\n" +
                    " Master_Grid_Rows     = 40,\n" +
                    " Number_of_Layers     = 29,\n" +
                    "!--- Parameters for the second grid ---\n" +
                    " Diagnostic_Error_Check = .false.,      ! True = will stop after 1st timestep\n" +
                    " Advection_Solver       = 'PPM',        ! (PPM,BOTT)\n" +
                    " Chemistry_Solver       = 'EBI',        ! (EBI,IEH,LSODE)\n" +
                    " PiG_Submodel           = 'GREASD',     ! (None,GREASD,IRON)\n" +
                    " Probing_Tool           = 'None',       ! (None,OSAT,GOAT,APCA,PSAT,DDM,PA,RTRAC)\n" +
                    " Chemistry              = .true.,\n" +
                    " Drydep_Model           = 'WESELY89',   ! (None,WESELY89,ZHANG03)\n" +
                    " Wet_Deposition         = .true.,\n" +
                    " ACM2_Diffusion         = .false.,\n" +
                    " Super_Stepping         = .true.,\n" +
                    " Gridded_Emissions      = .false.,\n" +
                    " Point_Emissions        = .true.,\n" +
                    " Ignore_Emission_Dates  = .true.,\n" +
                    " Flexi_Nest             = .false.,\n" +
                    " Inline_Ix_Emissions    = .true.,\n" +
                    "!--- Output specifications ---\n" +
                    " Root_Output_Name         = '$OUTPUT/CAMx.$RUN."+wrf.getSIMULATION_START_DATE_format2().substring(0, wrf.getSIMULATION_START_DATE_format2().length() - 2)+"${CAL}',\n" +
                    " Average_Output_3D        = .false.,\n" +
                    " NetCDF_Format_Output     = .true.,\n" +
                    " NetCDF_Use_Compression   = .false.,\n" +
                    " Output_Species_Names(1)   = 'ALL',\n" +
                    "!--- Input files ---\n" +
                    " Chemistry_Parameters = '$INPUT/CAMx6.5.chemparam.CB6r4_CF_SOAP_ISORROPIA',\n" + //ok
                    " Photolyis_Rates      = '$INPUT/tuv.do',\n" + //ok
                    " Ozone_Column         = '$INPUT/o3."+wrf.getSIMULATION_START_DATE_format2().substring(0, wrf.getSIMULATION_START_DATE_format2().length() - 2)+"${CAL}.txt',\n" +
                    " Initial_Conditions   = '$INPUT/ic.cb6r2."+wrf.getSIMULATION_START_DATE_format2()+".hr0.bin',\n" +
                    " Boundary_Conditions  = '$INPUT/bc.cb6r2."+wrf.getSIMULATION_START_DATE_format2().substring(0, wrf.getSIMULATION_START_DATE_format2().length() - 2)+"${CAL}.bin',\n" +
                    " Point_Sources        = '$PTSRCE/all.bin',\n" + //ok
                    " Master_Grid_Restart  = '$OUTPUT/CAMx.$RUN."+wrf.getSIMULATION_START_DATE_format2().substring(0, wrf.getSIMULATION_START_DATE_format2().length() - 2)+"${YESTERDAY}.inst',\n" +
                    " PiG_Restart          = ' ',\n" +
                    " Surface_Grid(1) = '$MET/camx.lu.bin',\n" +
                    " Met3D_Grid(1)   = '$MET/camx.3d."+wrf.getSIMULATION_START_DATE_format2().substring(0, wrf.getSIMULATION_START_DATE_format2().length() - 2)+"${CAL}.bin',\n" +
                    " Met2D_Grid(1)   = '$MET/camx.2d."+wrf.getSIMULATION_START_DATE_format2().substring(0, wrf.getSIMULATION_START_DATE_format2().length() - 2)+"${CAL}.bin',\n" +
                    " Vdiff_Grid(1)   = '$MET/camx.kv."+wrf.getSIMULATION_START_DATE_format2().substring(0, wrf.getSIMULATION_START_DATE_format2().length() - 2)+"${CAL}.bin.YSU',\n" +
                    " Cloud_Grid(1)   = '$MET/camx.cr."+wrf.getSIMULATION_START_DATE_format2().substring(0, wrf.getSIMULATION_START_DATE_format2().length() - 2)+"${CAL}.bin',\n" +
                    " Emiss_Grid(1)   = '$EMIS/emiss.anthro_bio_ocn."+wrf.getSIMULATION_START_DATE_format2().substring(0, wrf.getSIMULATION_START_DATE_format2().length() - 2)+"${CAL}.bin',\n" +
                    " /\n" +
                    "!-------------------------------------------------------------------------------\n" +
                    "ieof\n" +
                    "#\n" +
                    "#  --- Execute the model ---\n" +
                    "#\n" +
                    "if( ! { $EXEC } ) then\n" +
                    "   exit\n" +
                    "endif\n" +
                    "set RESTART = \"YES\"\n" +
                    "end";

            out.println(JOBFILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        runJobFile("main.camx.run");
    }


    public void runJobFile(String jobfiletorun) {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"/bin/csh", jobfile_dir + jobfiletorun};
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

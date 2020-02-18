import java.io.*;
import java.util.Map;

public class MCIP extends CAMxSIMULATION {

    private String jobfile_dir=PREPROCESSORS_DIR+"MCIP/";
    private String jobfile="RUN.mcip";
    private String module_dir=PREPROCESSORS_DIR+"MCIP/";
    private String work_dir=PREPROCESSORS_DIR+"MCIP/";
    private String input_dir="";
    private String output_dir="";

    private String RUNCITY ;

    CAMxSIMULATION caMxSIMULATION;

    public MCIP(CAMxSIMULATION caMxSIMULATION) {
        this.caMxSIMULATION= caMxSIMULATION;
        this.RUNCITY = caMxSIMULATION.getRUNCITY();

    }

    public void createJobFile(WRF wrf){

         String WRFOUTFILE = wrf.getNcfile();
         String MCIP_START = wrf.getSIMULATION_START_DATE()+".0000";
         MCIP_START=MCIP_START.replaceAll("\\s+","-");
         String MCIP_END =  wrf.getSIMULATION_END_DATE()+".0000";
         MCIP_END=MCIP_END.replaceAll("\\s+","-");
        int NCOLS = wrf.getGrid_size_x();
         int NROWS = wrf.getGrid_size_y();

        try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {

            // BuildMyString.com generated code. Please enjoy your string responsibly.

            String JOBFILE = "#!/bin/csh -f \n" +
                    "#------------------------------------------------------------------------------#\n" +
                    "#  The Community Multiscale Air Quality (CMAQ) system software is in           #\n" +
                    "#  continuous development by various groups and is based on information        #\n" +
                    "#  from these groups: Federal Government employees, contractors working        #\n" +
                    "#  within a United States Government contract, and non-Federal sources         #\n" +
                    "#  including research institutions.  These groups give the Government          #\n" +
                    "#  permission to use, prepare derivative works of, and distribute copies       #\n" +
                    "#  of their work in the CMAQ system to the public and to permit others         #\n" +
                    "#  to do so.  The United States Environmental Protection Agency                #\n" +
                    "#  therefore grants similar permission to use the CMAQ system software,        #\n" +
                    "#  but users are requested to provide copies of derivative works or            #\n" +
                    "#  products designed to operate in the CMAQ system to the United States        #\n" +
                    "#  Government without restrictions as to use by others.  Software              #\n" +
                    "#  that is used with the CMAQ system but distributed under the GNU             #\n" +
                    "#  General Public License or the GNU Lesser General Public License is          #\n" +
                    "#  subject to their copyright restrictions.                                    #\n" +
                    "#------------------------------------------------------------------------------#\n" +
                    "#=======================================================================\n" +
                    "#\n" +
                    "#  Script:  run.mcip\n" +
                    "#  Purpose: Runs Models-3/CMAQ Meteorology-Chemistry Interface\n" +
                    "#           Processor.  Part of the US EPA's Models-3/CMAQ system.\n" +
                    "#  Method:  In UNIX/Linux:  run.mcip >&! mcip.log\n" +
                    "#  Revised: 20 Sep 2001  Original version.  (T. Otte)\n" +
                    "#           18 Oct 2001  Added CoordName to user definitions.  Deleted\n" +
                    "#                        script variable DomIdMM5.  Added Fortran link\n" +
                    "#                        for GRIDDESC file.  Moved namelist output to\n" +
                    "#                        WorkDir, and mmheader output to OutDir.  Added\n" +
                    "#                        user variables I0, J0, NCOLS, and NROWS for\n" +
                    "#                        MCIP windowing.  (T. Otte)\n" +
                    "#           29 Jan 2002  Added new namelist for file names.  Generalized\n" +
                    "#                        the end-of-namelist delimiter.  (T. Otte)\n" +
                    "#           27 Feb 2002  Removed minimum size for windows.  (T. Otte)\n" +
                    "#           19 Mar 2002  Changed default grid cell for printing.\n" +
                    "#                        (T. Otte)\n" +
                    "#           11 Jun 2003  Clarified instructions on use of BTRIM and\n" +
                    "#                        setting I0 and J0 for windowing option.\n" +
                    "#                        Removed GRIDBDY2D, GRIDBDY3D, and METBDY2D\n" +
                    "#                        from output.  (T. Otte)\n" +
                    "#           01 Jul 2004  Restored GRIDBDY2D to output.  (T. Otte)\n" +
                    "#           29 Nov 2004  Added TERRAIN option for input to get\n" +
                    "#                        fractional land use from MM5 preprocessor.\n" +
                    "#                        (T. Otte)\n" +
                    "#           26 May 2005  Changed I0 and J0 to Y0 and X0 to make code\n" +
                    "#                        more general.  Removed \"_G1\" from environment\n" +
                    "#                        variables for output files.  Created two new\n" +
                    "#                        user options for calculating dry deposition\n" +
                    "#                        velocities.  Added capability to process more\n" +
                    "#                        than five input meteorology files in a single\n" +
                    "#                        MCIP run.  (T. Otte)\n" +
                    "#           27 Feb 2006  Updated automated namelist generator for\n" +
                    "#                        Linux on Mac (assumed to be) using the XLF\n" +
                    "#                        compiler.  (T. Otte)\n" +
                    "#           24 Jul 2007  Added option to bypass dry deposition velocity\n" +
                    "#                        calculations in MCIP so that they can be done\n" +
                    "#                        inline in the CCTM.  Eliminated options to\n" +
                    "#                        use RADM (Wesely) dry deposition, eliminated\n" +
                    "#                        multiple versions of M3Dry (Pleim) dry\n" +
                    "#                        deposition, and eliminated options and to\n" +
                    "#                        recalculate PBL and radiation fields in MCIP.\n" +
                    "#                        (T. Otte)\n" +
                    "#           27 May 2008  Added optional namelist variable to override\n" +
                    "#                        earth radius default from MM5 and WRF.  \n" +
                    "#                        (T. Otte)\n" +
                    "#                        Added variables to support GOES satellite\n" +
                    "#                        cloud processing (InSatDir, InSatFile, LSAT).\n" +
                    "#                        Requires additional data and preprocessing\n" +
                    "#                        package available from University of Alabama\n" +
                    "#                        at Huntsville.  Contributed by University of\n" +
                    "#                        Alabama at Huntsville.  (A. Biazar and T. Otte)\n" +
                    "#           23 Dec 2008  Added optional namelist variable to override\n" +
                    "#                        default setting for reference latitude for\n" +
                    "#                        WRF Lambert conformal projection.  (T. Otte)\n" +
                    "#           19 Mar 2010  Added namelist variable option to compute\n" +
                    "#                        and output potential vorticity.  Added namelist\n" +
                    "#                        variable option to output vertical velocity\n" +
                    "#                        predicted by meteorological model.  Allow\n" +
                    "#                        output from WRF Preprocessing System (WPS)\n" +
                    "#                        routine, GEOGRID, to provide fractional land\n" +
                    "#                        use output if it is unavailable in WRF output.\n" +
                    "#                        Add user option to output u- and v-component\n" +
                    "#                        winds on C-staggered grid.  (T. Otte)\n" +
                    "#           09 Sep 2010  Removed option to generate dry deposition\n" +
                    "#                        velocities in MCIP.  (T. Otte)\n" +
                    "#           07 Sep 2011  Corrected minor typos in error-checking (as\n" +
                    "#                        identified by Debra Baker, Univ. of Maryland).\n" +
                    "#                        Updated disclaimer.  (T. Otte)\n" +
                    "#           31 May 2012  Changed comment about MAX_MM to be consistent\n" +
                    "#                        with the change to the code.  (T. Otte)\n" +
                    "#=======================================================================\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Set identification for input and output files.\n" +
                    "#\n" +
                    "#   APPL       = Application Name (tag for MCIP output file names)\n" +
                    "#   CoordName  = Coordinate system name for GRIDDESC\n" +
                    "#   GridName   = Grid Name descriptor for GRIDDESC\n" +
                    "#   InMetDir   = Directory that contains input meteorology files\n" +
                    "#   InTerDir   = Directory that contains input MM5 \"TERRAIN\" file or\n" +
                    "#                WRF Preprocessing System \"GEOGRID\" file.  (Used for\n" +
                    "#                providing fractional land-use categories.  For MM5,\n" +
                    "#                it will only work if IEXTRA was set to TRUE in\n" +
                    "#                MM5's TERRAIN program.  Is TRUE for P-X simulations.\n" +
                    "#                Not needed for WRF if \"LANDUSEF\" is part of history\n" +
                    "#                file.)\n" +
                    "#   InSatDir   = Directory that contains GOES satellite files.  (Used\n" +
                    "#                with satellite processing from UAH; otherwise leave\n" +
                    "#                blank.)\n" +
                    "#   OutDir     = Directory to write MCIP output files\n" +
                    "#   ProgDir    = Directory that contains the MCIP executable\n" +
                    "#   WorkDir    = Working Directory for Fortran links and namelist\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "set APPL       = thess\n" +
                    "set CoordName  = LamCon_40N_97W    # 16-character maximum\n" +
                    "set GridName   = thess         # 16-character maximum\n" +
                    "set Expt = cmaqv51_benchmark\n" +
                    "set Test = v4-3\n" +
                    "set DataPath   = "+wrf.getDIR()+"\n" +
                    "set InMetDir   = "+wrf.getDIR()+"\n" +
                    "set InTerDir   = $InMetDir\n" +
                    "set InSatDir   = $$InMetDir\n" +
                    "set OutDir     = /opt/bin/camx_preprocessors/MCIP/output\n" +
                    "set ProgDir    = /opt/bin/camx_preprocessors/MCIP/src\n" +
                    "set WorkDir    = $OutDir\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Set name(s) of input meteorology file(s)\n" +
                    "#\n" +
                    "#   File name(s) must be set inside parentheses since \"InMetFiles\" is\n" +
                    "#   a C-shell script array.  Multiple file names should be space-\n" +
                    "#   delimited.  Additional lines can be used when separated by a\n" +
                    "#   back-slash (\\) continuation marker.  The file names can be as\n" +
                    "#   they appear on your system; MCIP will link the files in by a\n" +
                    "#   Fortran unit number and the explicit name via a namelist.  The\n" +
                    "#   files must be listed in chronological order.  The maximum number\n" +
                    "#   of input meteorology files must be less than or equal to the number\n" +
                    "#   in MAX_MM in file_mod.F (default is 367).\n" +
                    "#\n" +
                    "#   Example:\n" +
                    "#     set InMetFiles = ( $InMetDir/MMOUT_DOMAIN2.time1 \\\n" +
                    "#                        $InMetDir/MMOUT_DOMAIN2.time2 )\n" +
                    "#\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "set InMetFiles = ( "+WRFOUTFILE+" )\n" +
                    "set IfTer      = \"F\"\n" +
                    "set InTerFile  = $InTerDir/geo_em_d01.nc\n" +
                    "set InSatFiles = ( )\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Set user control options.\n" +
                    "#\n" +
                    "#   LPV:     0 = Do not compute and output potential vorticity\n" +
                    "#            1 = Compute and output potential vorticity\n" +
                    "#\n" +
                    "#   LWOUT:   0 = Do not output vertical velocity\n" +
                    "#            1 = Output vertical velocity\n" +
                    "#\n" +
                    "#   LUVCOUT: 0 = Do not output u- and v-component winds on C-grid\n" +
                    "#            1 = Output u- and v-component winds on C-grid\n" +
                    "#\n" +
                    "#   LSAT:    0 = No satellite input is available (default)\n" +
                    "#            1 = GOES observed cloud info replaces model-derived input\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "set LPV     = 0\n" +
                    "set LWOUT   = 0\n" +
                    "set LUVCOUT = 1\n" +
                    "set LSAT    = 0\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Set run start and end date.  (YYYY-MO-DD-HH:MI:SS.SSSS)\n" +
                    "#   MCIP_START:  First date and time to be output [UTC]\n" +
                    "#   MCIP_END:    Last date and time to be output  [UTC]\n" +
                    "#   INTVL:       Frequency of output [minutes]\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "set MCIP_START = "+MCIP_START+"  # [UTC]\n" +
                    "set MCIP_END   = "+MCIP_END+"  # [UTC]\n" +
                    "set INTVL      = 60 # [min]\n" +
                    "set BTRIM = 0\n" +
                    "set IOFORM = 1\n" +
                    "set LPV     = 0\n" +
                    "set LWOUT   = 0\n" +
                    "set LUVCOUT = 1\n" +
                    "set LSAT    = 0\n" +
                    "set LUVBOUT = 1\n" +
                    "set InGeoDir   = $DataPath/wrf\n" +
                    "set IfTer      = \"F\"\n" +
                    "set IfGeo      = \"F\"\n" +
                    "set InGeoFile  = $InGeoDir/geo_em_d01.nc\n" +
                    "set InTerFile  = $InTerDir/geo_em_d01.nc\n" +
                    "set InSatFiles = ( )\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Set CTM layers.  Should be in descending order starting at 1 and \n" +
                    "# ending with 0.  There is currently a maximum of 100 layers allowed.\n" +
                    "# To use all of the layers from the input meteorology without\n" +
                    "# collapsing (or explicitly specifying), set CTMLAYS = -1.0.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "set CTMLAYS = \"-1.0\"\n" +
                    "#set CTMLAYS = \"1.000, 0.995, 0.990, 0.980, 0.960, 0.940, 0.910, 0.860, \\\n" +
                    "#               0.800, 0.740, 0.650, 0.550, 0.400, 0.200, 0.000\"\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Determine whether or not static output (GRID) files will be created.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "set MKGRID = T\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Set number of meteorology \"boundary\" points to remove on each of four\n" +
                    "# horizontal sides of MCIP domain.  This affects the output MCIP domain\n" +
                    "# dimensions by reducing meteorology domain by 2*BTRIM + 2*NTHIK + 1,\n" +
                    "# where NTHIK is the lateral boundary thickness (in BDY files), and the\n" +
                    "# extra point reflects conversion from grid points (dot points) to grid\n" +
                    "# cells (cross points).  Setting BTRIM = 0 will use maximum of input\n" +
                    "# meteorology.  To remove MM5 lateral boundaries, set BTRIM = 5.\n" +
                    "#\n" +
                    "# *** If windowing a specific subset domain of input meteorology, set\n" +
                    "#     BTRIM = -1, and BTRIM will be ignored in favor of specific window\n" +
                    "#     information in X0, Y0, NCOLS, and NROWS.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "set BTRIM = 0\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Define MCIP subset domain.  (Only used if BTRIM = -1.  Otherwise,\n" +
                    "# the following variables will be set automatically from BTRIM and\n" +
                    "# size of input meteorology fields.)\n" +
                    "#   X0:     X-coordinate of lower-left corner of full MCIP \"X\" domain\n" +
                    "#           (including MCIP lateral boundary) based on input MM5 domain.\n" +
                    "#           X0 refers to the east-west dimension.  Minimum value is 1.\n" +
                    "#   Y0:     Y-coordinate of lower-left corner of full MCIP \"X\" domain\n" +
                    "#           (including MCIP lateral boundary) based on input MM5 domain.\n" +
                    "#           Y0 refers to the north-south dimension.  Minimum value is 1.\n" +
                    "#   NCOLS:  Number of columns in output MCIP domain (excluding MCIP\n" +
                    "#           lateral boundaries).\n" +
                    "#   NROWS:  Number of rows in output MCIP domain (excluding MCIP\n" +
                    "#           lateral boundaries).\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "set X0    =  1\n" +
                    "set Y0    =  1\n" +
                    "set NCOLS =  42\n" +
                    "set NROWS =  42\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Set coordinates for cell for diagnostic prints on output domain.\n" +
                    "# If coordinate is set to 0, domain center cell will be used.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "set LPRT_COL = 0\n" +
                    "set LPRT_ROW = 0\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Optional:  Set WRF Lambert conformal reference latitude.\n" +
                    "#            (Handy for matching WRF grids to existing MM5 grids.)\n" +
                    "#            If not set, MCIP will use average of two true latitudes.\n" +
                    "# To \"unset\" this variable, set the script variable to \"-999.0\".\n" +
                    "# Alternatively, if the script variable is removed here, remove it\n" +
                    "# from the setting of the namelist (toward the end of the script).\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "set WRF_LC_REF_LAT = 52.0\n" +
                    "#=======================================================================\n" +
                    "#=======================================================================\n" +
                    "# Set up and run MCIP.\n" +
                    "#   Should not need to change anything below here.\n" +
                    "#=======================================================================\n" +
                    "#=======================================================================\n" +
                    "set PROG = mcip\n" +
                    "date\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Make sure directories exist.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "if ( ! -d $InMetDir ) then\n" +
                    "  echo \"No such input directory $InMetDir\"\n" +
                    "  exit 1\n" +
                    "endif\n" +
                    "if ( ! -d $OutDir ) then\n" +
                    "  echo \"No such output directory...will try to create one\"\n" +
                    "  mkdir -p $OutDir\n" +
                    "  if ( $status != 0 ) then\n" +
                    "    echo \"Failed to make output directory, $OutDir\"\n" +
                    "    exit 1\n" +
                    "  endif\n" +
                    "endif\n" +
                    "if ( ! -d $ProgDir ) then\n" +
                    "  echo \"No such program directory $ProgDir\"\n" +
                    "  exit 1\n" +
                    "endif\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Make sure the input files exist.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "if ( $IfGeo == \"T\" ) then\n" +
                    "  if ( ! -f $InGeoFile ) then\n" +
                    "    echo \"No such input file $InGeoFile\"\n" +
                    "    exit 1\n" +
                    "  endif\n" +
                    "endif\n" +
                    "foreach fil ( $InMetFiles )\n" +
                    "  if ( ! -f $fil ) then\n" +
                    "    echo \"No such input file $fil\"\n" +
                    "    exit 1\n" +
                    "  endif\n" +
                    "end\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Make sure the executable exists.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "if ( ! -f $ProgDir/${PROG}.exe ) then\n" +
                    "  echo \"Could not find ${PROG}.exe\"\n" +
                    "  exit 1\n" +
                    "endif\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Create a work directory for this job.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "if ( ! -d $WorkDir ) then\n" +
                    "  mkdir -p $WorkDir\n" +
                    "  if ( $status != 0 ) then\n" +
                    "    echo \"Failed to make work directory, $WorkDir\"\n" +
                    "    exit 1\n" +
                    "  endif\n" +
                    "endif\n" +
                    "cd $WorkDir\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Set up script variables for input files.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "if ( $IfGeo == \"T\" ) then\n" +
                    "  if ( -f $InGeoFile ) then\n" +
                    "    set InGeo = $InGeoFile\n" +
                    "  else\n" +
                    "    set InGeo = \"no_file\"\n" +
                    "  endif\n" +
                    "else\n" +
                    "  set InGeo = \"no_file\"\n" +
                    "endif\n" +
                    "set FILE_GD  = $OutDir/GRIDDESC\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Create namelist with user definitions.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "set MACHTYPE = `uname`\n" +
                    "if ( ( $MACHTYPE == \"AIX\" ) || ( $MACHTYPE == \"Darwin\" ) ) then\n" +
                    "  set Marker = \"/\"\n" +
                    "else\n" +
                    "  set Marker = \"&END\"\n" +
                    "endif\n" +
                    "cat > $WorkDir/namelist.${PROG} << !\n" +
                    " &FILENAMES\n" +
                    "  file_gd    = \"$FILE_GD\"\n" +
                    "  file_mm    = \"$InMetFiles[1]\",\n" +
                    "!\n" +
                    "if ( $#InMetFiles > 1 ) then\n" +
                    "  @ nn = 2\n" +
                    "  while ( $nn <= $#InMetFiles )\n" +
                    "    cat >> $WorkDir/namelist.${PROG} << !\n" +
                    "               \"$InMetFiles[$nn]\",\n" +
                    "!\n" +
                    "    @ nn ++\n" +
                    "  end\n" +
                    "endif\n" +
                    "if ( $IfGeo == \"T\" ) then\n" +
                    "cat >> $WorkDir/namelist.${PROG} << !\n" +
                    "  file_geo   = \"$InGeo\"\n" +
                    "!\n" +
                    "endif\n" +
                    "cat >> $WorkDir/namelist.${PROG} << !\n" +
                    "  ioform     =  $IOFORM\n" +
                    " $Marker\n" +
                    " &USERDEFS\n" +
                    "  lpv        =  $LPV\n" +
                    "  lwout      =  $LWOUT\n" +
                    "  luvbout    =  $LUVBOUT\n" +
                    "  mcip_start = \"$MCIP_START\"\n" +
                    "  mcip_end   = \"$MCIP_END\"\n" +
                    "  intvl      =  $INTVL\n" +
                    "  coordnam   = \"$CoordName\"\n" +
                    "  grdnam     = \"$GridName\"\n" +
                    "  btrim      =  $BTRIM\n" +
                    "  lprt_col   =  $LPRT_COL\n" +
                    "  lprt_row   =  $LPRT_ROW\n" +
                    "  wrf_lc_ref_lat = $WRF_LC_REF_LAT\n" +
                    " $Marker\n" +
                    " &WINDOWDEFS\n" +
                    "  x0         =  $X0\n" +
                    "  y0         =  $Y0\n" +
                    "  ncolsin    =  $NCOLS\n" +
                    "  nrowsin    =  $NROWS\n" +
                    " $Marker\n" +
                    "!\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Set links to FORTRAN units.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "rm fort.*\n" +
                    "if ( -f $FILE_GD ) rm -f $FILE_GD\n" +
                    "ln -s $FILE_GD                   fort.4\n" +
                    "ln -s $WorkDir/namelist.${PROG}  fort.8\n" +
                    "set NUMFIL = 0\n" +
                    "foreach fil ( $InMetFiles )\n" +
                    "  @ NN = $NUMFIL + 10\n" +
                    "  ln -s $fil fort.$NN\n" +
                    "  @ NUMFIL ++\n" +
                    "end\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Set output file names and other miscellaneous environment variables.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "setenv IOAPI_CHECK_HEADERS  T\n" +
                    "setenv EXECUTION_ID         $PROG\n" +
                    "setenv GRID_BDY_2D          $OutDir/GRIDBDY2D_${APPL}\n" +
                    "setenv GRID_CRO_2D          $OutDir/GRIDCRO2D_${APPL}\n" +
                    "setenv GRID_DOT_2D          $OutDir/GRIDDOT2D_${APPL}\n" +
                    "setenv MET_BDY_3D           $OutDir/METBDY3D_${APPL}\n" +
                    "setenv MET_CRO_2D           $OutDir/METCRO2D_${APPL}\n" +
                    "setenv MET_CRO_3D           $OutDir/METCRO3D_${APPL}\n" +
                    "setenv MET_DOT_3D           $OutDir/METDOT3D_${APPL}\n" +
                    "setenv LUFRAC_CRO           $OutDir/LUFRAC_CRO_${APPL}\n" +
                    "setenv SOI_CRO              $OutDir/SOI_CRO_${APPL}\n" +
                    "setenv MOSAIC_CRO           $OutDir/MOSAIC_CRO_${APPL}\n" +
                    "if ( -f $GRID_BDY_2D ) rm -f $GRID_BDY_2D\n" +
                    "if ( -f $GRID_CRO_2D ) rm -f $GRID_CRO_2D\n" +
                    "if ( -f $GRID_DOT_2D ) rm -f $GRID_DOT_2D\n" +
                    "if ( -f $MET_BDY_3D  ) rm -f $MET_BDY_3D\n" +
                    "if ( -f $MET_CRO_2D  ) rm -f $MET_CRO_2D\n" +
                    "if ( -f $MET_CRO_3D  ) rm -f $MET_CRO_3D\n" +
                    "if ( -f $MET_DOT_3D  ) rm -f $MET_DOT_3D\n" +
                    "if ( -f $LUFRAC_CRO  ) rm -f $LUFRAC_CRO\n" +
                    "if ( -f $SOI_CRO     ) rm -f $SOI_CRO\n" +
                    "if ( -f $MOSAIC_CRO  ) rm -f $MOSAIC_CRO\n" +
                    "if ( -f $OutDir/mcip.nc      ) rm -f $OutDir/mcip.nc\n" +
                    "if ( -f $OutDir/mcip_bdy.nc  ) rm -f $OutDir/mcip_bdy.nc\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "# Execute MCIP.\n" +
                    "#-----------------------------------------------------------------------\n" +
                    "$ProgDir/${PROG}.exe\n" +
                    "if ( $status == 0 ) then\n" +
                    "  rm fort.*\n" +
                    "  exit 0\n" +
                    "else\n" +
                    "  echo \"Error running $PROG\"\n" +
                    "  exit 1\n" +
                    "endif\n";


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

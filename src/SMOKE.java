import java.io.*;
import java.util.Map;
import java.io.File;
import java.io.IOException;


public class SMOKE extends CAMxSIMULATION {

    private String jobfile_dir="";
    private String jobfile="SMOKE.run";
    private String module_dir=PREPROCESSORS_DIR+"SMOKE_4.5";
    private String work_dir="";
    private String input_dir="";
    private String output_dir=module_dir+"/data/run_vasilis/output/merge";

    CAMxSIMULATION caMxSIMULATION;


    public SMOKE(CAMxSIMULATION caMxSIMULATION) {
        this.caMxSIMULATION=caMxSIMULATION;
    }

    public void copyEmissInv(String emissinvdir,String smokeinvdir) throws IOException {
        File sourceFolder = new File(emissinvdir);
        File destinationFolder = new File(smokeinvdir);
        //EmissInvCopy.copyFolder(sourceFolder, destinationFolder);
        EmissInvCopy.copyFile(sourceFolder,destinationFolder);
    }


    public void createJobFile(WRF wrf){
        try {
            copyEmissInv(caMxSIMULATION.EMISION_INV,PREPROCESSORS_DIR+"SMOKE_4.5/data/inventory/vasilis/point/EmissionInventorySmoke.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter out = new PrintWriter("/opt/bin/camx_preprocessors/SMOKE_4.5/subsys/smoke/assigns/smkrun.assigns")) {
            String JOBFILE = "#/bin/csh -fx\n\n" +
                    "#\n\n" +
                    "## HEADER ##################################################################\n" +
                    "#\n" +
                    "#  SMOKE ASSIGNS file (sets up for area, biogenic, mobile, nonroad, and point sources)\n" +
                    "#\n" +
                    "#  Version @(#)$Id$\n" +
                    "#  Path    $Source$\n" +
                    "#  Date    $Date$\n" +
                    "#\n" +
                    "#  Scenario Description:\n" +
                    "#     This assigns file sets up the environment variables for use of \n" +
                    "#     SMOKE Version 2.1 outside of the Models-3 system.\n" +
                    "#\n" +
                    "#  Usage:\n" +
                    "#     source <Assign>\n" +
                    "#     Note: First set the SMK_HOME env variable to the base SMOKE installation\n" +
                    "#\n" +
                    "## END HEADER ##############################################################\n" +
                    "## Determine operating system for compiling SMOKE\n" +
                    "#  Set executable format, makeing allownces for 64-bit archtecnture\n" +
                    "#  example: Linux2_x86_64ifort (Intel), Linux2_x86_64pg (Portland group), Linux2_x86_64gfort (gfortran)...\n" +
                    "#\n" +
                    "   setenv BIN    Linux2_x86_64ifort    # Intel Fortran 64-bit compiler\n" +
                    "## I/O Naming roots\n" +
                    "#\n" +
                    "   setenv INVID     vasilis      # Inventory input identifier\n" +
                    "   setenv INVOP     vasilis        # Base year inventory output name\n" +
                    "   setenv INVEN     vasilis        # Base year inventory name with version\n" +
                    "   setenv ABASE     vasilis        # Area base case output name\n" +
                    "   setenv BBASE     vasilis       # Biogenics base case output name\n" +
                    "   setenv MBASE     vasilis        # Mobile base case output name\n" +
                    "   setenv PBASE     vasilis        # Point base case output name\n" +
                    "   setenv EBASE    vasilis        # Output merged base case name\n" +
                    "   setenv METSCEN      trial             # Met scenario name\n" +
                    "   setenv GRID         thess          # Gridding root for naming\n" +
                    "   setenv IOAPI_GRIDNAME_1 thess # Grid selected from GRIDDESC file\n" +
                    "   setenv IOAPI_ISPH   20               # Specifies spheroid type associated with grid\n" +
                    "   setenv SPC          cmaq_cb05_soa     # Speciation type\n" +
                    "## Mobile episode variables\n" +
                    "   setenv EPI_STDATE "+wrf.getStartJulianDate()+"     # Julian start date\n" +
                    "   setenv EPI_STTIME  000000     # start time (HHMMSS)\n" +
                    "   setenv EPI_RUNLEN "+wrf.EPI_RUNLEN()+"     # run length (HHHMMSS)\n" +
                    "   setenv EPI_NDAY   "+wrf.getSimulationDays()+"           # number of full run days\n" +
                    "## Per-period environment variables\n" +
                    "   setenv G_STDATE  "+wrf.getStartJulianDate()+"       # Julian start date\n" +
                    "   setenv G_STTIME   000000      # start time (HHMMSS)\n" +
                    "   setenv G_TSTEP     10000      # time step  (HHMMSS)\n" +
                    "   setenv G_RUNLEN   "+wrf.G_RUNLEN()+"      # run length (HHMMSS)\n" +
                    "   setenv ESDATE   "+wrf.getSIMULATION_START_DATE_formatyyyy(0)+"       # Start date of emis time-based files/dirs\n" +
                    "   setenv MSDATE   "+wrf.getSIMULATION_START_DATE_formatyyyy(0)+"       # Start date of met  time-based files\n" +
                    "   setenv NDAYS    "+wrf.getSimulationDays()+"       # Duration in days of each emissions file\n" +
                    "   setenv MDAYS   "+wrf.getSimulationDays()+"       # Duration in days of met  time-based files\n" +
                    "   setenv YEAR    "+wrf.getInvYear()+"       # Base year for year-specific files\n" +
                    "## Reset days if overrides are available\n" +
                    "   if ( $?G_STDATE_ADVANCE ) then\n" +
                    "       set date = $G_STDATE\n" +
                    "       @ date = $date + $G_STDATE_ADVANCE\n" +
                    "       setenv G_STDATE $date \n" +
                    "# org       setenv ESDATE `$IOAPIDIR/datshift $G_STDATE 0`\n" +
                    "       setenv ESDATE `$IOAPIDIR/jul2greg $G_STDATE`\n" +
                    "   endif\n" +
                    "## User-defined I/O directory settings\n" +
                    "   setenv SMK_SUBSYS  $SMK_HOME/subsys              # SMOKE subsystem dir\n" +
                    "   setenv SMKROOT     $SMK_SUBSYS/smoke             # System root dir\n" +
                    "   setenv SMKDAT      $SMK_HOME/data                # Data root dir\n" +
                    "   setenv ASSIGNS     $SMKROOT/assigns              # smoke assigns files\n" +
                    "#\n" +
                    "## Override speciation setting, if override variable is set\n" +
                    "if ( $?SPC_OVERRIDE ) then\n" +
                    "   if ( $SPC != \" \" ) then\n" +
                    "      setenv SPC $SPC_OVERRIDE\n" +
                    "   endif\n" +
                    "endif\n" +
                    "#\n" +
                    "## Override year setting, if override variable is set\n" +
                    "if ( $?YEAR_OVERRIDE ) then\n" +
                    "   setenv YEAR $YEAR_OVERRIDE\n" +
                    "endif\n" +
                    "#\n" +
                    "## Make changes to names for future year and/or control strategy\n" +
                    "set outstat = 0\n" +
                    "source $ASSIGNS/set_case.scr\n" +
                    "if ( $status > 0 ) then\n" +
                    "   set outstat = 1\n" +
                    "endif\n" +
                    "## Set dependent directory names\n" +
                    "#\n" +
                    "source $ASSIGNS/set_dirs_october.scr\n" +
                    "## Check script settings\n" +
                    "source $ASSIGNS/check_settings.scr\n" +
                    "##########  SMOKE formatted raw inputs #############\n" +
                    "#\n" +
                    "## Area-source input files\n" +
                    "if ( $SMK_SOURCE == 'A' ) then\n" +
                    "   setenv ARINV     $INVDIR/$SRCABBR/arinv.$SRCABBR.lst          # Stationary area emission inventory\n" +
                    "   setenv ARDAY     $INVDIR/$SRCABBR/arday.$SRCABBR.lst          # Stationary FF10 daily area emission inventory\n" +
                    "   setenv ARHOUR    $INVDIR/$SRCABBR/arhour.$SRCABBR.lst         # Stationary FF10 hourly area emission inventory\n" +
                    "   setenv REPCONFIG $INVDIR/$SRCABBR/REPCONFIG.area.txt          # Default report configurations\n" +
                    "   setenv ARTOPNT   $INVDIR/other/ar2pt_14OCT03_1999.txt         # area-to-point assignments\n" +
                    "   setenv AGREF     $GE_DAT/amgref_us_051704.txt                 # Area gridding x-ref \n" +
                    "   setenv ATPRO_MONTHLY   $GE_DAT/amptpro_monthly.csv            # Monthly Temporal profiles\n" +
                    "   setenv ATPRO_WEEKLY    $GE_DAT/amptpro_weekly.csv             # Weekly Temporal profiles\n" +
                    "   setenv ATPRO_HOURLY    $GE_DAT/amptpro_hourly.csv             # Hourly Temporal profiles\n" +
                    "   setenv ATREF     $GE_DAT/amptref_tref.csv                   # Temporal x-ref\n" +
                    "   setenv GSREF     $GE_DAT/gsref.$SPC.txt                       # Speciation x-ref\n" +
                    "   setenv GSPRO     $GE_DAT/gspro.$SPC.txt                       # Speciation profiles\n" +
                    "   setenv GSPRO_COMBO $GE_DAT/gspro_combo.$SPC.txt               # Speciation Combo prof\n" +
                    "endif\n" +
                    "#\n" +
                    "## Biogenic input files\n" +
                    "if ( $SMK_SOURCE == 'B' ) then\n" +
                    "   setenv B3FAC     $GE_DAT/b3fac.beis3_efac_v0.98.txt                       # BEIS3.14 emission factors input file\n" +
                    "   setenv B360FAC   $GE_DAT/b360fac_beld4_csv_nlcd2006.txt                   # BEIS3.6 emission factors input file\n" +
                    "   setenv BELD4     $INVDIR/biog/beld4.${IOAPI_GRIDNAME_1}.ncf               # BEIS3.6 BELD4 land-use input file\n" +
                    "   setenv BELD3_TOT $INVDIR/biog/beld3.${IOAPI_GRIDNAME_1}.output_tot.ncf    # BELD3 for BEIS3.14\n" +
                    "   setenv BELD3_A   $INVDIR/biog/beld3.${IOAPI_GRIDNAME_1}.output_a.ncf      # BELD3 for BEIS3.14\n" +
                    "   setenv BELD3_B   $INVDIR/biog/beld3.${IOAPI_GRIDNAME_1}.output_b.ncf      # BELD3 for BEIS3.14\n" +
                    "   setenv SOILINP   $STATIC/soil.beis312.$GRID.$SPC.ncf                      # NO soil input file\n" +
                    "   setenv GSPRO     $GE_DAT/gspro.$SPC.txt                                   # Speciation profiles\n" +
                    "endif\n" +
                    "if ( $SMK_SOURCE == 'B' || $MRG_BIOG == 'Y' ) then\n" +
                    "   setenv BGPRO   $GE_DAT/bgpro.12km_041604.nc.txt               # Biogenic gridding surrogates\n" +
                    "endif\n" +
                    "#\n" +
                    "## Mobile source input files\n" +
                    "if ( $SMK_SOURCE == 'M' ) then\n" +
                    "   setenv MBINV       $INVDIR/$SRCABBR/mbinv.$SRCABBR.lst        # mobile emissions\n" +
                    "   setenv SPDREF      $INVDIR/$SRCABBR/spdref.MOVES.txt          # Speed cross-reference file\n" +
                    "   setenv SPDPRO      $INVDIR/$SRCABBR/spdpro.MOVES.txt          # Speed profiles file\n" +
                    "   setenv SCCXREF     $GE_DAT/SCCXREF_$SRCABBR.csv               # SCC8-SCC10 mapping input file\n" +
                    "   setenv MCXREF      $GE_DAT/mcxref_nc.txt                      # ref county cross-reference\n" +
                    "   setenv MFMREF      $GE_DAT/mfmref_nc.txt                      # ref county fuel-month\n" +
                    "   setenv METLIST     $GE_DAT/metlist.met4moves.lst              # List of Meteorology files\n" +
                    "   setenv MGREF       $GE_DAT/mgref_nc_moves2014.txt             # Mobile gridding x-ref\n" +
                    "   setenv MTPRO_MONTHLY   $GE_DAT/amptpro_monthly.csv            # Monthly Temporal profiles\n" +
                    "   setenv MTPRO_WEEKLY    $GE_DAT/amptpro_weekly.csv             # Weekly Temporal profiles\n" +
                    "   setenv MTPRO_HOURLY    $GE_DAT/amptpro_hourly.csv             # Hourly Temporal profiles\n" +
                    "   setenv MTREF       $GE_DAT/amptref_tref.csv                   # Temporal x-ref\n" +
                    "   setenv GSREF       $GE_DAT/gsref.$SPC.moves2014.txt               # Speciation x-ref\n" +
                    "   setenv GSPRO       $GE_DAT/gspro.$SPC.moves2014.txt               # Speciation profiles\n" +
                    "   setenv GSPRO_COMBO $GE_DAT/gspro_combo.$SPC.moves.txt         # Speciation Combo prof\n" +
                    "   setenv MEPROC      $GE_DAT/meproc.$SRCABBR.txt                # Mobile emission processes\n" +
                    "   setenv SMK_MVSPATH $GE_DAT/MOVES_lookuptables/                # Dir for MOVES lookup tab\n" +
                    "   setenv MRCLIST     $SMK_MVSPATH/mrclist.${SRCABBR}.lst # List of MOVES lookup table list file\n" +
                    "   setenv METMOVES    $SMKDAT/met/run_$METSCEN/SMOKE_DAILY_${IOAPI_GRIDNAME_1}.ncf  # Met4moves output\n" +
                    "   setenv CFPRO       $GE_DAT/cfpro_moves_test.txt\n" +
                    "endif\n" +
                    "#\n" +
                    "## Point source input files\n" +
                    "if ( $SMK_SOURCE == 'P' ) then\n" +
                    "   setenv PTINV       $INVDIR/$SRCABBR/ptinv.$SRCABBR.lst        # EMS-95 point emissions\n" +
                    "   setenv PTDAY       $INVDIR/$SRCABBR/ptday.$SRCABBR.lst        # daily point emis\n" +
                    "   setenv PTHOUR      $INVDIR/$SRCABBR/pthour.$SRCABBR.lst       # hourly point emis\n" +
                    "   setenv PELVCONFIG  $INVDIR/$SRCABBR/pelvconfig.inline.txt     # elevated source selection\n" +
                    "   setenv REPCONFIG   $INVDIR/$SRCABBR/repconfig.point.txt       # Default report configurations\n" +
                    "   #      PTMPLIST                                               # Set automatically by script\n" +
                    "   setenv PTPRO_MONTHLY   $GE_DAT/amptpro_monthly.csv            # Monthly Temporal profiles\n" +
                    "   setenv PTPRO_WEEKLY    $GE_DAT/amptpro_weekly.csv             # Weekly Temporal profiles\n" +
                    "   setenv PTPRO_HOURLY    $GE_DAT/amptpro_hourly.csv             # Hourly Temporal profiles\n" +
                    "   setenv PTREF       $GE_DAT/amptref_tref.csv                   # Temporal x-ref\n" +
                    "   setenv PSTK        $GE_DAT/pstk.m3.txt                        # Replacement stack params\n" +
                    "   setenv GSPRO       $GE_DAT/gspro.$SPC.txt                     # Speciation profiles\n" +
                    "   setenv GSREF       $GE_DAT/gsref.$SPC.txt                     # Speciation x-ref\n" +
                    "   setenv GSPRO_COMBO $GE_DAT/gspro_combo.$SPC.txt               # Speciation Combo prof\n" +
                    "endif\n" +
                    "#\n" +
                    "## Shared input files\n" +
                    "   setenv GEOCODE_LEVEL1   $GE_DAT/geocode1_edgar.txt\n" +
                    "   setenv GEOCODE_LEVEL2   $GE_DAT/geocode2_edgar.txt\n" +
                    "   setenv GEOCODE_LEVEL3   $GE_DAT/geocode3_edgar.txt\n" +
                    "   setenv GEOCODE_LEVEL4   $GE_DAT/geocode4_edgar.txt\n" +
                    "   setenv INVTABLE    $INVDIR/other/invtable_hapcap_cb05soa.txt  # Inventory table\n" +
                    "   setenv GRIDDESC    "+GRIDDESCfile+"                   # Grid descriptions.\n" +
                    "   setenv COSTCY      $GE_DAT/costcy.txt                         # country/state/county info\n" +
                    "   setenv HOLIDAYS    $GE_DAT/holidays_Greek.txt                 # holidays for day change\n" +
                    "   setenv SCCDESC     $GE_DAT/scc_desc.txt                       # SCC descriptions\n" +
                    "   setenv SICDESC     $GE_DAT/sic_desc.txt                       # SIC descriptions\n" +
                    "   setenv SRGDESC     $GE_DAT/SRGDESC                            # surrogate descriptions\n" +
                    "   setenv SRGPRO_PATH $GE_DAT/Surrogates_12km/                   # surrogate files path\n" +
                    "   setenv ORISDESC    $GE_DAT/oris_info.txt                      # ORIS ID descriptions\n" +
                    "   setenv MACTDESC    $GE_DAT/mact_desc.txt                      # MACT descriptions\n" +
                    "   setenv NAICSDESC   $GE_DAT/naics_desc.txt                     # NAICS descriptions\n" +
                    "   setenv GSCNV       $GE_DAT/gscnv.$SPC.txt                     # ROG to TOG conversion facs\n" +
                    "#   setenv PROCDATES   $GE_DAT/procdates.txt                      # time periods that Temporal should process\n" +
                    "   setenv SOURCE_GROUPS $INVDIR/other/source_groups.nc.txt       # Source apportionment groups\n" +
                    "   setenv GRIDMASK   /nas01/depts/ie/cempd/EMAQ/Platform/GriddedEI/GEOCODE_EDGAR0.1.ncf\n" +
                    "#\n" +
                    "## Override shared inputs\n" +
                    "if ( $?INVTABLE_OVERRIDE ) then\n" +
                    "   if ( $INVTABLE_OVERRIDE != \" \" ) then\n" +
                    "      setenv INVTABLE $INVDIR/other/$INVTABLE_OVERRIDE\n" +
                    "   endif\n" +
                    "endif\n" +
                    "#\n" +
                    "## Miscellaeous input files\n" +
                    "   if ( $RUN_MRGGRID == Y ) then\n" +
                    "      setenv FILELIST   $INVDIR/other/filelist.mrggrid.txt\n" +
                    "   endif\n" +
                    "   if ( $RUN_GEOFAC == Y ) then\n" +
                    "      setenv AGTS     $OUTPUT/no_file_set\n" +
                    "      setenv GEOMASK  $INVDIR/other/no_file_set\n" +
                    "      setenv SPECFACS $INVDIR/other/no_file_set\n" +
                    "      setenv AGTSFAC  $INVDIR/other/no_file_set\n" +
                    "   endif\n" +
                    "   if ( $RUN_PKTREDUC == Y ) then\n" +
                    "      setenv GCNTL_OUT $INVDIR/no_file_set   # \n" +
                    "   endif\n" +
                    "   if ( $RUN_SMK2EMIS == Y ) then\n" +
                    "      setenv VNAMMAP  $GE_DAT/VNAMMAP.dat\n" +
                    "      setenv UAM_AGTS $OUTPUT/uam_agts_l.$ESDATE.$NDAYS.$GRID.$ASCEN.ncf\n" +
                    "      setenv UAM_BGTS $OUTPUT/uam_bgts_l.$ESDATE.$NDAYS.$GRID.$BSCEN.ncf\n" +
                    "      setenv UAM_MGTS $OUTPUT/uam_mgts_l.$ESDATE.$NDAYS.$GRID.$MSCEN.ncf\n" +
                    "      setenv UAM_PGTS $OUTPUT/uam_pgts_l.$ESDATE.$NDAYS.$GRID.$PSCEN.ncf\n" +
                    "      setenv UAM_EGTS $OUTPUT/uam_egts_l.$ESDATE.$NDAYS.$GRID.$ESCEN.ncf\n" +
                    "   endif\n" +
                    "   if( $RUN_UAMEMIS == Y ) then\n" +
                    "      setenv UAMEMIS $OUTPUT/no_file_set\n" +
                    "      setenv E2DNCF  $OUTPUT/e2dncf.ncf\n" +
                    "   endif\n" +
                    "## Meteorology IO/API input files (MCIP output files)\n" +
                    "#\n" +
                    "   setenv METDAT "+MCIPoutputdir+"\n" +
                    "      setenv GRID_CRO_2D $METDAT/GRIDCRO2D_thess\n" +
                    "      setenv MET_CRO_2D  $METDAT/METCRO2D_thess\n" +
                    "      setenv MET_DOT_3D  $METDAT/METDOT3D_thess\n" +
                    "      setenv MET_FILE1   $MET_CRO_2D\n" +
                    "      setenv MET_FILE2   $MET_CRO_2D\n" +
                    "      setenv GRID_CRO_3D $METDAT/GRIDCRO3D_thess # no longer needed due to inline option in CMAQ \n" +
                    "      setenv MET_CRO_3D  $METDAT/METCRO3D_thess # no longer needed due to inline option in CMAQ \n" +
                    "#\n" +
                    "##########################################################################\n" +
                    "#\n" +
                    "## Output and Intermediate files\n" +
                    "#\n" +
                    "## Area source intermediate and output files\n" +
                    "#\n" +
                    "if ( $SMK_SOURCE == 'A' ) then\n" +
                    "   setenv ADAY     $INVOPD/aday.$SRCABBR.$INVOP.ncf       # Area NetCDF day-specific\n" +
                    "   setenv ASCC     $INVOPD/ASCC.$SRCABBR.$FYIOP.txt\n" +
                    "   setenv REPINVEN $REPSTAT/repinven.$SRCABBR.$INVOP.txt\n" +
                    "   setenv ATSUPNAME $SMKDAT/run_${ASCEN}/scenario/atsup.$SRCABBR.$ASCEN.\n" +
                    "   setenv ATSUP    $ATSUPNAME$G_STDATE.txt\n" +
                    "   setenv ASSUP    $SMKDAT/run_$ABASE/static/assup.$SRCABBR.$SPC.$ABASE.txt\n" +
                    "   setenv AGSUP    $SMKDAT/run_$ABASE/static/agsup.$SRCABBR.$GRID.$ABASE.txt\n" +
                    "   setenv ACREP    $REPSTAT/acrep.$SRCABBR.$ASCEN.rpt           \n" +
                    "   setenv APROJREP $REPSTAT/aprojrep.$SRCABBR.$ASCEN.rpt\n" +
                    "   setenv AREACREP $REPSTAT/areacrep.$SRCABBR.$ASCEN.rpt\n" +
                    "   setenv ACSUMREP $REPSTAT/acsumrep.$SRCABBR.$ASCEN.rpt\n" +
                    "   setenv ACTLWARN $REPSTAT/actlwarn.$SRCABBR.$ASCEN.txt\n" +
                    "   setenv AREA_O   $INVOPD/area.$SRCABBR.map.$FYINV.txt\n" +
                    "   setenv ARINV_O  $ARDAT/arinv_o.$SRCABBR.$FYINV.orl.txt\n" +
                    "endif\n" +
                    "if ( $SMK_SOURCE == A || $RUN_SMKMERGE == Y && $MRG_AREA == Y ) then\n" +
                    "   setenv AREA     $INVOPD/area.map.$SRCABBR.$FYIOP.txt   # Area inventory map\n" +
                    "   setenv ATMPNAME $SMKDAT/run_$ASCEN/scenario/atmp.$SRCABBR.$ASCEN.\n" +
                    "   setenv ATMP     $ATMPNAME$G_STDATE.ncf\n" +
                    "   setenv ASMAT_S  $SMKDAT/run_$ABASE/static/asmat_s.$SRCABBR.$SPC.$ABASE.ncf\n" +
                    "   setenv ASMAT_L  $SMKDAT/run_$ABASE/static/asmat_l.$SRCABBR.$SPC.$ABASE.ncf\n" +
                    "   setenv ARMAT_L  $SMKDAT/run_$ASCEN/static/armat_l.$SRCABBR.$SPC.$ASCEN.ncf\n" +
                    "   setenv ARMAT_S  $SMKDAT/run_$ASCEN/static/armat_s.$SRCABBR.$SPC.$ASCEN.ncf\n" +
                    "   setenv ARSUP    $SMKDAT/run_$ASCEN/static/arsup.$SRCABBR.$ASCEN.txt\n" +
                    "   setenv ACMAT    $SMKDAT/run_$ASCEN/static/acmat.$SRCABBR.$ASCEN.ncf          \n" +
                    "   setenv AGMAT    $SMKDAT/run_$ABASE/static/agmat.$SRCABBR.$GRID.$ABASE.ncf\n" +
                    "   setenv APMAT    $SMKDAT/run_$ASCEN/static/apmat.$SRCABBR.$ASCEN.ncf\n" +
                    "   setenv SRCGROUPS_OUT $OUTPUT/source_groups_out.$SRCABBR.$GRID.$ASCEN.ncf\n" +
                    "   setenv SRCGRP_REPORT $REPSTAT/srcgroups.$SRCABBR.$ASCEN.txt\n" +
                    "endif\n" +
                    "## Biogenic source intermediate and output files\n" +
                    "#\n" +
                    "if ( $SMK_SOURCE == 'B' ) then\n" +
                    "   setenv BGRD      $INVOPD/bgrd.summer.$GRID.$BSCEN.ncf  # Summer/default normalized bio emis\n" +
                    "   setenv BGRDW     $INVOPD/bgrd.winter.$GRID.$BSCEN.ncf  # Winter grd normalized bio emis\n" +
                    "   setenv BIOSEASON $GE_DAT/bioseason.$YEAR.us36.ncf\n" +
                    "   setenv B3GRD     $INVOPD/b3grd.$GRID.$BSCEN.ncf\n" +
                    "   setenv SOILOUT   $STATIC/soil.beis312.$GRID.$SPC.ncf  # NO soil output file\n" +
                    "endif\n" +
                    "if ( $SMK_SOURCE == 'B' || $MRG_BIOG == 'Y' ) then\n" +
                    "   setenv BGTS_L    $B_OUT/b3gts_l.$ESDATE.$NDAYS.$GRID.$BSCEN.ncf\n" +
                    "   setenv BGTS_S    $B_OUT/b3gts_s.$ESDATE.$NDAYS.$GRID.$BSCEN.ncf\n" +
                    "   setenv B3GTS_L   $B_OUT/b3gts_l.$ESDATE.$NDAYS.$GRID.$BSCEN.ncf\n" +
                    "   setenv B3GTS_S   $B_OUT/b3gts_s.$ESDATE.$NDAYS.$GRID.$BSCEN.ncf\n" +
                    "   setenv BGTS_L_O  $B_OUT/bgts_l_o.$ESDATE.$NDAYS.$GRID.$BSCEN.ncf\n" +
                    "   setenv BGTS_S_O  $B_OUT/bgts_s_o.$ESDATE.$NDAYS.$GRID.$BSCEN.ncf\n" +
                    "   setenv B3GTS_L_O $B_OUT/b3gts_l_o.$ESDATE.$NDAYS.$GRID.$BSCEN.ncf\n" +
                    "   setenv B3GTS_S_O $B_OUT/b3gts_s_o.$ESDATE.$NDAYS.$GRID.$BSCEN.ncf\n" +
                    "   setenv SRCGROUPS_OUT $OUTPUT/source_groups_out.$SRCABBR.$GRID.$BSCEN.ncf\n" +
                    "   setenv SRCGRP_REPORT $REPSTAT/srcgroups.$SRCABBR.$BSCEN.txt\n" +
                    "endif\n" +
                    "# Mobile source intermediate and output files \n" +
                    "#\n" +
                    "if ( $SMK_SOURCE == 'M' ) then\n" +
                    "   setenv MSCC      $INVOPD/MSCC.$SRCABBR.$FYIOP.txt\n" +
                    "   setenv REPINVEN  $REPSTAT/repinven.$SRCABBR.$INVOP.txt\n" +
                    "   setenv MTSUPNAME $SMKDAT/run_${MSCEN}/scenario/mtsup.$SRCABBR.$MSCEN.\n" +
                    "   setenv MTSUP     $MTSUPNAME$G_STDATE.txt\n" +
                    "   setenv MSSUP     $SMKDAT/run_${MBASE}/static/mssup.$SRCABBR.$SPC.$MBASE.txt\n" +
                    "   setenv MGSUP     $SMKDAT/run_$MBASE/static/mgsup.$SRCABBR.$GRID.$MBASE.txt\n" +
                    "   setenv MCREP     $REPSTAT/mcrep.$SRCABBR.$MSCEN.rpt\n" +
                    "   setenv MPROJREP  $REPSTAT/mprojrep.$SRCABBR.$MSCEN.rpt\n" +
                    "   setenv MREACREP  $REPSTAT/mreacrep.$SRCABBR.$MSCEN.rpt\n" +
                    "   setenv MCSUMREP  $REPSTAT/mcsumrep.$SRCABBR.$MSCEN.rpt\n" +
                    "   setenv MCTLWARN  $REPSTAT/mctlwarn.$SRCABBR.$MSCEN.txt\n" +
                    "   #      HOURLYT      automaticall set and created by emisfac_run.scr script\n" +
                    "   #      MEFLIST      automatically set and created by smk_run.scr script\n" +
                    "   setenv SPDSUM       $STATIC/spdsum.$SRCABBR.$MSCEN.txt # Speed summary file\n" +
                    "   setenv MOBL_O   $INVOPD/mobl.map.$SRCABBR.$FYINV.txt\n" +
                    "   setenv MBINV_O  $MBDAT/mbinv_o.$SRCABBR.$FYINV.emis.txt\n" +
                    "   setenv MBINV_AO $MBDAT/mbinv_o.$SRCABBR.$FYINV.actv.txt\n" +
                    "endif\n" +
                    "if ( $SMK_SOURCE == M || $RUN_SMKMERGE == Y && $MRG_MOBILE == Y ) then\n" +
                    "   setenv MOBL     $INVOPD/mobl.map.$SRCABBR.$FYIOP.txt   # Mobile inventory map\n" +
                    "   setenv MTMPNAME $SMKDAT/run_$MSCEN/scenario/mtmp.$SRCABBR.$MSCEN.\n" +
                    "   setenv MTMP     $MTMPNAME$G_STDATE.ncf\n" +
                    "   setenv MSMAT_L  $SMKDAT/run_$MBASE/static/msmat_l.$SRCABBR.$SPC.$MBASE.ncf\n" +
                    "   setenv MSMAT_S  $SMKDAT/run_$MBASE/static/msmat_s.$SRCABBR.$SPC.$MBASE.ncf\n" +
                    "   setenv MRMAT_L  $SMKDAT/run_$MSCEN/static/mrmat_l.$SRCABBR.$SPC.$MSCEN.ncf\n" +
                    "   setenv MRMAT_S  $SMKDAT/run_$MSCEN/static/mrmat_s.$SRCABBR.$SPC.$MSCEN.ncf\n" +
                    "   setenv MRSUP    $SMKDAT/run_$MSCEN/static/mrsup.$SRCABBR.$MSCEN.txt\n" +
                    "   setenv MCMAT    $SMKDAT/run_$MSCEN/static/mcmat.$SRCABBR.$MSCEN.ncf\n" +
                    "   setenv MUMAT    $SMKDAT/run_$MBASE/static/mumat.$SRCABBR.$GRID.$MBASE.ncf\n" +
                    "   setenv MGMAT    $SMKDAT/run_$MBASE/static/mgmat.$SRCABBR.$GRID.$MBASE.ncf\n" +
                    "   setenv MPMAT    $SMKDAT/run_$MSCEN/static/mpmat.$SRCABBR.$MSCEN.ncf\n" +
                    "   setenv SRCGROUPS_OUT $OUTPUT/source_groups_out.$SRCABBR.$GRID.$MSCEN.ncf\n" +
                    "   setenv SRCGRP_REPORT $REPSTAT/srcgroups.$SRCABBR.$MSCEN.txt\n" +
                    "endif\n" +
                    "## Point source intermediate and output files\n" +
                    "#\n" +
                    "if ( $SMK_SOURCE == 'P' ) then\n" +
                    "   setenv PDAY      $INVOPD/pday.$SRCABBR.$INVOP.ncf       # Point NetCDF day-specific\n" +
                    "   setenv PHOUR     $INVOPD/phour.$SRCABBR.$INVOP.ncf      # Point NetCDF hour-specific\n" +
                    "   setenv PHOURPRO  $INVOPD/phourpro.$SRCABBR.$INVOP.ncf   # Pt NetCDF src-spec dnl profs\n" +
                    "   setenv REPINVEN  $REPSTAT/repinven.$SRCABBR.$INVOP.txt\n" +
                    "   setenv PTREF_ALT $INVOPD/ptref.$SRCABBR.smkout.txt      # Point temporal x-ref\n" +
                    "   setenv PTSUPNAME $SMKDAT/run_${PSCEN}/scenario/ptsup.$SRCABBR.$PSCEN.\n" +
                    "   setenv PTSUP     $PTSUPNAME$G_STDATE.txt\n" +
                    "   setenv PSSUP     $SMKDAT/run_${PBASE}/static/pssup.$SRCABBR.$SPC.$PBASE.txt\n" +
                    "   setenv PCREP     $REPSTAT/pcrep.$SRCABBR.$PSCEN.rpt           \n" +
                    "   setenv PPROJREP  $REPSTAT/pprojrep.$SRCABBR.$PSCEN.rpt\n" +
                    "   setenv PREACREP  $REPSTAT/preacrep.$SRCABBR.$PSCEN.rpt\n" +
                    "   setenv PCSUMREP  $REPSTAT/pcsumrep.$SRCABBR.$PSCEN.rpt\n" +
                    "   setenv PCTLWARN  $REPSTAT/pctlwarn.$SRCABBR.$PSCEN.txt\n" +
                    "   setenv PNTS_O    $INVOPD/pnts.map.$SRCABBR.$FYINV.txt\n" +
                    "   setenv PTINV_O   $PTDAT/ptinv_o.$SRCABBR.$FYINV.orl.txt\n" +
                    "   setenv REPPELV   $REPSTAT/reppelv.$SRCABBR.$PSCEN.rpt\n" +
                    "endif\n" +
                    "if ( $SMK_SOURCE == P || $RUN_SMKMERGE == Y && $MRG_POINT == Y ) then\n" +
                    "   setenv PNTS     $INVOPD/pnts.map.$SRCABBR.$FYIOP.txt   # Point inventory map\n" +
                    "   setenv PSCC     $INVOPD/PSCC.$SRCABBR.$FYIOP.txt       # Point unique SCC list\n" +
                    "   setenv PTMPNAME $SMKDAT/run_$PSCEN/scenario/ptmp.$SRCABBR.$PSCEN.\n" +
                    "   setenv PTMP     $PTMPNAME$G_STDATE.ncf\n" +
                    "   setenv PSMAT_L  $SMKDAT/run_$PBASE/static/psmat_l.$SRCABBR.$SPC.$PBASE.ncf\n" +
                    "   setenv PSMAT_S  $SMKDAT/run_$PBASE/static/psmat_s.$SRCABBR.$SPC.$PBASE.ncf\n" +
                    "   setenv PRMAT_L  $SMKDAT/run_$PSCEN/static/prmat_l.$SRCABBR.$SPC.$PSCEN.ncf\n" +
                    "   setenv PRMAT_S  $SMKDAT/run_$PSCEN/static/prmat_s.$SRCABBR.$SPC.$PSCEN.ncf\n" +
                    "   setenv PRSUP    $SMKDAT/run_$PSCEN/static/prsup.$SRCABBR.$PSCEN.txt\n" +
                    "   setenv PCMAT    $SMKDAT/run_$PSCEN/static/pcmat.$SRCABBR.$PSCEN.ncf\n" +
                    "   setenv PGMAT    $SMKDAT/run_$PBASE/static/pgmat.$SRCABBR.$GRID.$PBASE.ncf\n" +
                    "   setenv PPMAT    $SMKDAT/run_$PSCEN/static/ppmat.$SRCABBR.$PSCEN.ncf\n" +
                    "   setenv STACK_GROUPS $OUTPUT/stack_groups.$SRCABBR.$GRID.$PBASE.ncf\n" +
                    "   setenv SRCGROUPS_OUT $OUTPUT/source_groups_out.$SRCABBR.$GRID.$PSCEN.ncf\n" +
                    "   setenv SRCGRP_REPORT $REPSTAT/srcgroups.$SRCABBR.$PSCEN.txt\n" +
                    "   setenv INLNTS_L $OUTPUT/inlnts_l.$SRCABBR.$ESDATE.$NDAYS.$GRID.$PSCEN.ncf\n" +
                    "   setenv PLAY     $SMKDAT/run_$PBASE/scenario/play.$SRCABBR.$ESDATE.$NDAYS.$GRID.$MSPBAS.ncf\n" +
                    "   setenv PLAY_EX  $SMKDAT/run_$PBASE/scenario/play_ex.$SRCABBR.$ESDATE.$NDAYS.$GRID.$MSPBAS.ncf\n" +
                    "   setenv PELV     $STATIC/PELV.$SRCABBR.$PBASE.txt       # Elev/PinG pt source list\n" +
                    "endif\n" +
                    "# Cumstomized Smkmerge output file names when merging all souce sectors\n" +
                    "# If using Smkmerge to merge all sectors\n" +
                    "   \n" +
                    "   if ( $SMKMERGE_CUSTOM_OUTPUT == Y && $RUN_SMKMERGE == Y || $MOVESMRG_CUSTOM_OUTPUT == Y && $RUN_MOVESMRG == Y   ) then\n" +
                    "         setenv EOUT  $OUTPUT/egts_l.$SRCABBR.$ESDATE.$NDAYS.$SPC.$GRID.$ESCEN.ncf \n" +
                    "         setenv AOUT  $A_OUT/agts_l.$SRCABBR.$ESDATE.$NDAYS.$GRID.$ASCEN.ncf \n" +
                    "         setenv BOUT  $B_OUT/bgts_l_o.$SRCABBR.$ESDATE.$NDAYS.$GRID.$BSCEN.ncf\n" +
                    "         setenv POUT  $P_OUT/pgts_l.$SRCABBR.$ESDATE.$NDAYS.$GRID.$PSCEN.ncf\n" +
                    "         setenv MOUT  $M_OUT/mgts_l.$SRCABBR.$ESDATE.$NDAYS.$GRID.$MSCEN.ncf\n" +
                    "         setenv PING  $OUTPUT/pingts_l.$SRCABBR.$ESDATE.$NDAYS.$GRID.$PSCEN.ncf \n" +
                    "         setenv ELEV  $OUTPUT/elevts_l.$SRCABBR.$ESDATE.$NDAYS.$GRID.$PSCEN.txt \n" +
                    "         setenv REPEG $REPSCEN/rep_${MM}_all_${ESDATE}_${GRID}_${SPC}.txt\n" +
                    "         setenv REPAG $REPSCEN/rep_${MM}_${SRCABBR}_${ESDATE}_${GRID}_${SPC}.txt\n" +
                    "         setenv REPBG $REPSCEN/rep_${MM}_${SRCABBR}_${ESDATE}_${GRID}_${SPC}.txt\n" +
                    "         setenv REPPG $REPSCEN/rep_${MM}_${SRCABBR}_${ESDATE}_${GRID}_${SPC}.txt\n" +
                    "         setenv REPMG $REPSCEN/rep_${MM}_${SRCABBR}_${ESDATE}_${GRID}_${SPC}.txt\n" +
                    "         setenv AGTS_L    $AOUT \n" +
                    "         setenv PGTS_L    $POUT\n" +
                    "         setenv PGTS3D_L  $POUT\n" +
                    "         setenv EGTS_L    $EOUT\n" +
                    "         setenv REPB3GTS_L $REPSCEN/repb3gts_l.$SRCABBR.$ESDATE.$NDAYS.$GRID.$BSCEN.rpt\n" +
                    "         setenv REPB3GTS_S $REPSCEN/repb3gts_s.$SRCABBR.$ESDATE.$NDAYS.$GRID.$BSCEN.rpt\n" +
                    "    else\n" +
                    "      source $ASSIGNS/setmerge_files.scr    #  Define merging output file names \n" +
                    "    endif\n" +
                    "#  Make directory for executables\n" +
                    "   setenv SMK_BIN \"$SMKROOT/$BIN\"\n" +
                    "   if( ! -e $SMK_BIN ) mkdir -p $SMK_BIN\n" +
                    "#  Create and change permissions for output directories\n" +
                    "   $ASSIGNS/smk_mkdir\n" +
                    "   if ( $status > 0 ) then\n" +
                    "      set outstat = 1   \n" +
                    "   endif\n" +
                    "#\n" +
                    "#  Delete appropriate NetCDF files for the programs that are being run\n" +
                    "   if ( -e $ASSIGNS/smk_rmfiles.scr ) then\n" +
                    "      $ASSIGNS/smk_rmfiles.scr\n" +
                    "   else\n" +
                    "      echo \"NOTE: missing smk_rmfiles.scr in ASSIGNS directory for\"\n" +
                    "      echo \"      automatic removal of SMOKE I/O API intermediate and\"\n" +
                    "      echo \"      output files\"\n" +
                    "   endif\n" +
                    "#\n" +
                    "#  Unset temporary environment variables\n" +
                    "   source $ASSIGNS/unset.scr\n" +
                    "if ( $outstat == 1 ) then\n" +
                    "   echo \"ERROR: Problem found while setting up SMOKE.\"\n" +
                    "   echo \"       See messages above.\"\n" +
                    "   exit( 1 )\n" +
                    "endif";


            out.println(JOBFILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void run(){
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"/bin/csh",module_dir+"/run-smoke.csh"};
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

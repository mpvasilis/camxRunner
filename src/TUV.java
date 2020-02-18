import java.io.*;
import java.util.Map;

public class TUV extends CAMxSIMULATION {

    private String jobfile_dir=PREPROCESSORS_DIR+"tuv/";
    private String jobfile="14_tuv.run";
    private String module_dir=PREPROCESSORS_DIR+"tuv/";
    private String work_dir="";
    private String input_dir="";
    private String output_dir="";


    public TUV() {

    }


    public void createJobFile(WRF wrf){
        String WRFOUTFILE = wrf.getNcfile();
        String STARTDAY = wrf.getSIMULATION_START_DATE_format2();
        String ENDDAY =  wrf.getSIMULATION_END_DATE_format2();
        int NCOLS = wrf.getGrid_size_x();
        int NROWS = wrf.getGrid_size_y();

        try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {
            String JOBFILE = "set MECH    =  CB6                  # [CB05|CB6|SAPRC99|SAPRC07|SAPRC07I]\n" +
                    "set SCHEME  =  2                    # 1 = pseudo spherical 2-stream\n" +
                    "                                    # 2 = discrete ordinates (recommended)\n" +
                    "set yymmdd  = "+wrf.getSIMULATION_END_DATE_format2()+"                # YYMMDD format\n" +
                    "if ($SCHEME == 1) set SCM = 'ps2str'\n" +
                    "if ($SCHEME == 2) set SCM = 'do'\n" +
                    "set TUVINP  = tuv.${SCM}_${MECH}.inp\n" +
                    "set TUVOUT  = tuv.${SCM}_${MECH}.diag\n" +
                    "set OUTFILE = tuv.${SCM}\n" +
                    "if ($MECH == 'CB6') then\n" +
                    "  set RXNUM = 23\n" +
                    "  set RXLST = '94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 114, 115, 116, 111, 112, 113'\n" +
                    "endif\n" +
                    "if ($MECH == 'CB05') then\n" +
                    "  set RXNUM = 19\n" +
                    "  set RXLST = '77, 84, 83, 79, 78, 87, 66, 88, 89, 90, 91, 63, 68, 67, 62, 92, 61, 93, 82'\n" +
                    "endif\n" +
                    "if ($MECH == 'SAPRC07') then\n" +
                    "  set RXNUM = 29\n" +
                    "  set RXLST = '168 169 170 172 173 161 158 159 155 175 157 156 149 148 143 165 152 154 153 166 146 147 145 164 167 163 160 144 174'\n" +
                    "endif\n" +
                    "cat << EOF >! ./tuv.inp\n" +
                    "program version    |TUV4.8CAMx6.30\n" +
                    "output file name   | /opt/bin/camx/inputs_mine_new/$OUTFILE\n" +
                    "o3map file name    | /opt/bin/camx/inputs_mine_new/o3."+wrf.getSIMULATION_START_DATE_format2()+".txt\n" +
                    "# of vert levels   | 11\n" +
                    "levels, km agl     | 0. .15 .35 .6 1. 2. 3. 4. 6. 8. 10.\n" +
                    "date (YYMMDD)      | $yymmdd\n" +
                    "rad transfer scheme| $SCHEME\n" +
                    "# of phot reactions| $RXNUM\n" +
                    "TUV reaction #s    | $RXLST\n" +
                    "EOF\n" +
                    module_dir+"/src/tuv\n" +
                    "mv -f tuv.inp $TUVINP\n" +
                    "mv -f tuv_diag.out $TUVOUT\n" +
                    "# Do a quick comparison of CAMx photolysis rate files\n" +
                    "# and report the ratios in a flat ASCII format\n" +
                    "# Note: Number of reactions in the two files have to be the same.\n" +
                    "set RUN_TUVCOMPR = 'NO'             # Run TUV comparison\n" +
                    "if ( $RUN_TUVCOMPR == 'YES' ) then\n" +
                    "./tuvcompr/tuvcompr.linux << EOF\n" +
                    "rates file 1       | /opt/bin/camx/inputs_mine_new/tuv.do.$yymmdd\n" +
                    "rates file 2       |./tuv.ps2str_${MECH}.$yymmdd\n" +
                    "summary output file|./compare.${MECH}_do_ps2str\n" +
                    "number of reactions| $RXNUM\n" +
                    "number of altitudes| 11\n" +
                    "EOF\n" +
                    "endif";


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
            proc = rt.exec(commands,
                    null, new File(jobfile_dir));
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

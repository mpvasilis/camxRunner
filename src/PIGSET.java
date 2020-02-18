import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class PIGSET extends CAMxSIMULATION {

    private String jobfile_dir=PREPROCESSORS_DIR+"pigset/";
    private String jobfile="3_pigset.run";
    private String module_dir=PREPROCESSORS_DIR+"pigset";
    private String work_dir=PREPROCESSORS_DIR+"igset";
    private String input_dir=PREPROCESSORS_DIR+"pigset";
    private String output_dir=PREPROCESSORS_DIR+"pigset";

    CAMxSIMULATION caMxSIMULATION;

    public PIGSET(CAMxSIMULATION caMxSIMULATION) {

        this.caMxSIMULATION=caMxSIMULATION;

    }

    public void createJobFile(){
        String ASCIIinptfile= caMxSIMULATION.getASCIIinptfile();
        String CAMxbinaryptfile=caMxSIMULATION.getCAMxbinaryptfile();
        String GRIDDESCfile=caMxSIMULATION.getGRIDDESCfile();
        try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {
            String JOBFILE = "#\n" +
                    "#  PIGSET reads an ASCII PTSOURCE preprocessor input file \n" +
                    "#         and performs the following functions:\n" +
                    "#\n" +
                    "#     1) Identifies all sources emitting more than a user-specified lower\n" +
                    "#        limit of NOx (TPD) (optionally, within a subdomain)\n" +
                    "#     2) Identifies all sources co-located with those sources found in (1);\n" +
                    "#        distance defining co-location is user-specified, and only sources\n" +
                    "#        that are within this distance AND are greater than or equal to\n" +
                    "#        10% of the user-specified NOx threshold are identified\n" +
                    "#     3) Ranks these sources, and prints out a summary to a separate output\n" +
                    "#        file\n" +
                    "#     4) Writes a new binary UAM-ready point source file with all identified\n" +
                    "#        sources in (1) and (2) selected for PiG treatment (negative diameter) \n" +
                    "#\n" +
                    "#     This program assumes that the input elevated point source file\n" +
                    "#     contains 24 hourly records\n" +
                    "#\n" +
                    "#\n" +
                    "# ASCII in.pt file   | - name of input ascii ptsource preprocessor file\n" +
                    "# Write new pt file? | - T or F; T will generate a binary point source file\n" +
                    "# CAMx binary pt file| - file name for binary point source file\n" +
                    "# Generate rank file?| - T or F; T will generate a ranked list of NOx sources\n" +
                    "# NOx rank file name | - file name for ranked NOx table\n" +
                    "# Min NOx (TPD)      | - minimum value to include in ranked NOx table\n" +
                    "# Colocation dist m  | - distance in meters to define co-location\n" +
                    "# Locs in lat/lon?   | - T or F; T indicates locations are lat-lon\n" +
                    "# Subdomain?         | - T or F; T examines a rectangular subdomain for PiGs\n" +
                    "# xy SW and NE corner| - (x,y) SW subgrid corner and (x,y) NE subgrid corner\n" +
                    "#\n" +
                    "#  Notes:\n" +
                    "#\n" +
                    "#  If \"generate rank file\" is F then PIGSET reads an already generated\n" +
                    "#  table of ranked sources and simply sets them for PiG treatment in the\n" +
                    "#  output binary file.  Thus, PIGSET is often run twice: first to\n" +
                    "#  generate a list of sources for PiG treatment; second to PiG the sources\n" +
                    "#  on the list.  This allows for the list to be reviewed or edited.  For \n" +
                    "#  instance, a single list can be used for multiple days (to ensure consistent\n" +
                    "#  PiG sources even if emissions vary from day to day).\n" +
                    "#\n" +
                    "#  The subdomain specification must be in the same coordinate units as the\n" +
                    "#  input ptsrce file.\n" +
                    "#\n" +
                    "# ===============================================================================\n" +
                    "#\n" +
                    " rm pigranks.YYMMDD.dat\n" +
                    ""+module_dir+"/src/pigset << EOF\n" +
                    "ASCII in.pt file   |" +ASCIIinptfile+ "\n"+
                    "Write new pt file? |F\n" +
                    "CAMx binary pt file|" +CAMxbinaryptfile+ "\n"+
                    "Generate rank file?|T\n" +
                    "NOx rank file name |pigranks.YYMMDD.dat\n" +
                    "Min NOx (TPD)      |20.\n" +
                    "Colocation dist m  |100.\n" +
                    "Locs in lat/lon?   |F\n" +
                    "Subdomain?         |T\n" +
                    "xy SW and NE corner|" + readGRIDDESC(GRIDDESCfile)+ "\n"+
                    "EOF\n" +
                    ""+module_dir+"/src/pigset << EOF\n" +
                    "ASCII in.pt file   |" +ASCIIinptfile+ "\n"+
                    "Write new pt file? |T\n" +
                    "CAMx binary pt file|" +CAMxbinaryptfile+ "\n"+
                    "Generate rank file?|F\n" +
                    "NOx rank file name |pigranks.YYMMDD.dat\n" +
                    "Min NOx (TPD)      |20.\n" +
                    "Colocation dist m  |100.\n" +
                    "Locs in lat/lon?   |F\n" +
                    "Subdomain?         |T\n" +
                    "xy SW and NE corner|" + readGRIDDESC(GRIDDESCfile)+ "\n"+
                    "EOF";

            out.println(JOBFILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        File source = new File("/opt/bin/camx_preprocessors/MCIP/output/GRIDDESC");
        File dest = new File("/opt/bin/camx_preprocessors/MEGAN/MEGANv2.10/work/GRIDDESC");
        File dest2 = new File("/opt/bin/GRIDDESC");

        EmissInvCopy copyfun = new EmissInvCopy();
        try {
            copyfun.copyFile(source,dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            copyfun.copyFile(source,dest2);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public String readGRIDDESC(String locaton) {
        String dataline = null;
        try {
             dataline = Files.readAllLines(Paths.get(locaton)).get(5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] data = dataline.split("\\s+"); // 3 7 13 19 21 23
        String calc1= Float.toString(Float.parseFloat(data[1])+Float.parseFloat(data[3])*Float.parseFloat(data[5]));
        String calc2= Float.toString(Float.parseFloat(data[2])+Float.parseFloat(data[4])*Float.parseFloat(data[6]));
        String output = data[1]+" "+data[2]+"   "+calc1+" "+calc2+"";
        int c1=(int) Float.parseFloat(data[1])/1000;
        int c2=(int) Float.parseFloat(data[2])/1000;

        caMxSIMULATION.setXo(Integer.toString(c1));
        caMxSIMULATION.setYo(Integer.toString(c2));
        return output;
    }

    }

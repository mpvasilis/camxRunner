import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class OCEANIC extends CAMxSIMULATION {

    private String jobfile_dir=PREPROCESSORS_DIR+"oceanic/";
    private String jobfile="15_oceanic.run";
    private String module_dir=PREPROCESSORS_DIR+"oceanic/";
    private String work_dir="";
    private String input_dir="";
    private String output_dir="";

    public OCEANIC() { }

    // URL http://orca.science.oregonstate.edu/2160.by.4320.monthly.hdf.chl.gsm.m.php
    public String downloadOceanicChlorophyllData(String date,String location){
        String saveFile="";
        try{
            String ftpUrl = "http://orca.science.oregonstate.edu/data/2x4/monthly/chl.r2014.gsm.v8.m/hdf/chl."+date+".hdf.gz";
            saveFile = location+"chl."+date+".hdf.gz";
            URL url = new URL(ftpUrl);
            URLConnection conn = url.openConnection();
            InputStream inputStream = conn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(saveFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();
            System.out.println(saveFile+" downloaded");

        }catch (Exception e){
            e.printStackTrace();
        }
        return saveFile;

    }
    public void convert2NETCDF(String inputfile, String outputfile, String netcdffile){
        byte[] buffer = new byte[1024];
        try{
            GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(inputfile));
            FileOutputStream out = new FileOutputStream(outputfile);
            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            gzis.close();
            out.close();
            System.out.println("File ungziped to "+outputfile);

        }catch(IOException ex){
            ex.printStackTrace();
        }
        try {
            PrintWriter f0 = new PrintWriter(new FileWriter(netcdffile));
            Process p = Runtime.getRuntime().exec(module_dir+"get_hdf4/ncdump -b c "+outputfile);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                //System.out.println(line);
                f0.println(line);
            }
            f0.close();
            System.out.println("File converted to cdl to "+netcdffile);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void createJobFile(WRF wrf){
        String chldatadate=wrf.getYear()+wrf.getJulianMonth();

        if(false){
            chldatadate = wrf.getYear()+wrf.getJulianMonth();
            String ChlorophyllData = downloadOceanicChlorophyllData(chldatadate,"/opt/bin/camx_preprocessors/oceanic/");
            convert2NETCDF(ChlorophyllData,module_dir+"chl."+chldatadate+".hdf",module_dir+"chl."+chldatadate+".cdl");
        }
        else{
            chldatadate = wrf.FORFUTURE_getyear(Integer.parseInt(wrf.getYear()))+wrf.getJulianMonth();
            String ChlorophyllData = downloadOceanicChlorophyllData(chldatadate,"/opt/bin/camx_preprocessors/oceanic/");
            convert2NETCDF(ChlorophyllData,module_dir+"chl."+chldatadate+".hdf",module_dir+"chl."+chldatadate+".cdl");
        }

        try {
            wrf.createLandMask("/opt/bin/camx_preprocessors/oceanic/saltwater_mask.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {
            String JOBFILE = "#!/bin/csh -f\n" +
                    "setenv BIN_LWMASK F\n" +
                    "#   ! Use this for CAMx-binary LW mask file\n" +
                    "set INMET  = \"/opt/bin/camx/met_mine_new\"\n" +
                    "set INEMS  = \"/opt/bin/camx/emiss\"\n" +
                    "set year = " + wrf.getYear().substring(2)+"\n"+
                    "set yr = " + wrf.getYearShort()+"\n"+
                    "set mo = " + wrf.getMonth()+"\n"+
                    "set g2j = " + wrf.dayswithcountStart()+"\n"+
                    "foreach dy ( "+wrf.getforeachdy()+")\n" +
                    "@ jdy = $dy + $g2j\n" +
                    "echo \"Date: $year$mo$dy - $year$jdy\"\n" +
                    module_dir+"/src/oceanic << IEOF\n" +
                    "SS scaling factor  :1.0\n" +
                    "Ocean salinity     :35.0\n" +
                    "Coastline length   :NONE\n" +
                    "Output emiss file  :$INEMS/oceanic.$year$mo$dy.bin\n" +
                    "File identifier    :"+wrf.getYear()+" Oceanic Emissions\n" +
                    "Number of species  :19\n" +
                    "Species name # 1   :NA\n" +
                    "Species name # 2   :PCL\n" +
                    "Species name # 3   :PSO4\n" +
                    "Species name # 4   :SSCL\n" +
                    "Species name # 5   :SSBR\n" +
                    "Species name # 6   :MB3\n" +
                    "Species name # 7   :MB2\n" +
                    "Species name # 8   :MBC\n" +
                    "Species name # 9   :MB2C\n" +
                    "Species name # 10  :MBC2\n" +
                    "Species name # 11  :CH3I\n" +
                    "Species name # 12  :MIC\n" +
                    "Species name # 13  :MIB\n" +
                    "Species name # 14  :MI2\n" +
                    "Species name # 15  :DMS\n" +
                    "Species name # 16  :WIND\n" +
                    "Species name # 17  :CHLA\n" +
                    "Species name # 18  :RELH\n" +
                    "Species name # 19  :DMSWC\n" +
                    "DMS output as SO2  :.false.\n" +
                    "CAMx 3D file       :$INMET/camx.3d.$year$mo$dy.bin\n" + //change
                    "CAMx 2D file       :$INMET/camx.2d.$year$mo$dy.bin\n" + //change
                    "CAMx Land Use file :$INMET/camx.lu.bin\n" +
                    "Saltwater mask     :/opt/bin/camx_preprocessors/oceanic/saltwater_mask.txt\n" +
                    "Chlorophyll-A file :"+module_dir+"chl."+chldatadate+".cdl"+"\n" +
                    "DMS concentrations :/opt/bin/camx_preprocessors/oceanic/dms_conc/DMSclim_"+wrf.getMonth()+".csv\n" +
                    "IEOF\n" +
                    "#Saltwater mask :./saltwater_mask.txt\n" +
                    "#Species name # 20 :SRFT\n" +
                     module_dir+"/src/mrgspc << EOF\n" +
                    "Input File         |$INEMS/MEGANv3.thess.CB6.20$year$g2j.camx\n" + //change
                    "Input File         |$INEMS/oceanic.$year$mo$dy.bin\n" + //change
                    "Input File         |END\n" +
                    "Output File        |$INEMS/emiss.anthro_bio_ocn.$year$mo$dy.bin\n" + //change
                    "EOF\n" +
                    "end  #end loop over days";

            out.println(JOBFILE);
        } catch (IOException e) {
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

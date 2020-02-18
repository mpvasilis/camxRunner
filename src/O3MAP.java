import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class O3MAP extends CAMxSIMULATION {

    private String jobfile_dir=PREPROCESSORS_DIR+"o3map/";
    private String jobfile="o3map.run";
    private String module_dir=PREPROCESSORS_DIR+"o3map/";
    private String work_dir="";
    private String input_dir="";
    private String output_dir="";

    CAMxSIMULATION caMxSIMULATION;

    public O3MAP(CAMxSIMULATION caMxSIMULATION) {

        this.caMxSIMULATION=caMxSIMULATION;

    }


    // URL ftp://toms.gsfc.nasa.gov/pub/omi/data/ozone/Y2015/L3_ozone_omi_20150103.txt
    public void downloadO3data(String dateforsave, String datefordownload, String year) {
       try{
           String ftpUrl = "https://ozonewatch.gsfc.nasa.gov/data/omi/Y"+year+"/L3_ozone_omi_"+datefordownload+".txt";
           String saveFile = dateforsave+".txt";
           URL url = new URL(ftpUrl);
           URLConnection conn = url.openConnection();
           conn.setConnectTimeout(15 * 1000);
           InputStream inputStream = conn.getInputStream();
           FileOutputStream outputStream = new FileOutputStream(saveFile);
           byte[] buffer = new byte[4096];
           int bytesRead = -1;
           while ((bytesRead = inputStream.read(buffer)) != -1) {
               outputStream.write(buffer, 0, bytesRead);
           }
           outputStream.close();
           inputStream.close();
           System.out.println(saveFile+" downloaded.");

       }catch (IOException e){
           e.printStackTrace();
       }

    }

    public void createJobFile(WRF wrf){
        if(false) { //!wrf.isFuture()
            for (int i = 0; i < wrf.getSimulationDays(); i++) {
                downloadO3data(wrf.getSIMULATION_START_DATE_format2(i), wrf.getSIMULATION_START_DATE_formatyyyy(i), wrf.getYear());
                try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {
                    String JOBFILE = module_dir+"/src_v3/o3map << EOF\n" +
                            "Coordinate project |LAMBERT\n" +
                            "xo,yo,cln,clt,t1,t2|"+caMxSIMULATION.getXo()+","+caMxSIMULATION.getYo()+",10,52,30,45\n" +
                            "dx,dy              |2.,2.\n" +
                            "nx,ny              |42,42\n" +
                            "Output filename    |/opt/bin/camx/inputs_mine/o3."+wrf.getSIMULATION_START_DATE_format2()+".txt\n" +
                            "Numbr of TOMS files|" +wrf.getSimulationDays()+"\n";

                    for (int l = 0; l <wrf.getSimulationDays(); l++) {
                        JOBFILE=JOBFILE+ "Bday,Eday,TOMS file|"+wrf.getSIMULATION_START_DATE_format2(l)+","+wrf.getSIMULATION_START_DATE_format2(l)+","+wrf.getSIMULATION_START_DATE_format2(l)+".txt \n";
                    }
                    JOBFILE = JOBFILE+ "EOF";


                    out.println(JOBFILE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            String oldDate="olddate";
            for (int i = 0; i < wrf.getSimulationDays(); i++) {
                System.out.println(wrf.getSIMULATION_START_DATE_format2(i));
                String newDate= wrf.getJulianDate(i)+" "+wrf.getMonthString(i)+"  "+wrf.getDays(i)+", "+wrf.getYear();
                String futureOzonefile="/opt/bin/camx_preprocessors/o3map/o3_future.txt";
                replaceDate(futureOzonefile,oldDate,newDate);
                File source = new File(futureOzonefile);
                File dest = new File("/opt/bin/camx_preprocessors/o3map/o3.future"+wrf.getSIMULATION_START_DATE_format2(i)+".txt");
                File destcamx = new File("/opt/bin/camx/inputs_mine_new/o3."+wrf.getSIMULATION_START_DATE_format2(i)+".txt");

                copyFile(source,dest);
              //  copyFile(source,destcamx);

                try (PrintWriter out = new PrintWriter(jobfile_dir+jobfile)) {
                    String JOBFILE = module_dir+"/src_v3/o3map << EOF\n" +
                            "Coordinate project |LAMBERT\n" +
                            "xo,yo,cln,clt,t1,t2|"+caMxSIMULATION.getXo()+","+caMxSIMULATION.getYo()+",10,52,30,45\n" +
                            "dx,dy              |2.,2.\n" +
                            "nx,ny              |42,42\n" +
                            "Output filename    |/opt/bin/camx/inputs_mine_new/o3."+wrf.getSIMULATION_START_DATE_format2(i)+".txt\n" +
                            "Numbr of TOMS files|1 \n";

                        JOBFILE=JOBFILE+ "Bday,Eday,TOMS file|"+wrf.getSIMULATION_START_DATE_format2(i)+","+wrf.getSIMULATION_START_DATE_format2(i)+",/opt/bin/camx_preprocessors/o3map/o3.future"+wrf.getSIMULATION_START_DATE_format2(i)+".txt\n";

                    JOBFILE = JOBFILE+ "EOF";


                    out.println(JOBFILE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                run();
            }

        }

    }

    public void copyFile(File source, File dest){
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private static void replaceDate(String file, String olddate, String newdate){
        try {
            Path path = Paths.get(file);
            Stream <String> lines = Files.lines(path);
            List <String> replaced = lines.map(line -> line.replaceAll(olddate, newdate)).collect(Collectors.toList());
            Files.write(path, replaced);
            lines.close();
            System.out.println("Date replaced to future date");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

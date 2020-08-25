import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import io.sentry.SentryClientFactory;
import io.sentry.event.Event;
import io.sentry.event.EventBuilder;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class Main {

    private static SentryClient sentry;

    public static void main(String[] args) {

        // ******************************************
        // Application Monitoring and Error Tracking
        // ******************************************

        Sentry.init("https://0d7be045e046447cab962d0e321965f9@sentry.io/2518431");
        sentry = SentryClientFactory.sentryClient();

        // *********************************
        // CAMx simulation basic settings
        // *********************************

        String RUNDATE = args[0]; // 20150101
        String RUNHOURS = args[1]; // 48
        String RUNCITY = args[2]; // Thessaloniki
        String CORES = args[3]; // 8
        String runID = getrunID(); // 20
        String EMISION_INV = "/mnt/data/"+runID+"/SMOKE_CAMX_FinalEmissionInventory.csv"; // EMISION_INV /opt/data
        String apiEndpoint = args[4]; // http://oldrestapi.icarus2020.eu:8081
        //String getMeteorologyFromOtherRun = args[5]; // Other runs ID

        String wrfoutputfile ="/mnt/data/"+runID+"/wrfout_d02"; // wrfoutput /opt/data
        String wrfinputfile = "/mnt/data/"+runID+"/wrfinput_d01"; // wrfoutput /opt/data

       /*
       if(args.length==6) {
            if (Integer.parseInt(getMeteorologyFromOtherRun) > 0) {
                wrfoutputfile = "/mnt/data/" + getMeteorologyFromOtherRun + "/wrfout_d02"; // wrfoutput /opt/data
                wrfinputfile = "/mnt/data/" + getMeteorologyFromOtherRun + "/wrfinput_d01"; // wrfoutput /opt/data
            }
        }
        */

        String Baseline = args[5];
        boolean isBaseline = false;
        if(Baseline.equals("Baseline")){
            isBaseline = true;
            EMISION_INV = "/mnt/data/"+runID+"/SMOKE_CAMX_FinalEmissionInventory_baseline.csv";
            System.out.println("Baseline simulation detected...\n");
        }




        boolean CAMxTESTCASE = false;

        // *********************************
        // CAMx simulation flow
        // *********************************

        //if(RUNDATE.equals("20150101") && RUNCITY.equals("Thessaloniki") && RUNHOURS.equals("48")) CAMxTESTCASE=true;

        boolean checks = checkFiles(wrfoutputfile,wrfinputfile,EMISION_INV,runID,apiEndpoint);

        if (CAMxTESTCASE == false && checks == true) {

            System.out.println("Initializing CAMx simulation....\n" +
                    "City: " + RUNCITY + "\n" +
                    "Date " + RUNDATE + "\n" +
                    "Run Hours " + RUNHOURS + "\n" +
                    "Emssion Inventory: " + EMISION_INV + "\n" +
                    "WRF Output: " + wrfoutputfile + "\n" +
                    "WRF Input: " + wrfinputfile + "\n" +
                    "Run ID: " + runID + "\n" +
                    "Total CPU cores:" + CORES + "\n");

            CAMxSIMULATION camxSimulation = new CAMxSIMULATION(RUNDATE, RUNHOURS, RUNCITY, CORES, EMISION_INV, wrfoutputfile);



            WRF wrfinput = new WRF(wrfinputfile);
            try {
                wrfinput.readWRFOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Start time for WRF input: " + wrfinput.getSIMULATION_START_DATE());
            System.out.println("End time for WRF input: " + wrfinput.getSIMULATION_END_DATE());
            System.out.println("Start Julian time for WRF input: " + wrfinput.getStartJulianDate());
            System.out.println("End Julian time for WRF input: " + wrfinput.getEndJulianDate());
            System.out.println("WRF input grid size: " + wrfinput.getGrid_size_x() + "x" + wrfinput.getGrid_size_y());

            WRF wrf = new WRF(wrfoutputfile);
            try {
                wrf.readWRFOutput();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Start time for WRF output: " + wrf.getSIMULATION_START_DATE());
            System.out.println("End time for WRF output: " + wrf.getSIMULATION_END_DATE());
            System.out.println("Start Julian time for WRF output: " + wrf.getStartJulianDate());
            System.out.println("End Julian time for WRF output: " + wrf.getEndJulianDate());
            System.out.println("WRF output grid size: " + wrf.getGrid_size_x() + "x" + wrf.getGrid_size_y());

            ArrayList<SimulationDay> SimulationDaysList = new ArrayList<SimulationDay>();
            for (int i = 0; i < wrf.getSimulationDays(); i++) {
                // SimulationDay simday = new SimulationDay(wrf.getSIMULATION_START_DATE_format2(i));
                //    SimulationDaysList.add(simday);
            }
            camxSimulation.simdays = SimulationDaysList;

            System.out.println("1) Running MCIP!");
            MCIP mcip = new MCIP(camxSimulation);
            mcip.createJobFile(wrf);
            mcip.run();

            System.out.println("2) Running SMOKE using given emission inventory!");
            SMOKE smoke = new SMOKE(camxSimulation);
            smoke.createJobFile(wrf);
            smoke.run();

            System.out.println("3) Running PIGSET!");
            PIGSET pigset = new PIGSET(camxSimulation);
            pigset.createJobFile();
            pigset.run();

            System.out.println("4) Running WRFCAMX!");

            WRFCAMX wrfcamx = new WRFCAMX(camxSimulation);
            wrfcamx.createJobFile(wrf);
            wrfcamx.run();

            System.out.println("5) Running nfc2ioapi!");

            NFC2IOAPI nfc2IOAPI = new NFC2IOAPI(RUNDATE, RUNHOURS, RUNCITY, CORES, EMISION_INV, runID);
            nfc2IOAPI.createJobFile(wrf);
            nfc2IOAPI.run();


            System.out.println("6) Running mozart2camx!");

            MOZART2CAMX mozart2CAMX = new MOZART2CAMX(RUNDATE, RUNHOURS, RUNCITY, CORES, EMISION_INV);
            mozart2CAMX.createJobFile(wrf);
            mozart2CAMX.run();


            System.out.println("7) Running prepmegan4cmaq!");

            PREPMEGAN4CMAQ prepmegan4CMAQ = new PREPMEGAN4CMAQ(RUNDATE, RUNHOURS, RUNCITY, CORES, EMISION_INV);
            prepmegan4CMAQ.createJobFile(wrf, wrfinput, Integer.parseInt(runID));
            prepmegan4CMAQ.run();


            System.out.println("8) Running txt2ioapi!");

            TXT2IOAPI txt2IOAPI = new TXT2IOAPI(RUNDATE, RUNHOURS, RUNCITY, CORES, EMISION_INV);
            txt2IOAPI.createJobFile(wrf);
            txt2IOAPI.run();


            System.out.println("9) Running met2mgn!");

            MET2MGN met2MGN = new MET2MGN(RUNDATE, RUNHOURS, RUNCITY, CORES, EMISION_INV);
            met2MGN.createJobFile(wrf);
            met2MGN.run();


            System.out.println("10) Running emproc!");

            EMPROC emproc = new EMPROC(RUNDATE, RUNHOURS, RUNCITY, CORES, EMISION_INV);
            emproc.createJobFile(wrf);
            emproc.run();


            System.out.println("11) Running mgn2mech!");

            MGN2MECH mgn2MECH = new MGN2MECH(RUNDATE, RUNHOURS, RUNCITY, CORES, EMISION_INV);
            mgn2MECH.createJobFile(wrf);
            mgn2MECH.run();


            System.out.println("12) Running ioapi2uam!");

            IOAPI2UAM ioapi2UAM = new IOAPI2UAM(RUNDATE, RUNHOURS, RUNCITY, CORES, EMISION_INV);
            ioapi2UAM.createJobFile(wrf);
            ioapi2UAM.run();


            System.out.println("13) Running o3map!");
            O3MAP o3MAP = new O3MAP(camxSimulation);
            o3MAP.createJobFile(wrf);


            System.out.println("14) Running tuv!");

            TUV tuv = new TUV();
            tuv.createJobFile(wrf);
            tuv.run();


            System.out.println("15) Running oceanic!");

            OCEANIC oceanic = new OCEANIC();
            oceanic.createJobFile(wrf);
            oceanic.run();


            System.out.println("Finally! Running CAMx!");

            CAMx camx = new CAMx(camxSimulation, CORES);
            camx.createJobFile(wrf);
            camx.run();

        } else {
            System.out.println("Initializing CAMx simulation for Thessaloniki");

            System.out.println("Running CAMx!");

            CAMxSIMULATION camxSimulation = new CAMxSIMULATION();
            CAMxTESTCASE camxtestcase = new CAMxTESTCASE(camxSimulation, CORES);
            camxtestcase.createJobFile();
            camxtestcase.run();

        }
        System.out.println("Posting results to DSS endpoint!");

        File outputdir = new File("/opt/bin/camx/outputs_mine_new");
        File[] listOfFiles = outputdir.listFiles();

        if(listOfFiles.length > 0) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().contains(".avrg.grd01.nc")) {
                    getDATA(file.getAbsolutePath(), apiEndpoint, Integer.parseInt(runID), wrfoutputfile, isBaseline);
                    File camxfile = new File(file.getAbsolutePath());
                    File destination = new File("/mnt/data/"+runID+"/"+file.getName().replace("thess",RUNCITY));
                    try {
                        EmissInvCopy.copyFile(camxfile,destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            taskExecutionCompleted(runID, apiEndpoint);

            System.out.println("CAMx simulation finished!");
        }
        else{
            System.out.println("CAMx simulation failed!");
            taskExecutionFailed(runID, apiEndpoint,"CAMx simulation failed! Unable to find CAMx output files.");
        }
    }

    // ***************************************************
    // Reading CAMx outputs data and posts to DSS endpoint
    // ***************************************************

    private static void getDATA(String camOutputFile, String apiEndpoint, int runID, String wrfoutputfile, boolean isBaseline) {

        WRF wrf = new WRF(wrfoutputfile);
        try {
            wrf.readWRFOutput();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Output file  " + camOutputFile);
        CAMxOutputReader camxreader = null;
        camxreader = new CAMxOutputReader(camOutputFile);

        ArrayList<String> pollutants = new ArrayList<String>() {
            {
                add("O3");
                add("NO");
                add("CO");
                add("NO2");
                add("NO3");
                add("NH3");
                add("HNO3");
                add("HNO4");
                add("S02");

            }
        };

        CAMxOutputReader finalWrfreader = camxreader;
        pollutants.forEach((pollutant) -> {

            System.out.println(pollutant);
            try {
                List<CAMxOutputModel> outputdata = null;
                outputdata = finalWrfreader.readCAMxOutput(runID, pollutant, wrf);
                if (outputdata.size() > 0) {
                    Gson gson = new Gson();
                    GsonBuilder builder = new GsonBuilder();
                    gson = builder.serializeNulls().create();
                    String json = gson.toJson(outputdata);
                    byte[] bytes = new byte[0];
                    try {
                        bytes = json.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Post size = " + (bytes.length / 1048576) + "MB");
                    int statuscode;
                    if(isBaseline){
                        statuscode = jsonPostBaseline(json, apiEndpoint);
                    }
                    else{
                        statuscode = jsonPost(json, apiEndpoint);
                    }
                    System.err.println(statuscode);
                    if(statuscode!=200){
                        EventBuilder eventBuilder = new EventBuilder()
                                .withMessage("DSS status code "+statuscode+" while posting "+ pollutant+ " at run ID "+ runID+".")
                                .withLevel(Event.Level.ERROR)
                                .withLogger(Main.class.getName());
                        Sentry.capture(eventBuilder);
                    }
                } else {
                    System.out.println("Aborted posting " + pollutant);
                }
            } catch (Exception e) {
                System.err.println("Error while posting " + pollutant);
                e.printStackTrace();
            }
        });

        // PM2.5 POST

        try {
            String pollutant = "PM25";
            List<CAMxOutputModel> PM25 = null;
            CAMxPM camxpm = new CAMxPM(camOutputFile);
            PM25 = camxpm.readCAMxOutput(runID, "PM25");
            if (PM25.size() > 0) {
                Gson gson = new Gson();
                GsonBuilder builder = new GsonBuilder();
                gson = builder.serializeNulls().create();
                String json = gson.toJson(PM25);
                byte[] bytes = new byte[0];
                try {
                    bytes = json.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println("Post size = " + (bytes.length / 1048576) + "MB");
                int statuscode = jsonPost(json, apiEndpoint);
                System.err.println(statuscode);
                if(statuscode!=200){
                    EventBuilder eventBuilder = new EventBuilder()
                            .withMessage("DSS status code "+statuscode+" while posting "+ pollutant+ " at run ID "+ runID+".")
                            .withLevel(Event.Level.ERROR)
                            .withLogger(Main.class.getName());
                    Sentry.capture(eventBuilder);
                }
            } else {
                System.out.println("Aborted posting PM2.5 ");
            }
        } catch (Exception e) {
            System.err.println("Error while posting PM2.5 ");
            e.printStackTrace();
        }

        // PM10 POST

        try {
            String pollutant = "PM10";
            List<CAMxOutputModel> PM10 = null;
            CAMxPM camxpm = new CAMxPM(camOutputFile);
            PM10 = camxpm.readCAMxOutput(runID, "PM10");
            if (PM10.size() > 0) {
                Gson gson = new Gson();
                GsonBuilder builder = new GsonBuilder();
                gson = builder.serializeNulls().create();
                String json = gson.toJson(PM10);
                byte[] bytes = new byte[0];
                try {
                    bytes = json.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println("Post size = " + (bytes.length / 1048576) + "MB");
                int statuscode = jsonPost(json, apiEndpoint);
                System.err.println(statuscode);
                if(statuscode!=200){
                    EventBuilder eventBuilder = new EventBuilder()
                            .withMessage("DSS status code "+statuscode+" while posting "+ pollutant+ " at run ID "+ runID+".")
                            .withLevel(Event.Level.ERROR)
                            .withLogger(Main.class.getName());
                    Sentry.capture(eventBuilder);
                }
            } else {
                System.out.println("Aborted posting PM10");
            }
        } catch (Exception e) {
            System.err.println("Error while posting PM10");
            e.printStackTrace();
        }


    }

    // *********************************
    // JSON data post to DSS endpoint
    // *********************************

    private static int jsonPost(String json, String endpoint) {
        HttpResponse response = Unirest.post(endpoint + "/externalmodels/postoutputmodelrecords")
                .header("Content-Type", "application/json")
                .basicAuth("admin", "password")
                .body(json)
                .asEmpty();
        return response.getStatus();
    }

    // ******************************************************
    // JSON data post to DSS endpoint for baseline results
    // ******************************************************

    private static int jsonPostBaseline(String json, String endpoint) {
        HttpResponse response = Unirest.post(endpoint + "/externalmodels/postoutputmodelbaselinerecords")
                .header("Content-Type", "application/json")
                .basicAuth("admin", "password")
                .body(json)
                .asEmpty();
        return response.getStatus();
    }


    // ************************************************
    // Task execution completed post to DSS endpoint
    // ************************************************

    private static int taskExecutionCompleted(String runID, String endpoint) {
        HttpResponse response = Unirest.get(endpoint + "/externalmodels/taskexecutioncompleted/")
                .queryString("taskExecutionId", runID)
                .queryString("file", "0")
                .basicAuth("admin", "password")
                .asString();
        return response.getStatus();
    }

    // ************************************************
    // Task execution failed post to DSS endpoint
    // ************************************************

    private static int taskExecutionFailed(String runID, String endpoint, String messsage) {
        System.out.println("taskExecutionFailed: "+messsage);
        HttpResponse response = Unirest.get(endpoint + "/externalmodels/taskexecutionerror/")
                .queryString("taskExecutionId", runID)
                .queryString("error", messsage)
                .basicAuth("admin", "password")
                .asString();
        return response.getStatus();
    }

    // ****************************************************
    // Returns the city from WRF's nested grid i,j variables
    // ****************************************************

    private static String getCity(int i, int j) {
        HttpResponse<String> city = Unirest.get("https://vasilis.pw/icarus/ij-to-city.php")
                .queryString("i", i)
                .queryString("j", j)
                .asString();
        return city.getBody();
    }

    // ******************************************
    // Returns the runID from hostname (camx-10)
    // ******************************************

    private static String getrunID() {
        String hostname;
        String runID = "0";
        try {
            InetAddress ip;
            ip = InetAddress.getLocalHost();
            hostname = ip.getHostName();
            if (hostname.contains("camx") || hostname.contains("CAMX")) runID = hostname.split("-")[1];
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return runID;
    }



    // **************************************************************************************
    // Checks if WRF output, WRF input and emission inventory exist in provided locations
    // **************************************************************************************

    private static boolean checkFiles(String WRFoutput, String WRFinput, String EmissionsInventory, String runID, String endpoint) {
            File f = new File(WRFoutput);
            if (!(f.exists() && !f.isDirectory())) {
                taskExecutionFailed(runID,endpoint,"WRF output not found at: "+ WRFoutput);
                System.exit(5);
            }
            f = new File(WRFinput);
            if (!(f.exists() && !f.isDirectory())) {
                taskExecutionFailed(runID,endpoint,"WRF input not found at: "+ WRFinput);
                System.exit(5);
            }
            f = new File(EmissionsInventory);
            if (!(f.exists() && !f.isDirectory())) {
                taskExecutionFailed(runID, endpoint, "Emission inventory not found at: " + EmissionsInventory);
                System.exit(5);
            }
            return true;
    }

}

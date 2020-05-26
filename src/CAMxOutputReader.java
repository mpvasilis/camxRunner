
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ucar.multiarray.MultiArray;
import ucar.netcdf.Attribute;
import ucar.netcdf.NetcdfFile;
import ucar.netcdf.Variable;

public class CAMxOutputReader  {

    int grid_size_x;
    int grid_size_y;
    int max_time;
    private String ncfile;
    private String SIMULATION_START_DATE;
    private String SIMULATION_END_DATE;
    private Date SIMULATION_START_DATE_date;
    private Date SIMULATION_END_DATE_date;

    public CAMxOutputReader(String output) {
        ncfile=output;
    }

    public List<CAMxOutputModel> readCAMxOutput(int runID, String variable, WRF wrf) throws IOException {

        List<CAMxOutputModel> outputdata = new ArrayList<CAMxOutputModel>();

        NetcdfFile nc =  new NetcdfFile(ncfile,true);

        grid_size_x = Integer.parseInt(nc.getDimensions().get("ROW").toString().replaceAll("\\D+", ""));
        grid_size_y = Integer.parseInt(nc.getDimensions().get("COL").toString().replaceAll("\\D+", ""));
        max_time = Integer.parseInt(nc.getDimensions().get("TSTEP").toString().replaceAll("\\D+", ""));


        Variable latitude  = nc.get("latitude");
        int[] origin = new int[latitude .getRank()];
        int[] extent = latitude .getLengths();
        MultiArray latitudeMa = latitude .copyout(origin, extent);

        Variable longitude  = nc.get("longitude");
        origin = new int[longitude .getRank()];
        extent = longitude .getLengths();
        MultiArray longitudeMa = longitude .copyout(origin, extent);

        try{
            Variable ncvariable  = nc.get(variable);
            origin = new int[ncvariable .getRank()];
            extent = ncvariable .getLengths();
            MultiArray ncvariableMultiArray = ncvariable .copyout(origin, extent);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

            SIMULATION_START_DATE=ncfile.replace("/opt/bin/camx/outputs_mine_new/CAMx.thess.","");
            SIMULATION_START_DATE=SIMULATION_START_DATE.replace(".avrg.grd01.nc","");
            Date camxoutputdate = null;
            try {
                camxoutputdate = dateFormat.parse(SIMULATION_START_DATE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(camxoutputdate);

            for (int time = 0; time<max_time; time++) {
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                Date camx_this_record_date = calendar.getTime();
                for (int i = 0; i < grid_size_x; i++) {
                    for (int j = 0; j < grid_size_y; j++) {
                        double lon = longitudeMa.getDouble(new int[]{i,j});
                        double lat = latitudeMa.getDouble(new int[]{i,j});
                        double value = ncvariableMultiArray.getDouble(new int[]{time, 0, i, j});
                        double T2 = wrf.getT2Ma().getDouble(new int[]{time, 0, i, j});
                        // System.out.println("Cell " + i + "," + j + " value: " + value + " (Lat,Long: " + lat +","+lon+")");
                        CAMxOutputModel data = new CAMxOutputModel();
                        data.setId(null);
                        data.setTaskExecutionId(runID);
                        data.setCellCol(i);
                        data.setCellRow(j);
                        data.setConcentration(convert_emiss(variable.toUpperCase(),value, T2));
                        data.setLatitude(lat);
                        data.setLongitude(lon);
                        data.setPollutant(variable.toUpperCase());
                        data.setOutputDate(camx_this_record_date.getTime());
                        data.setOutputTime(time);
                        data.setGeom(null);
                        outputdata.add(data);
                    }
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return outputdata;
    }
    /*
    ** Converts from ppmv to μg/m3
    */
    public double convert_emiss(String pollutant, double value, double temp){
           double  MolecularWeight = 0.0;
           switch(pollutant)
           {
               case "NH3":
                   MolecularWeight=17.03;
                   break;
               case "CO2":
                   MolecularWeight=44.01;
                   break;
               case "CO":
                   MolecularWeight=28.01;
                   break;
               case "NO":
                   MolecularWeight=30.01;
                   break;
               case "NO2":
                   MolecularWeight=46.01;
                   break;
               case "NO3":
                   MolecularWeight=62.0049;
                   break;
               case "O3":
                   MolecularWeight=48.00;
                   break;
               case "SO2":
                   MolecularWeight= 64.06;
                   break;
               case "HNO2":
                   MolecularWeight= 47.013;
                   break;
               case "HNO3":
                   MolecularWeight= 63.01;
                   break;
               case "HNO4":
                   MolecularWeight= 79.01224;
                   break;
               default:
                   System.out.println(pollutant + " not found in list!");
           }
           double result = value * (MolecularWeight / (0.082057338 * temp));
           result = result * 1000;
           return result;

    }


}
/*
 * Copyright (c) 2020. Vasileios Balafas
 */

import ucar.multiarray.MultiArray;
import ucar.netcdf.NetcdfFile;
import ucar.netcdf.Variable;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CAMxPM {

    int grid_size_x;
    int grid_size_y;
    int max_time;
    private String ncfile;
    private String SIMULATION_START_DATE;
    private String SIMULATION_END_DATE;
    private Date SIMULATION_START_DATE_date;
    private Date SIMULATION_END_DATE_date;

    public CAMxPM(String output) {
        ncfile = output;
    }

    public List<CAMxOutputModel> readCAMxOutput(int runID, String variable) throws IOException {

        List<CAMxOutputModel> outputdata = new ArrayList<CAMxOutputModel>();

        NetcdfFile nc = new NetcdfFile(ncfile, true);

        grid_size_x = Integer.parseInt(nc.getDimensions().get("ROW").toString().replaceAll("\\D+", ""));
        grid_size_y = Integer.parseInt(nc.getDimensions().get("COL").toString().replaceAll("\\D+", ""));
        max_time = Integer.parseInt(nc.getDimensions().get("TSTEP").toString().replaceAll("\\D+", ""));

        Variable latitude = nc.get("latitude");
        int[] origin = new int[latitude.getRank()];
        int[] extent = latitude.getLengths();
        MultiArray latitudeMa = latitude.copyout(origin, extent);

        Variable longitude = nc.get("longitude");
        origin = new int[longitude.getRank()];
        extent = longitude.getLengths();
        MultiArray longitudeMa = longitude.copyout(origin, extent);


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

        SIMULATION_START_DATE = ncfile.replace("/opt/bin/camx/outputs_mine_new/CAMx.thess.", "");
        SIMULATION_START_DATE = SIMULATION_START_DATE.replace(".avrg.grd01.nc", "");
        Date camxoutputdate = null;
        try {
            camxoutputdate = dateFormat.parse(SIMULATION_START_DATE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(camxoutputdate);

        final String[] varaiblesPM25 = {"FCRS", "FPRM", "PSO4", "PNO3", "PNH4", "NA", "PCL", "PEC", "POA", "SOA1", "SOPA", "SOPB"};
        double PM25[][][][] = new double[max_time][1][grid_size_x][grid_size_y];
        double PM2510[][][][] = new double[max_time][1][grid_size_x][grid_size_y];
        double PM10[][][][] = new double[max_time][1][grid_size_x][grid_size_y];

        try {
            for (String PM25var : varaiblesPM25) {
                System.out.println(PM25var);
                for (int time = 0; time < max_time; time++) {
                    for (int i = 0; i < grid_size_x; i++) {
                        for (int j = 0; j < grid_size_y; j++) {
                            Variable ncvariable = nc.get(PM25var);
                            origin = new int[ncvariable.getRank()];
                            extent = ncvariable.getLengths();
                            MultiArray ncvariableMultiArray = ncvariable.copyout(origin, extent);
                            if (PM25var.equals("SOA1")) {
                                PM25[time][0][i][j] = PM25[time][0][i][j] + (ncvariableMultiArray.getDouble(new int[]{time, 0, i, j}) - 4);
                            } else {
                                PM25[time][0][i][j] = PM25[time][0][i][j] + ncvariableMultiArray.getDouble(new int[]{time, 0, i, j});
                            }
                        }
                    }
                }
            }
            if (variable.equals("PM10")) {
                final String[] varaiblesPM2510 = {"CCRS", "CPRM"};
                for (String PM2510var : varaiblesPM2510) {

                    for (int time = 0; time < max_time; time++) {
                        for (int i = 0; i < grid_size_x; i++) {
                            for (int j = 0; j < grid_size_y; j++) {
                                Variable ncvariable = nc.get(PM2510var);
                                origin = new int[ncvariable.getRank()];
                                extent = ncvariable.getLengths();
                                MultiArray ncvariableMultiArray = ncvariable.copyout(origin, extent);
                                PM2510[time][0][i][j] = PM2510[time][0][i][j] + ncvariableMultiArray.getDouble(new int[]{time, 0, i, j});

                            }
                        }
                    }
                }


                for (int time = 0; time < max_time; time++) {
                    for (int i = 0; i < grid_size_x; i++) {
                        for (int j = 0; j < grid_size_y; j++) {
                            PM10[time][0][i][j] = PM2510[time][0][i][j] + PM25[time][0][i][j];

                        }
                    }
                }

            }

            for (int time = 0; time < max_time; time++) {
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                Date camx_this_record_date = calendar.getTime();
                for (int i = 0; i < grid_size_x; i++) {
                    for (int j = 0; j < grid_size_y; j++) {
                        double lon = longitudeMa.getDouble(new int[]{i, j});
                        double lat = latitudeMa.getDouble(new int[]{i, j});
                        double value = 0;
                        if (variable.equals("PM25")) {
                            value = PM25[time][0][i][j];
                        } else {
                            value = PM10[time][0][i][j];
                        }
                        System.out.println("Cell " + i + "," + j + " value: " + value + " (Lat,Long: " + lat + "," + lon + ")");
                        CAMxOutputModel data = new CAMxOutputModel();
                        data.setId(null);
                        data.setTaskExecutionId(runID);
                        data.setCellCol(i);
                        data.setCellRow(j);
                        data.setConcentration(value);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputdata;
    }


}
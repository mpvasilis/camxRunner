/*
 * Copyright (c) 2020. Vasileios Balafas
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimulationDay {


    String date; //20150101
    int day; // 01
    int Julian; //2015001
    int JulianDayOnly; // 001
    boolean isLastday = false;
    int month;
    int year;

    String ChemistryParameters ;
    String PhotolyisRates  ;
    String OzoneColumn;
    String InitialConditions;
    String BoundaryConditions;
    String Emiss_Grid;

    public SimulationDay(String date) {
        this.date=date;
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date_date = dateFormat.parse(date);
            SimpleDateFormat dateFormatdd = new SimpleDateFormat("dd");
            this.day= Integer.parseInt(dateFormatdd.format(date_date)) ;
            SimpleDateFormat dateFormatMM = new SimpleDateFormat("MM");
            this.month= Integer.parseInt(dateFormatMM.format(date_date)) ;
            SimpleDateFormat dateFormatyyyy = new SimpleDateFormat("yyyy");
            this.year= Integer.parseInt(dateFormatyyyy.format(date_date)) ;
            this.Julian=Integer.parseInt(getJulianDate());
            this.JulianDayOnly=Integer.parseInt(dayswithcountStart());
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
    public String getJulianDate(){
        String date = year+dayswithcountStart();
        if(date.length()==1) date="00"+date;
        if(date.length()==2) date="0"+date;
        return date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String dayswithcountStart(){
        SimpleDateFormat myFormat = new SimpleDateFormat("yyMMdd");
        String inputString1 = year+"0101";
        String inputString2 = date;

        try {
            Date date1 = myFormat.parse(inputString1);
            Date date2 = myFormat.parse(inputString2);
            long diff = date2.getTime() - date1.getTime();
            int days = (int) (diff / (1000*60*60*24))+1;
            String days_str = Integer.toString(days);
            if(days_str.length()==1) days_str="00"+days_str;
            if(days_str.length()==2) days_str="0"+days_str;
            return days_str;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getJulian() {
        return Julian;
    }

    public void setJulian(int julian) {
        Julian = julian;
    }

    public int getJulianDayOnly() {
        return JulianDayOnly;
    }

    public void setJulianDayOnly(int julianDayOnly) {
        JulianDayOnly = julianDayOnly;
    }

    public boolean isLastday() {
        return isLastday;
    }

    public void setLastday(boolean lastday) {
        isLastday = lastday;
    }

    public String getChemistryParameters() {
        return ChemistryParameters;
    }

    public void setChemistryParameters(String chemistryParameters) {
        ChemistryParameters = chemistryParameters;
    }

    public String getPhotolyisRates() {
        return PhotolyisRates;
    }

    public void setPhotolyisRates(String photolyisRates) {
        PhotolyisRates = photolyisRates;
    }

    public String getOzoneColumn() {
        return OzoneColumn;
    }

    public void setOzoneColumn(String ozoneColumn) {
        OzoneColumn = ozoneColumn;
    }

    public String getInitialConditions() {
        return InitialConditions;
    }

    public void setInitialConditions(String initialConditions) {
        InitialConditions = initialConditions;
    }

    public String getBoundaryConditions() {
        return BoundaryConditions;
    }

    public void setBoundaryConditions(String boundaryConditions) {
        BoundaryConditions = boundaryConditions;
    }

    public String getEmiss_Grid() {
        return Emiss_Grid;
    }

    public void setEmiss_Grid(String emiss_Grid) {
        Emiss_Grid = emiss_Grid;
    }
}

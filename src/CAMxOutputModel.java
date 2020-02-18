public class CAMxOutputModel {

    public Double longitude;
    public Double latitude;
    public String pollutant;
    public Double concentration;
    public Integer cellRow;
    public Integer cellCol;
    public long outputDate;
    public Integer outputTime;
    public Object geom;
    public Object id;
    public Object taskExecutionId;
    public CAMxOutputModel() {
    }

    public Object getTaskExecutionId() {
        return taskExecutionId;
    }

    public void setTaskExecutionId(Object taskExecutionId) {
        this.taskExecutionId = taskExecutionId;
    }

    public CAMxOutputModel(Double longitude, Double latitude, String pollutant, Double concentration, Integer cell_row,
                           Integer cell_col, long output_date, Integer output_time) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.pollutant = pollutant;
        this.concentration = concentration;
        this.cellRow = cell_row;
        this.cellCol = cell_col;
        this.outputDate = output_date;
        this.outputTime = output_time;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getPollutant() {
        return pollutant;
    }

    public void setPollutant(String pollutant) {
        this.pollutant = pollutant;
    }

    public Double getConcentration() {
        return concentration;
    }

    public void setConcentration(Double concentration) {
        this.concentration = concentration;
    }

    public Integer getCellRow() {
        return cellRow;
    }

    public void setCellRow(Integer cellRow) {
        this.cellRow = cellRow;
    }

    public Integer getCellCol() {
        return cellCol;
    }

    public void setCellCol(Integer cellCol) {
        this.cellCol = cellCol;
    }

    public long getOutputDate() {
        return outputDate;
    }

    public void setOutputDate(long outputDate) {
        this.outputDate = outputDate;
    }

    public Integer getOutputTime() {
        return outputTime;
    }

    public void setOutputTime(Integer outputTime) {
        this.outputTime = outputTime;
    }

    public Object getGeom() {
        return geom;
    }

    public void setGeom(Object geom) {
        this.geom = geom;
    }


}

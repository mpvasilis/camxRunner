import java.util.ArrayList;

public class CAMxSIMULATION {

    public ArrayList<SimulationDay> simdays = new ArrayList<SimulationDay>();

    // *********************************
    // CAMx simulation advanced settings
    // *********************************

    // DEBUG

    boolean DEBUG=false;

    public boolean isBaseline = false;


    // CAMx grid size (default 40)

    public int CAMX_GRID_X=40;
    public int CAMX_GRID_Y=40;

    //CAMx Grid spacing (default 2)

    int CAMX_GRID_SPACING_X=2;
    int CAMX_GRID_SPACING_Y=2;

    // CAMX_GRID_X * CAMX_GRID_SPACING_X -> 40 * 2 = 80
    // CAMX_GRID_Y * CAMX_GRID_SPACING_Y -> 40 * 2 = 80
    // GRID 80 km x 80 km

    // CAMx RUN information
    String RUNDATE;
    String RUNHOURS;
    String RUNCITY;
    String CORES;
    String EMISION_INV;
    String wrfoutput;
    String apiEndpoint;
    String runID;


    //PREPROCESSORS DIR
    public String PREPROCESSORS_DIR= "/opt/bin/camx_preprocessors/";

    //CAMX DIRS

    public String CAMX_DIR= "/opt/bin/camx/";
    public String CAMXoutdir="/opt/bin/camx/outputs/";
    public String CAMXmetdir="/opt/bin/camx/met/";
    public String CAMXptsrcdir="/opt/bin/camx/met/";

    //SMOKE -> PIGSET

    public String ASCIIinptfile="/opt/bin/camx_preprocessors/SMOKE_4.5/data/run_vasilis/output/merge/elev.txt";
    public String CAMxbinaryptfile="/opt/bin/camx/elevated_point_sources/all.bin";

    // CAMx xo, yo
    public String xo;
    public String yo;

    //MCIP

    public String MCIPoutputdir="/opt/bin/camx_preprocessors/MCIP/output";
    public String GRIDDESCfile="/opt/bin/camx_preprocessors/MCIP/output/GRIDDESC";

    // PREPMEGAN4CAMQ

    public String LAI_input_dir="/opt/bin/camx_preprocessors/MEGAN/prepmegan4cmaq_2014-06-02/input_global";

    // CAMX input files

    public String ChemistryParameters = "CAMx6.5.chemparam.CB6r4_CF_SOAP_ISORROPIA";
    public String PhotolyisRates      = "tuv.do";
    public String OzoneColumn         = "o3.01012015.txt";
    public String InitialConditions   = "ic.cb6r2.20150101.hr0.bin";
    public String BoundaryConditions  = "bc.cb6r2.201501${CAL}.bin";


    //Run command
    public String RUNC_CMD="/bin/csh";
    public String RUNC_CMD_DEBUG="/bin/csh -V";


    //***************************************
    // Getters and setters
    //***************************************


    public boolean isDEBUG() {
        return DEBUG;
    }

    public void setDEBUG(boolean DEBUG) {
        this.DEBUG = DEBUG;
    }

    public String getRUNC_CMD_DEBUG() {
        return RUNC_CMD_DEBUG;
    }

    public void setRUNC_CMD_DEBUG(String RUNC_CMD_DEBUG) {
        this.RUNC_CMD_DEBUG = RUNC_CMD_DEBUG;
    }

    public String getRUNC_CMD() {
        return RUNC_CMD;
    }

    public void setRUNC_CMD(String RUNC_CMD) {
        this.RUNC_CMD = RUNC_CMD;
    }

    public int getCAMX_GRID_SPACING_X() {
        return CAMX_GRID_SPACING_X;
    }

    public void setCAMX_GRID_SPACING_X(int CAMX_GRID_SPACING_X) {
        this.CAMX_GRID_SPACING_X = CAMX_GRID_SPACING_X;
    }

    public int getCAMX_GRID_SPACING_Y() {
        return CAMX_GRID_SPACING_Y;
    }

    public void setCAMX_GRID_SPACING_Y(int CAMX_GRID_SPACING_Y) {
        this.CAMX_GRID_SPACING_Y = CAMX_GRID_SPACING_Y;
    }

    public String getPREPROCESSORS_DIR() {
        return PREPROCESSORS_DIR;
    }

    public void setPREPROCESSORS_DIR(String PREPROCESSORS_DIR) {
        this.PREPROCESSORS_DIR = PREPROCESSORS_DIR;
    }

    public String getCAMX_DIR() {
        return CAMX_DIR;
    }

    public void setCAMX_DIR(String CAMX_DIR) {
        this.CAMX_DIR = CAMX_DIR;
    }

    public String getMCIPoutputdir() {
        return MCIPoutputdir;
    }

    public void setMCIPoutputdir(String MCIPoutputdir) {
        this.MCIPoutputdir = MCIPoutputdir;
    }

    public String getXo() {
        return xo;
    }

    public void setXo(String xo) {
        this.xo = xo;
    }

    public String getYo() {
        return yo;
    }

    public void setYo(String yo) {
        this.yo = yo;
    }

    public String getLAI_input_dir() {
        return LAI_input_dir;
    }

    public void setLAI_input_dir(String LAI_input_dir) {
        this.LAI_input_dir = LAI_input_dir;
    }

    public CAMxSIMULATION() {
    }

    public CAMxSIMULATION(String RUNDATE, String RUNHOURS, String RUNCITY, String CORES, String EMISION_INV, String wrfoutput) {
        this.RUNDATE = RUNDATE;
        this.RUNHOURS = RUNHOURS;
        this.RUNCITY = RUNCITY;
        this.CORES = CORES;
        this.EMISION_INV = EMISION_INV;
        this.wrfoutput = wrfoutput;

    }

    public CAMxSIMULATION(String RUNDATE, String RUNHOURS, String RUNCITY, String CORES, String EMISION_INV, String wrfoutput, int CAMX_GRID_X, int CAMX_GRID_Y) {
        this.RUNDATE = RUNDATE;
        this.RUNHOURS = RUNHOURS;
        this.RUNCITY = RUNCITY;
        this.CORES = CORES;
        this.EMISION_INV = EMISION_INV;
        this.wrfoutput = wrfoutput;
        this.CAMX_GRID_X=CAMX_GRID_X;
        this.CAMX_GRID_Y=CAMX_GRID_Y;
    }

    public int getCAMX_GRID_X() {
        return CAMX_GRID_X;
    }

    public String getASCIIinptfile() {
        return ASCIIinptfile;
    }

    public void setASCIIinptfile(String ASCIIinptfile) {
        this.ASCIIinptfile = ASCIIinptfile;
    }

    public String getCAMxbinaryptfile() {
        return CAMxbinaryptfile;
    }

    public void setCAMxbinaryptfile(String CAMxbinaryptfile) {
        this.CAMxbinaryptfile = CAMxbinaryptfile;
    }

    public String getGRIDDESCfile() {
        return GRIDDESCfile;
    }

    public void setGRIDDESCfile(String GRIDDESCfile) {
        this.GRIDDESCfile = GRIDDESCfile;
    }

    public void setCAMX_GRID_X(int CAMX_GRID_X) {
        this.CAMX_GRID_X = CAMX_GRID_X;
    }

    public int getCAMX_GRID_Y() {
        return CAMX_GRID_Y;
    }

    public void setCAMX_GRID_Y(int CAMX_GRID_Y) {
        this.CAMX_GRID_Y = CAMX_GRID_Y;
    }

    public String getRUNDATE() {
        return RUNDATE;
    }

    public void setRUNDATE(String RUNDATE) {
        this.RUNDATE = RUNDATE;
    }

    public String getRUNHOURS() {
        return RUNHOURS;
    }

    public void setRUNHOURS(String RUNHOURS) {
        this.RUNHOURS = RUNHOURS;
    }

    public String getRUNCITY() {
        return RUNCITY;
    }

    public void setRUNCITY(String RUNCITY) {
        this.RUNCITY = RUNCITY;
    }

    public String getCORES() {
        return CORES;
    }

    public void setCORES(String CORES) {
        this.CORES = CORES;
    }

    public String getEMISION_INV() {
        return EMISION_INV;
    }

    public void setEMISION_INV(String EMISION_INV) {
        this.EMISION_INV = EMISION_INV;
    }

    public String getWrfoutput() {
        return wrfoutput;
    }

    public void setWrfoutput(String wrfoutput) {
        this.wrfoutput = wrfoutput;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getRunID() {
        return runID;
    }

    public void setRunID(String runID) {
        this.runID = runID;
    }

    public String getCAMXoutdir() {
        return CAMXoutdir;
    }

    public void setCAMXoutdir(String CAMXoutdir) {
        this.CAMXoutdir = CAMXoutdir;
    }

    public String getCAMXmetdir() {
        return CAMXmetdir;
    }

    public void setCAMXmetdir(String CAMXmetdir) {
        this.CAMXmetdir = CAMXmetdir;
    }

    public String getCAMXptsrcdir() {
        return CAMXptsrcdir;
    }

    public void setCAMXptsrcdir(String CAMXptsrcdir) {
        this.CAMXptsrcdir = CAMXptsrcdir;
    }

    public ArrayList<SimulationDay> getSimdays() {
        return simdays;
    }

    public void setSimdays(ArrayList<SimulationDay> simdays) {
        this.simdays = simdays;
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
}

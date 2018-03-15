package com.spicejet.util;

/**
 * Contains all the constants required in application.
 */
public class Constants {
	public static final String PRIMARY_KEY = "app.primaryKey";
	public static final String SESSION_MANAGER = "app.SessionManager.url";
	public static final String BOOKING_MANAGER = "app.BookingManager.url";
	public static final String OPERATION_MANAGER = "app.OperationManager.url";
	public static final String DOMAIN_CODE = "app.DomainCode";
	public static final String AGENT_NAME = "app.AgentName";
	public static final String PASSWORD = "app.Password";

	public static final int CONTRACT_VERSION = 9;
	public static final String ADULT_TYPE = "ADT";
	public static final Object CHECKED_IN = "CheckedIn";

	public static final String DEVELOPMENT_ENVIRONMENT = "dev";
	public static final String PRODUCTION_ENVIRONMENT = "prod";

	public static final String PECTABS_FILE = "content.pectab.descriptor";
	public static final String BOARDING_PASS_TEMPLATE_FILE = "boarding.pass.template.file";
    public static final String BAG_TAG_TEMPLATE_FILE = "bag.tag.template.file";
    public static final String BAG_TAG_MULTI_SEG = "multi.seg.bag.tag.template.file";
    public static final String IATA_BOARDING_PASS_BARCODE_STREAM_TEMPLATE_FILE = "iata.boarding.pass.barcode.template.file";
    public static final String BOARDING_PASS_HTML_TEMPLATE_FILE = "boarding.pass.html.template.file";
    public static final String BAG_TAG_HTML_TEMPLATE_FILE = "bag.tag.html.template.file";

	public static final String BP_DATE_FORMAT = "bp.date.format";
	public static final String BP_TIME_FORMAT = "bp.time.format";

    public static final String BLANK_SPACE = " ";
    public static final String BACK_SLASH = "/";

    public static final String SEQ = "SEQ";
    public static final char HYPEN = '-';
    public static final String RCISSR="RCI";
    public static final String WEBCSSR = "WEBC";

	public static String[] CUSTOM_MONTHS = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT",
			"NOV", "DEC" };

	public static int DEFAULT_YEAR = 9999;

    public static final String CORBA_SUPPORTED = "app.isCorba.supported";
    
    public static final String BOOKING_HISTORY_CODE = "app.booking.history.code";
    public static final String BOOKING_HISTORY_AGENT_CODE = "app.booking.history.agent.code";
    public static final String LIMIT_OF_BOARDINGPASS_PRINTED="app.boardingpass.limit";
    public static final String BOARDINGPASS_LIMIT_ERROR="app.boardingpass.limit.error";
    public static final String DISABLE_WEB_CHECKIN_BAGGAGE="app.disable.web.checkin.baggage";
    public static final String APP_LOAD_Images="app.load.images";
    public static final String FAILED="FAILED";
    public static final String SUCCESS="SUCCESS";
    public static final String INPROGRESS="INPROGRESS";
}
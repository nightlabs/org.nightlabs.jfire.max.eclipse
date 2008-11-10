/**
 * 
 */
package org.nightlabs.jfire.scripting.print.ui.transfer.delivery;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DeliveryProcessorPrintDebugInfo {

	public static final String CAT_FETCH_DATA_FETCH_LAYOUTS_MAP = "010_FetchLayoutMap";
	public static final String CAT_FETCH_DATA_FETCH_LAYOUTS = "020_FetchLayouts";
	public static final String CAT_FETCH_DATA_SCRIPT_RESULTS = "030_ScriptResults";
	public static final String CAT_FETCH_DATA_TOTAL_TIME = "040_FetchDataTotalTime";
	public static final String CAT_PROCESS_DATA_PARSE_LAYOUT_FILE = "050_ParseLayoutFile";
	public static final String CAT_PROCESS_DATA_PREPARE_PRINT_ENGINE = "060_PreparePrintEngine";
	public static final String CAT_PROCESS_DATA_PREPARE_LAYOUT_FOR_PRINT = "070_PrepareLayoutForPrint";
	public static final String CAT_PROCESS_DATA_PRINT = "080_Print";
	public static final String CAT_TOTAL_TIME = "090_TotalTime";
	
	private volatile Map<String, Long> category2Times = new HashMap<String, Long>();
	
	private final boolean doMeasure;
	private final Logger logger;
	public DeliveryProcessorPrintDebugInfo() {
		logger = Logger.getLogger(DeliveryProcessorPrintDebugInfo.class);
		doMeasure = logger.isDebugEnabled();
	}
	
	private void _addTime(String category, long time) {
		Long oldTime = category2Times.get(category);
		if (oldTime == null)
			category2Times.put(category, time);
		else
			category2Times.put(category, new Long(oldTime.longValue() + time));
	}
	
	private void _printDebugInformation() {
		TreeMap<String, Long> sortedMap = new TreeMap<String, Long>(category2Times);
		StringBuffer names = new StringBuffer();
		StringBuffer values = new StringBuffer();
		for (Map.Entry<String, Long> entry : sortedMap.entrySet()) {
			names.append(entry.getKey() + "\t");
			values.append(String.valueOf(entry.getValue()) + "\t");
		}
		
		logger.info("Print debug information:");
		logger.info("Category names:");
		logger.info(names.toString());
		logger.info("Category values (ms)");
		logger.info(values.toString());
	}
	
	private static DeliveryProcessorPrintDebugInfo sharedInstance;
	
	public static DeliveryProcessorPrintDebugInfo sharedInstance() {
		return sharedInstance;
	}
	
	public static void beginMeasure() {
		sharedInstance = new DeliveryProcessorPrintDebugInfo();
	}
	
	public static void addTime(String category, long time) {
		if (sharedInstance().doMeasure)
			sharedInstance()._addTime(category, time);
	}
	
	public static void print() {
		if (sharedInstance().doMeasure)
			sharedInstance()._printDebugInformation();
	}
}

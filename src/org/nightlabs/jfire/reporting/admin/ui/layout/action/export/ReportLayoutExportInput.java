/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.layout.action.export;

import java.io.File;

import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireRemoteReportEditorInput;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportLayoutExportInput extends JFireRemoteReportEditorInput {

	/**
	 * @param reportRegistryItemID
	 */
	public ReportLayoutExportInput(ReportRegistryItemID reportRegistryItemID) {
		super(reportRegistryItemID);
	}
	
	
	public File getLocalReportFile()  {
		return getLocalInput().getFile();
	}

}

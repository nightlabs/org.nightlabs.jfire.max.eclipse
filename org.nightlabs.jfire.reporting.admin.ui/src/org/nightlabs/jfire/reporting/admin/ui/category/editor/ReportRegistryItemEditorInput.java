/**
 *
 */
package org.nightlabs.jfire.reporting.admin.ui.category.editor;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.IReportRegistryItemEditorInput;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;

/**
 * {@link IEditorInput} that points to an {@link ReportRegistryItemID}.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ReportRegistryItemEditorInput
extends JDOObjectEditorInput<ReportRegistryItemID>
implements IReportRegistryItemEditorInput
{

	/**
	 * @param jdoObjectID
	 */
	public ReportRegistryItemEditorInput(ReportRegistryItemID jdoObjectID) {
		super(jdoObjectID);
	}

	@Override
	public ReportRegistryItemID getReportRegistryItemID() {
		return getJDOObjectID();
	}
}

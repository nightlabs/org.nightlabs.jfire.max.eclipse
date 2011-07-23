/**
 *
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled.editor;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.login.ui.part.ICloseOnLogoutEditorPart;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduledReportEditor extends EntityEditor implements ICloseOnLogoutEditorPart {

	/**
	 * Constant for the editor-id of this editor like defined in the extension.
	 */
	public static final String EDITOR_ID = ScheduledReportEditor.class.getName();

	public ScheduledReportEditor() {
	}

}

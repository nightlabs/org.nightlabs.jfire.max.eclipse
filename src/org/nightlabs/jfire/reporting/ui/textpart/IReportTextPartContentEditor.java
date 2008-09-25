/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.textpart;

import org.eclipse.swt.widgets.Control;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IReportTextPartContentEditor {
	void setContent(String content);
	String getContent();
	Control getControl();
}

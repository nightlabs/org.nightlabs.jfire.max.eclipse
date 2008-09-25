/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.textpart;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportTextPartContentEditorDefault 
extends XComposite 
implements IReportTextPartContentEditor {

	private Text text;
	
	/**
	 * @param parent
	 * @param style
	 */
	public ReportTextPartContentEditorDefault(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
	}

	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.textpart.IReportTextPartContentEditor#getContent()
	 */
	@Override
	public String getContent() {
		return text.getText();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.textpart.IReportTextPartContentEditor#getControl()
	 */
	@Override
	public Control getControl() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.textpart.IReportTextPartContentEditor#setContent(java.lang.String)
	 */
	@Override
	public void setContent(String content) {
		text.setText(content);
	}

}

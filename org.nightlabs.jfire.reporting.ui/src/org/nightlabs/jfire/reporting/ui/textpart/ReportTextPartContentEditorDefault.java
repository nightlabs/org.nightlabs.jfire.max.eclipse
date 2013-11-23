/**
 *
 */
package org.nightlabs.jfire.reporting.ui.textpart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
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

//	private StyledText text;
private Text text;
	/**
	 * @param parent
	 * @param style
	 */
	public ReportTextPartContentEditorDefault(Composite parent, int style) {
		super(parent, style, LayoutMode.ORDINARY_WRAPPER);
//		text = new StyledText(this, getBorderStyle() | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text = new Text(this, getBorderStyle() | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL) ;
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		adaptToToolkit();
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


	@Override
	public void addModifyListener(ModifyListener modifyListener) {
		text.addModifyListener(modifyListener);
	}


	@Override
	public void removeModifyListener(ModifyListener modifyListener) {
		text.removeModifyListener(modifyListener);
	}

}

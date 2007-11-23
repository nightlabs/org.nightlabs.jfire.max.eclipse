/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.overview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.issue.query.IssueQuery;

/**
 * @author Chairat Kongarayawetchakun 
 *
 */
public class IssueSearchComposite extends JDOQueryComposite {
	private Text issueIDText;
	private Text subjectText;
	private Text severityTypeText;
	private Text priorityText;
	private Text reporterText;
	private Text assigneeText;
	
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public IssueSearchComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public IssueSearchComposite(Composite parent, int style) {
		super(parent, style);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jdo.ui.JDOQueryComposite#createComposite(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createComposite(Composite parent) {
		Group group = new Group(this, getBorderStyle());
		group.setLayout(new GridLayout(6, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(group, SWT.NONE).setText("ID");
		issueIDText = new Text(group, SWT.NONE);
		
		new Label(group, SWT.NONE).setText("Severity");
		severityTypeText = new Text(group, SWT.NONE);
		
		new Label(group, SWT.NONE).setText("Subject");
		subjectText = new Text(group, SWT.NONE);
		
		new Label(group, SWT.NONE).setText("Priority");
		priorityText = new Text(group, SWT.NONE);
		
		new Label(group, SWT.NONE).setText("Reporter");
		reporterText = new Text(group, SWT.NONE);
		
		new Label(group, SWT.NONE).setText("Assignee");
		assigneeText = new Text(group, SWT.NONE);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jdo.ui.JDOQueryComposite#getJDOQuery()
	 */
	@Override
	public JDOQuery getJDOQuery() {
		return new IssueQuery();
	}

}

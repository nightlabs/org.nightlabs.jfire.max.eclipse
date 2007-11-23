package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;

public class IssueFilterComposite 
extends AbstractQueryFilterComposite 
{	
	private Text issueIDText;
	private Text subjectText;
	private Text severityTypeText;
	private Text priorityText;
	private Text reporterText;
	private Text assigneeText;

	public IssueFilterComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createContents(Composite parent) {
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

	@Override
	protected Class getQueryClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<JDOQueryComposite> registerJDOQueryComposites() {
		// TODO Auto-generated method stub
		return null;
	}
}

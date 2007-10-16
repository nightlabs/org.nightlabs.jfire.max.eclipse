package org.nightlabs.jfire.issuetracking.ui.issue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.XComposite;

public class CreateIssueComposite extends XComposite{
	
	public CreateIssueComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}
	
	/**
	 * Create the content for this composite.
	 * @param parent The parent composite
	 */
	protected void createComposite(Composite parent) 
	{
		getGridLayout().numColumns = 1;
		
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 2;
		mainComposite.getGridData().horizontalAlignment = GridData.HORIZONTAL_ALIGN_FILL;
		
		
		Group reportGroup = new Group(mainComposite, SWT.NONE);
		reportGroup.setText("Report");
		
		Group toGroup = new Group(mainComposite, SWT.NONE);
		toGroup.setText("To");
	}
}

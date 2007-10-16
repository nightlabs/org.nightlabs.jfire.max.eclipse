package org.nightlabs.jfire.issuetracking.ui.overview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;

/**
 * @author Chairat Kongarayawetchakun chairatk [at] NightLabs [dot] de
 */
public class IssueView 
extends LSDViewPart 
{
	public static String VIEW_ID = IssueView.class.getName();
	
	private IssueComposite issueComposite = null;
	public void createPartContents(Composite parent) 
	{
		issueComposite = new IssueComposite(parent, SWT.NONE);
		if (parent.getLayout() instanceof FillLayout) {			
			issueComposite.setLayoutData(new Object());
		}
		if (parent.getLayout() instanceof GridLayout) {
			issueComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		}
	}
}

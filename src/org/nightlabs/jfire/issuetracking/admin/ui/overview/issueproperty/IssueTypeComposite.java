package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;

public class IssueTypeComposite
extends XComposite {

	public IssueTypeComposite(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		createComposite();
	}

	/**
	 * Create the content for this composite.
	 * @param parent The parent composite
	 */
	protected void createComposite() 
	{
		IssueTypeTable issueTypeTable = new IssueTypeTable(this, SWT.NONE);
	}
}
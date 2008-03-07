package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.id.IssueTypeID;

public class IssueTypeCategoryComposite
extends XComposite {

	public IssueTypeCategoryComposite(Composite parent, int style) {
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
		
		issueTypeTable.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				IssueType issueType = (IssueType)s.getFirstElement();
				try {
					RCPUtil.openEditor(new IssueTypeEditorInput((IssueTypeID)JDOHelper.getObjectId(issueType)),
							IssueTypeEditor.EDITOR_ID);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		
	}
}
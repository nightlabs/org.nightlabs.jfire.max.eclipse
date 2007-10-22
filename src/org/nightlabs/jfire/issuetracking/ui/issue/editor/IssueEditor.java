package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.jfire.base.ui.overview.OverviewEntryEditor;


public class IssueEditor extends OverviewEntryEditor{
	public static final String EDITOR_ID = IssueEditor.class.getName();
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
	}
}

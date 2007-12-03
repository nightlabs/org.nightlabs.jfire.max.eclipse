package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.entity.editor.EntityEditor;

public class IssueTypeEditor extends EntityEditor{
	public static final String EDITOR_ID = IssueTypeEditor.class.getName();
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
	}
}
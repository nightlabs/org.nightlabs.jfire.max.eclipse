package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;

public class IssueTypeEditorPageController extends EntityEditorPageController{

	public IssueTypeEditorPageController(EntityEditor editor)
	{
		super(editor);
	}
	
	public void doLoad(IProgressMonitor monitor) {
		monitor.beginTask("Loading Issue Types....", 100);
		
		monitor.done();
		fireModifyEvent(null, null);
	}

	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}
}

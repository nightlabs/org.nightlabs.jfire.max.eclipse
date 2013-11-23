package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import javax.security.auth.login.LoginException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.ui.overview.AbstractEntry;
import org.nightlabs.jfire.base.ui.overview.EntryFactory;
import org.nightlabs.jfire.base.ui.overview.EntryViewer;
import org.nightlabs.jfire.base.ui.overview.OverviewEntryEditorInput;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypeEntry 
extends AbstractEntry
{
	private IssueTypeCategoryComposite composite;
	
	public IssueTypeEntry(EntryFactory entryFactory) {
		super(entryFactory);
	}
	
	public Composite createComposite(Composite parent) {
		try {
			Login.getLogin();
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
		return new IssueTypeCategoryComposite(parent, SWT.NONE);
	}

	public EntryViewer createEntryViewer() {
		// TODO Auto-generated method stub
		return null;
	}

	public Composite getComposite() {
		return composite;
	}

	public IWorkbenchPart handleActivation()
	{
		try
		{
			return RCPUtil.openEditor(new OverviewEntryEditorInput(this), IssueTypeEditor.EDITOR_ID);
		}
		catch (PartInitException e)
		{
			throw new RuntimeException(e);
		}	
	}

}

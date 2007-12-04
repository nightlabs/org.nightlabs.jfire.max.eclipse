package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import javax.security.auth.login.LoginException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.overview.AbstractEntry;
import org.nightlabs.jfire.base.ui.overview.EntryFactory;
import org.nightlabs.jfire.base.ui.overview.EntryViewer;

public class IssueTypeEntry extends AbstractEntry{

	private IssueTypeComposite composite;
	
	public IssueTypeEntry(EntryFactory entryFactory) {
		super(entryFactory);
	}
	
	public Composite createComposite(Composite parent) {
		try {
			Login.getLogin();
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
		return new IssueTypeComposite(parent, SWT.NONE);
	}

	public EntryViewer createEntryViewer() {
		// TODO Auto-generated method stub
		return null;
	}

	public Composite getComposite() {
		return composite;
	}

	public void handleActivation() {
		// TODO Auto-generated method stub
		
	}

}

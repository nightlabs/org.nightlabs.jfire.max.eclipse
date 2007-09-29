package org.nightlabs.jfire.trade.repository.editor;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.jfire.trade.resource.Messages;
import org.nightlabs.jfire.transfer.Anchor;
import org.nightlabs.jfire.transfer.id.AnchorID;

public class RepositoryEditorInput
extends JDOObjectEditorInput<AnchorID>
{
	public RepositoryEditorInput(AnchorID repositoryID)
	{
		super(repositoryID);
		setName(String.format(Messages.getString("org.nightlabs.jfire.trade.repository.editor.RepositoryEditorInput.name"), Anchor.getPrimaryKey(repositoryID.organisationID, repositoryID.anchorTypeID, repositoryID.anchorID))); //$NON-NLS-1$
	}
}

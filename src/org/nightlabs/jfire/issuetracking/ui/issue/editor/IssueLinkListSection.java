package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItemChangedListener;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkItemChangedEvent;

/* 
* @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
*/
public class IssueLinkListSection extends AbstractIssueEditorGeneralSection{

	private IssueLinkAdderComposite issueLinkAdderComposite;
	private Issue issue;
	
	public IssueLinkListSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getSection().setText("Issue Links");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());
		
		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 
		
		issueLinkAdderComposite = new IssueLinkAdderComposite(
				client, SWT.NONE);
		issueLinkAdderComposite.getGridData().grabExcessHorizontalSpace = true;
		issueLinkAdderComposite.addIssueLinkTableItemListener(new IssueLinkTableItemChangedListener() {
			public void issueLinkItemChanged(
					IssueLinkItemChangedEvent itemChangedEvent) {
				for (ObjectID objectID : issueLinkAdderComposite.getItems()) {
					controller.getIssue().addReferencedObjectID(objectID);	
				}
				markDirty();
			}
		});
		
		getSection().setClient(client);
	}
	
	@Override
	protected void doSetIssue(Issue issue) {
		this.issue = issue;

		Set<ObjectID> objectIDs = issue.getReferencedObjectIDs();
		issueLinkAdderComposite.setItems(objectIDs);
	}
}
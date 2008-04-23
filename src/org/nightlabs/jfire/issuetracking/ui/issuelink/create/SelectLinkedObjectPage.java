/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink.create;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkAdder;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkDoubleClickListener;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkDoubleClickedEvent;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class SelectLinkedObjectPage extends DynamicPathWizardPage
{
	private Composite carrier;
	private IssueLinkAdder issueLinkAdder;
	private Set<ObjectID> linkedObjectIDs = new HashSet<ObjectID>();

	public SelectLinkedObjectPage() {
		super(SelectLinkedObjectPage.class.getName(), "Select the linked object");
		setDescription("Please select the object(s)");
	}

	/**
	 * Set the currently active {@link IssueLinkAdder} or <code>null</code>.
	 *
	 * @param issueLinkAdder the {@link IssueLinkAdder} that has been selected in another wizard page or <code>null</code>, if it has been deselected.
	 */
	public void setIssueLinkAdder(final IssueLinkAdder issueLinkAdder) {
		// In case there currently is some UI in our carrier, we clean it.
		if (carrier == null)
			throw new IllegalStateException("setIssueLinkAdder(...) called before createPageContents(...)!");

		for (Control child : carrier.getChildren())
			child.dispose();

		this.issueLinkAdder = issueLinkAdder;
		linkedObjectIDs.clear();

		if (issueLinkAdder != null) {
			Composite issueLinkAdderComposite = issueLinkAdder.createComposite(carrier);
			issueLinkAdderComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			carrier.layout(true);

			issueLinkAdder.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					linkedObjectIDs.clear();
					linkedObjectIDs.addAll(issueLinkAdder.getLinkedObjectIDs());
					getContainer().updateButtons();
				}	
			});

			issueLinkAdder.addIssueLinkDoubleClickListener(new IssueLinkDoubleClickListener() {
				@Override
				public void issueLinkDoubleClicked(IssueLinkDoubleClickedEvent event) {
					getContainer().showPage(getNextPage());
				}
			});
		}

		getContainer().updateButtons();
	}

	public Set<ObjectID> getLinkedObjectIDs() {
		return linkedObjectIDs;
	}

	@Override
	public Control createPageContents(Composite parent) {
		carrier = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		return carrier;
	}

	@Override
	public boolean isPageComplete() {
		return !linkedObjectIDs.isEmpty(); 
	}

	public IssueLinkAdder getIssueLinkAdder() {
		return issueLinkAdder;
	}

	class IssueLinkTypeLabelProvider extends LabelProvider{
		@Override
		public String getText(Object element) 
		{
			if (element instanceof IssueLinkType) {
				IssueLinkType issueLinkType = (IssueLinkType) element;
				return issueLinkType.getName().getText();
			}

			return super.getText(element);
		}		
	}
}
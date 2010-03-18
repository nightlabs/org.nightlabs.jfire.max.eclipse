package org.nightlabs.jfire.personrelation.ui.tucked;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.selection.SelectionProviderProxy;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTree;

/**
 * A slightly different portrayal of nodes in the {@link PersonRelationTree}; i.e. they are 'tucked' with the idea
 * of space conservation when search results returns a long list of hits.
 *
 * See notes on "Search by Association".
 *
 * @author khaireel (at) nightlabs (dot) de
 */
public class TuckedPersonRelationTreeView extends LSDViewPart {
	private SelectionProviderProxy selectionProviderProxy = new SelectionProviderProxy();

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getSite().setSelectionProvider(selectionProviderProxy);
	}

	@Override
	public void createPartContents(Composite parent) {

		// Later on...
		// selectionProviderProxy.addRealSelectionProvider(personRelationTree);
	}
}

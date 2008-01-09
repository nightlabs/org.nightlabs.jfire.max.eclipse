package org.nightlabs.jfire.issuetracking.ui.issuelink;


import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public interface IssueLinkAdder 
{
	void init(IssueLinkHandlerFactory handlerFactory);

	IssueLinkHandlerFactory getIssueLinkHandlerFactory();
	
	Composite createComposite(Composite parent);

	Composite getComposite();

	void onDispose();

	void dispose();
	
	boolean isComplete();
	
	void addIssueLinkSelectionListener(IssueLinkSelectionListener listener);
	
	void removeIssueLinkSelectionListener(IssueLinkSelectionListener listener);
	
	Collection<String> getIssueLinkObjectIds();
}

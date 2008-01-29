package org.nightlabs.jfire.issuetracking.ui.issuelink;


import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.ObjectID;

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
	
	void addIssueLinkDoubleClickListener(IssueLinkDoubleClickListener listener);
	
	void removeIssueLinkDoubleClickListener(IssueLinkDoubleClickListener listener);
	
	Set<ObjectID> getIssueLinkObjectIds();
}

/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.accounting.issuelink;

import java.util.Collection;
import java.util.HashSet;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkAdder;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkAdder extends AbstractIssueLinkAdder {

	private IssueLinkTable issueLinkTable;
	@Override
	protected Composite doCreateComposite(Composite parent) {
		XComposite c = new XComposite(parent, SWT.NONE);
		issueLinkTable = new IssueLinkTable(c, SWT.NONE);
		
		issueLinkTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				notifyIssueLinkSelectionListeners();
			}
		});
		
		return c;
	}

	public Collection<String> getIssueLinkObjectIds() {
		Collection<String> result = new HashSet<String>();
		for(Object o : issueLinkTable.getSelectedElements()) {
			result.add(JDOHelper.getObjectId(o).toString());
		}
		return result;
	}

	public boolean isComplete() {
		if(getIssueLinkObjectIds() == null || getIssueLinkObjectIds().size() <= 0) {
			return false;
		}
		return true; 
	}
}

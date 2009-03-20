package org.nightlabs.jfire.trade.admin.ui.layout;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController;
import org.nightlabs.jfire.trade.ILayout;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

public abstract class AbstractRemoteLayoutListComposite<ID, L extends ILayout> extends XComposite {

	RemoteLayoutTable<ID, L> layoutTable;
	
	public AbstractRemoteLayoutListComposite(Composite parent, int style, boolean load) {
		super(parent, style);
		
		Label label = new Label(this, SWT.NONE);
		label.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.layout.AbstractRemoteLayoutListComposite.label")); //$NON-NLS-1$
		
		layoutTable = new RemoteLayoutTable<ID, L>(this, SWT.NONE, createActiveJDOObjectController());
		
		if (load) {
			layoutTable.load();
		}
	}
	
	public L getSelectedLayout() {
		return layoutTable.getFirstSelectedElement();
	}
	
	public void selectLayout(L layout) {
		List<L> selection = layout != null ? Collections.singletonList(layout) : new LinkedList<L>();
		layoutTable.setSelection(selection, true);
	}
	
	public RemoteLayoutTable<ID, L> getLayoutTable() {
		return layoutTable;
	}
	
	protected abstract ActiveJDOObjectController<ID, L> createActiveJDOObjectController();
}

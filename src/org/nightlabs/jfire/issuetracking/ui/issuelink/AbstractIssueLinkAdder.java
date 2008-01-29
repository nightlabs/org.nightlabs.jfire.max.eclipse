/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public abstract class AbstractIssueLinkAdder 
implements IssueLinkAdder 
{
	private IssueLinkHandlerFactory issueLinkHandlerFactory;
	private ListenerList selectionChangeListeners = new ListenerList();
	private ListenerList selectionDoubleClickListeners = new ListenerList();
	
	public void init(IssueLinkHandlerFactory issueLinkHandlerFactory) 
	{
		this.issueLinkHandlerFactory = issueLinkHandlerFactory;
	}
	
	public IssueLinkHandlerFactory getIssueLinkHandlerFactory() 
	{
		return issueLinkHandlerFactory;
	}
	
	private Composite composite = null;
	
	public Composite createComposite(Composite parent) {
		if (composite != null)
			throw new IllegalStateException("createComposite(...) has already been called! Have already a composite!"); //$NON-NLS-1$

		composite = doCreateComposite(parent);
		
		composite.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				((Composite)e.getSource()).removeDisposeListener(this);
				onDispose();
			}
		});

		return composite;
	}

	/**
	 * This method is called by {@link #createComposite(Composite)}. 
	 * Implement it and return a new instance
	 * of <tt>Composite</tt>.
	 *
	 * @param parent The parent <tt>Composite</tt> for the new <tt>Composite</tt>.
	 * @return The newly created <tt>Composite</tt>.
	 */
	protected abstract Composite doCreateComposite(Composite parent);
	
	public void dispose()
	{
		if (composite != null)
			composite.dispose();
	}

	public Composite getComposite()
	{
		return composite;
	}

	

	public void onDispose() 
	{
		composite = null;
	}
	
	public void addIssueLinkSelectionListener(IssueLinkSelectionListener listener) {
		selectionChangeListeners.add(listener);
	}
	
	public void removeIssueLinkSelectionListener(IssueLinkSelectionListener listener) {
		selectionChangeListeners.remove(listener);
	}
	
	protected void notifyIssueLinkSelectionListeners() {
		Object[] listeners = selectionChangeListeners.getListeners();
		IssueLinkSelectionChangedEvent evt = new IssueLinkSelectionChangedEvent(this);
		for (Object l : listeners) {
			if (l instanceof IssueLinkSelectionListener) {
				((IssueLinkSelectionListener) l).issueLinkSelectionChanged(evt);
			}
		}
	}
	
	public void addIssueLinkDoubleClickListener(IssueLinkDoubleClickListener listener) {
		selectionDoubleClickListeners.add(listener);
	}
	
	public void removeIssueLinkDoubleClickListener(IssueLinkDoubleClickListener listener) {
		selectionDoubleClickListeners.remove(listener);
	}
	
	protected void notifyIssueLinkDoubleClickListeners() {
		Object[] listeners = selectionDoubleClickListeners.getListeners();
		IssueLinkDoubleClickedEvent evt = new IssueLinkDoubleClickedEvent(this);
		for (Object l : listeners) {
			if (l instanceof IssueLinkDoubleClickListener) {
				((IssueLinkDoubleClickListener) l).issueLinkDoubleClicked(evt);
			}
		}
	}
}

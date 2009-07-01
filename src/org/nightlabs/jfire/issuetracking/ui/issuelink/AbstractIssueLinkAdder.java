/**
 *
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

/**
 * This class should be subclassed to create a composite that can contain the UI
 * for selecting the object to be linked to an issue.
 * <p>
 * Normally, The UI created in the adder composite is an instance of <link>SearchEntryViewer</link>
 * that has the seach button to do searching.
 * </p>
 *
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 */
public abstract class AbstractIssueLinkAdder
implements IssueLinkAdder
{
	private IssueLinkHandlerFactory issueLinkHandlerFactory;
	private ListenerList selectionDoubleClickListeners = new ListenerList();

	/**
	 *
	 */
	public void init(IssueLinkHandlerFactory issueLinkHandlerFactory)
	{
		this.issueLinkHandlerFactory = issueLinkHandlerFactory;
	}

	/**
	 * Gets the {@link IssueLinkHandlerFactory}.
	 * @return  the {@link IssueLinkHandlerFactory}
	 */
	public IssueLinkHandlerFactory getIssueLinkHandlerFactory()
	{
		return issueLinkHandlerFactory;
	}

	private Composite composite = null;

	/**
	 * Creates the composite and then calls the search function to show elements for choosing.
	 */
	public Composite createComposite(Composite parent) {
		if (composite != null)
			throw new IllegalStateException("createComposite(...) has already been called! Have already a composite!"); //$NON-NLS-1$

		composite = doCreateComposite(parent);
		// by default no search should be performed after having clicked just on a entry of the available
		// IssueLinkHandlers. Search can be a very expensive operation especially when no criteria is specified. (Daniel)
//		doSearch();

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

	/**
	 * This method does searching thing to choose the object to be linked.
	 * Implement it and do searching thing in its.
	 */
	protected abstract void doSearch();

	public void dispose()
	{
		if (composite != null)
			composite.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	public Composite getComposite()
	{
		return composite;
	}

	public void onDispose()
	{
		composite = null;
	}

	private ListenerList selectionChangedListeners = new ListenerList();
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}
	@Override
	public IStructuredSelection getSelection() {
		return new StructuredSelection(getLinkedObjectIDs());
	}
	@Override
	public void setSelection(ISelection selection) {
		// no-op
	}

	protected void fireSelectionChangedEvent() {
		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
		for (Object l : selectionChangedListeners.getListeners())
			((ISelectionChangedListener ) l).selectionChanged(event);
	}

	/**
	 *
	 */
	public void addIssueLinkDoubleClickListener(IssueLinkDoubleClickListener listener) {
		selectionDoubleClickListeners.add(listener);
	}

	/**
	 *
	 */
	public void removeIssueLinkDoubleClickListener(IssueLinkDoubleClickListener listener) {
		selectionDoubleClickListeners.remove(listener);
	}


	public void notifyIssueLinkDoubleClickListeners() {
		Object[] listeners = selectionDoubleClickListeners.getListeners();
		IssueLinkDoubleClickedEvent evt = new IssueLinkDoubleClickedEvent(this);
		for (Object l : listeners) {
			if (l instanceof IssueLinkDoubleClickListener) {
				((IssueLinkDoubleClickListener) l).issueLinkDoubleClicked(evt);
			}
		}
	}
}
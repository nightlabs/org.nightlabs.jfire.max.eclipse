package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.store.id.ProductTypeID;

public abstract class AbstractCellReferenceProductTypeSelector implements CellReferenceProductTypeSelector
{
	private Composite composite = null;
	private PriceConfigComposite priceConfigComposite = null;
	
	public Composite createComposite(Composite parent) {
		if (composite != null)
			throw new IllegalStateException("createComposite(...) has already been called! Have already a composite!"); //$NON-NLS-1$

		composite = _createComposite(parent);

//		composite.addDisposeListener(new DisposeListener() {
//			public void widgetDisposed(DisposeEvent e)
//			{
//				((Composite)e.getSource()).removeDisposeListener(this);
//				onDispose();
//			}
//		});

		return composite;
	}

	public void setPriceConfigComposite(PriceConfigComposite priceConfigComposite)
	{
		this.priceConfigComposite = priceConfigComposite;
	}
	public PriceConfigComposite getPriceConfigComposite()
	{
		return priceConfigComposite;
	}

	public Composite getComposite() {
		return composite;
	}
	
	protected abstract Composite _createComposite(Composite parent);

	private ListenerList selectionChangedListeners = new ListenerList();

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.add(listener);
	}
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	public ISelection getSelection()
	{
		return new StructuredSelection(getSelectedProductTypeID());
	}

	public void setSelection(ISelection selection)
	{
		// Not yet implemented, since the wizard currently only creates new cell references - it doesn't modify existing ones, yet.
		throw new UnsupportedOperationException("NYI");
	}

	protected void fireSelectionChangedEvent()
	{
		ISelection selection = getSelection();
		if (selection == null)
			throw new IllegalStateException("getSelection() must not return null!");

		// check whether the selection contains a ProductTypeID
		IStructuredSelection sel = (IStructuredSelection) selection;
		if (!sel.isEmpty() && !(sel.getFirstElement() instanceof ProductTypeID))
			throw new IllegalStateException("getSelection() returned an IStructuredSelection which contains an instance of an illegal type! Expected is " + ProductTypeID.class.getName() + " but found " + (sel.getFirstElement() == null ? null : sel.getFirstElement().getClass().getName()));

		Object[] listeners = selectionChangedListeners.getListeners();
		if (listeners.length == 0)
			return;

		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());
		for (Object l : listeners)
			((ISelectionChangedListener)l).selectionChanged(event);
	}
}

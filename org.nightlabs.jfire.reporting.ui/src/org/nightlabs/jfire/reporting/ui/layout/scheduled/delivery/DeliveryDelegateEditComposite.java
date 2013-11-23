package org.nightlabs.jfire.reporting.ui.layout.scheduled.delivery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jfire.reporting.scheduled.IScheduledReportDeliveryDelegate;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DeliveryDelegateEditComposite extends XComposite {

	private XComboComposite<IScheduledReportDeliveryDelegateEditFactory> factoriesCombo;
	private Map<String, EditComposite> editComposites = new HashMap<String, EditComposite>();
	private Composite stackComposite;
	private StackLayout stackLayout;
	
	private IDirtyStateManager dirtyStateManager;
	
	class EditComposite extends Composite {
		
		private IScheduledReportDeliveryDelegateEdit delegateEdit;
		
		public EditComposite(Composite parent, IScheduledReportDeliveryDelegateEdit delegateEdit) {
			super(parent, SWT.NONE);
			this.delegateEdit = delegateEdit;
			setLayout(XComposite.getLayout(LayoutMode.TIGHT_WRAPPER));
			Control editControl = delegateEdit.createControl(this);
			editControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		
		public IScheduledReportDeliveryDelegateEdit getDelegateEdit() {
			return delegateEdit;
		}
	}
	
	/**
	 * @param parent
	 * @param style
	 */
	public DeliveryDelegateEditComposite(Composite parent, int style, IDirtyStateManager dirtyStateManager) {
		super(parent, style);
		this.dirtyStateManager = dirtyStateManager;
		init();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutDataMode
	 */
	public DeliveryDelegateEditComposite(Composite parent, int style, LayoutDataMode layoutDataMode, IDirtyStateManager dirtyStateManager) {
		super(parent, style, layoutDataMode);
		this.dirtyStateManager = dirtyStateManager;
		init();
	}

	protected void init() {
		Label factoriesLabel = new Label(this, SWT.WRAP);
		factoriesLabel.setText("Delivery configuration");
		factoriesCombo = new XComboComposite<IScheduledReportDeliveryDelegateEditFactory>(this, SWT.READ_ONLY);
		List<IScheduledReportDeliveryDelegateEditFactory> factories = new ArrayList<IScheduledReportDeliveryDelegateEditFactory>(
				ScheduledReportDeliveryDelegateEditRegistry.sharedInstance().getFactories());
		Collections.sort(factories, new Comparator<IScheduledReportDeliveryDelegateEditFactory>() {
			@Override
			public int compare(IScheduledReportDeliveryDelegateEditFactory o1, IScheduledReportDeliveryDelegateEditFactory o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		factoriesCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				updateEditComposite();
			}
		});
		factoriesCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IScheduledReportDeliveryDelegateEditFactory) element).getName();
			}
		});
		factoriesCombo.addElements(factories);
		
		stackComposite = new Composite(this, SWT.NONE);
		stackComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		stackLayout = new StackLayout();
		stackComposite.setLayout(stackLayout);
		
		if (factories.size() > 0) {
			factoriesCombo.selectElement(factories.get(0));
			updateEditComposite();
		}
			
	}
	
	protected EditComposite updateEditComposite() {
		IScheduledReportDeliveryDelegateEditFactory factory = factoriesCombo.getSelectedElement();
		if (factory == null) {
			return null;
		}
		EditComposite editComposite = editComposites.get(factory.getId());
		if (editComposite == null) {
			IScheduledReportDeliveryDelegateEdit delegateEdit = factory.createDeliveryDelegateEdit();
			if (delegateEdit != null) {
				editComposite = new EditComposite(stackComposite, delegateEdit);
				delegateEdit.setDirtyStateManager(dirtyStateManager);
				editComposites.put(factory.getId(), editComposite);
			}
		}
		if (editComposite != null) {
			stackLayout.topControl = editComposite;		
			layoutEnvironment();
		}
		return editComposite;
	}
	
	protected void layoutEnvironment() {
		getParent().layout(true, true);
	}
	
	public void setDeliveryDelegate(IScheduledReportDeliveryDelegate deliveryDelegate) {
		List<IScheduledReportDeliveryDelegateEditFactory> factories = factoriesCombo.getElements();
		
		for (IScheduledReportDeliveryDelegateEditFactory factory : factories) {
			if (factory.canHandleDeliveryDelegate(deliveryDelegate)) {
				factoriesCombo.selectElement(factory);
				EditComposite editComposite = updateEditComposite();
				if (editComposite != null)
					editComposite.getDelegateEdit().setDeliveryDelegate(deliveryDelegate);
				return;
			} else {
				// If this is not the Edit that matches, we clear the ui
				EditComposite editComposite = editComposites.get(factory.getId());
				if (editComposite != null) {
					editComposite.getDelegateEdit().clear();
				}
			}
		}
		// If we come here no appropriate factory was found, or the delegate is null, so we cleare the current one
		EditComposite editComposite = updateEditComposite();
		if (editComposite != null) {
			
		}
	}
	
	public IScheduledReportDeliveryDelegate getDeliveryDelegate() {
		IScheduledReportDeliveryDelegateEditFactory factory = factoriesCombo.getSelectedElement();
		EditComposite editComposite = editComposites.get(factory.getId());
		if (editComposite == null) {
			return null;
		}
		return editComposite.getDelegateEdit().getScheduledReportDeliveryDelegate();
	}
}

/**
 * 
 */
package org.nightlabs.jfire.trade.ui.store.search;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.base.ui.search.ActiveStateManager;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class SaleAccessStateCombo 
extends XComposite
implements ActiveStateManager
{
	private Button activeButton = null;
	private XComboComposite<SaleAccessState> stateCombo = null;
	private SaleAccessState selectedSaleAccessState = SaleAccessState.SALEABLE;
 
	/**
	 * @param parent
	 * @param comboStyle
	 */
	public SaleAccessStateCombo(Composite parent, int comboStyle) {
		super(parent, comboStyle);
		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Group group = new Group(this, SWT.NONE);
		group.setText("Sale Access Mode");
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		activeButton = new Button(group, SWT.CHECK);
		activeButton.setText("Active");

		stateCombo = new XComboComposite<SaleAccessState>(group, SWT.READ_ONLY, new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				SaleAccessState state = (SaleAccessState) element;
				String prefix = "org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite.saleAccessState_"; //$NON-NLS-1$
				return Messages.getString(prefix + state.name());
			}
		});
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		stateCombo.setLayoutData(data);
		stateCombo.addElements(CollectionUtil.array2ArrayList(SaleAccessState.values()));
		stateCombo.selectElement(selectedSaleAccessState);
		stateCombo.setEnabled(false);
	}

	/**
	 * Returns the activeButton.
	 * @return the activeButton
	 */
	public Button getActiveButton() {
		return activeButton;
	}

	/**
	 * Returns the stateCombo.
	 * @return the stateCombo
	 */
	public XComboComposite<SaleAccessState> getStateCombo() {
		return stateCombo;
	}

	public boolean isActive() {
		return activeButton.getSelection();
	}
	
	@Override
	public void setActive(boolean active) {
		activeButton.setSelection(active);
		stateCombo.setEnabled(active);
	}

}

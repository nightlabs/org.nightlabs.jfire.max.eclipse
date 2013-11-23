package org.nightlabs.jfire.issuetimetracking.admin.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.dao.PriceFragmentTypeDAO;
import org.nightlabs.jfire.issuetimetracking.ProjectCost;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 */
public class ProjectCostSection
extends ToolBarSectionPart
{
	private CostRevenueComposite costRevenueComposite;
	private ProjectCost projectCost;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public static final String PROPERTY_KEY_CURRENCY = CostRevenueComposite.PROPERTY_KEY_CURRENCY;
	public static final String PROPERTY_KEY_COST = CostRevenueComposite.PROPERTY_KEY_COST;
	public static final String PROPERTY_KEY_REVENUE = CostRevenueComposite.PROPERTY_KEY_REVENUE;

	public ProjectCostSection(FormPage page, Composite parent) {
		super(
				page, parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE,
				"Project Cost");

		XComposite mainComposite = new XComposite(getContainer(), SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, false));

		costRevenueComposite = new CostRevenueComposite(mainComposite, SWT.NONE, true);
		costRevenueComposite.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (ignoreModifyEvents)
					return;

				if (priceFragmentType == null) {
					priceFragmentType =
						PriceFragmentTypeDAO.sharedInstance().getPriceFragmentType(PriceFragmentType.PRICE_FRAGMENT_TYPE_ID_TOTAL,
								new String[] { FetchPlan.DEFAULT},
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new NullProgressMonitor());
				}

				projectCost.setCurrency(costRevenueComposite.getSelectedCurrency());
				projectCost.getDefaultCost().setAmount(priceFragmentType, costRevenueComposite.getCost());
				projectCost.getDefaultRevenue().setAmount(priceFragmentType, costRevenueComposite.getRevenue());

				markDirty();

				propertyChangeSupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
			}
		});
	}

	private boolean ignoreModifyEvents = false;

	protected void assertSWTThread()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Thread mismatch! This method must be called on the SWT UI thread!");
	}

	private PriceFragmentType priceFragmentType;
	public void setProjectCost(ProjectCost projectCost) {
		assertSWTThread();

		ignoreModifyEvents = true;
		try {
			this.projectCost = projectCost;
			costRevenueComposite.setCurrency(projectCost.getCurrency());
			costRevenueComposite.setRevenue((int)projectCost.getDefaultRevenue().getAmount());
			costRevenueComposite.setCost((int)projectCost.getDefaultCost().getAmount());
		} finally {
			ignoreModifyEvents = false;
		}
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}
}
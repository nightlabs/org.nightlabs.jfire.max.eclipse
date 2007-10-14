package org.nightlabs.jfire.voucher.ui.detail;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.voucher.accounting.VoucherRedemption;
import org.nightlabs.jfire.voucher.dao.VoucherKeyDAO;
import org.nightlabs.jfire.voucher.store.Voucher;
import org.nightlabs.jfire.voucher.store.VoucherKey;
import org.nightlabs.jfire.voucher.ui.resource.Messages;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.Util;

public class VoucherKeyDetailComposite
extends XComposite
{
	private String voucherKeyString = null;
	private VoucherKey voucherKey;

	private Text validity;
	private Text nominalValue;
	private Text restValue;

	private VoucherRedemptionTable voucherRedemptionTable;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * The value of the property is either an instance of {@link VoucherKey} or <code>null</code>.
	 */
	public static final String PROPERTY_NAME_VOUCHER_KEY = "voucherKey"; //$NON-NLS-1$

	/**
	 * This composite fires a {@link PropertyChangeEvent} after data has been loaded. If there is no data
	 * to be loaded (because <code>null</code> has been passed as key string), it will immediately fire
	 * an event.
	 * <p>
	 * The {@link PropertyChangeListener}s will be triggered on the SWT UI thread.
	 * </p>
	 *
	 * @param propertyName Currently, there's only one propertyName supported: {@link #PROPERTY_NAME_VOUCHER_KEY}
	 * @param listener The listener to be added.
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * @see #addPropertyChangeListener(String, PropertyChangeListener)
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public VoucherKeyDetailComposite(Composite parent, int style)
	{
		super(parent, style);

		this.getGridLayout().numColumns = 2;

		new Label(this, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.voucher.ui.detail.VoucherKeyDetailComposite.validityLabel.text")); //$NON-NLS-1$
		validity = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		validity.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(this, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.voucher.ui.detail.VoucherKeyDetailComposite.nominalValueLabel.text")); //$NON-NLS-1$
		nominalValue = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		nominalValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(this, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.voucher.ui.detail.VoucherKeyDetailComposite.restValueLabel.text")); //$NON-NLS-1$
		restValue = new Text(this, SWT.BORDER | SWT.READ_ONLY);
		restValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		voucherRedemptionTable = new VoucherRedemptionTable(this);
		voucherRedemptionTable.getGridData().horizontalSpan = 2;
	}

	public static String getValidity(VoucherKey voucherKey)
	{
		return Messages.getString("org.nightlabs.jfire.voucher.ui.detail.VoucherKeyDetailComposite." + voucherKey.getValidityString()); //$NON-NLS-1$
	}

	/**
	 * This method must be called on the SWT UI Thread. It clears immediately all displayed data and then
	 * spawns a Job to load the new data. After having loaded the data (or if not loading at all, because <code>null</code> has
	 * been passed as <code>_voucherKeyString</code>), a {@link PropertyChangeEvent} is fired. See {@link #addPropertyChangeListener(String, PropertyChangeListener)}
	 * for details.
	 *
	 * @param _voucherKeyString The key of the voucher (e.g. "v327sdhj289s") or <code>null</code> to clear the composite.
	 */
	public void setVoucherKeyString(String _voucherKeyString)
	{
		if (Util.equals(this.voucherKeyString, _voucherKeyString))
			return;

		final VoucherKey oldVoucherKey = this.voucherKey;

		this.voucherKeyString = _voucherKeyString;
		this.voucherKey = null;

		if (voucherKeyString == null)
			validity.setText(""); //$NON-NLS-1$
		else
			validity.setText(Messages.getString("org.nightlabs.jfire.voucher.ui.detail.VoucherKeyDetailComposite.validity_loadingData")); //$NON-NLS-1$

		nominalValue.setText(""); //$NON-NLS-1$
		restValue.setText(""); //$NON-NLS-1$
		voucherRedemptionTable.setVoucherKey(null);

		if (voucherKeyString == null) {
			propertyChangeSupport.firePropertyChange(PROPERTY_NAME_VOUCHER_KEY, oldVoucherKey, voucherKey);
			return;
		}

		Job job = new Job(Messages.getString("org.nightlabs.jfire.voucher.ui.detail.VoucherKeyDetailComposite.loadVoucherJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor)
			{
				voucherKey = VoucherKeyDAO.sharedInstance().getVoucherKey(voucherKeyString, FETCH_GROUPS_VOUCHER_KEY, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						if (voucherKey == null) {
							validity.setText(Messages.getString("org.nightlabs.jfire.voucher.ui.detail.VoucherKeyDetailComposite.validity_keyNotFound")); //$NON-NLS-1$
						}
						else {
							validity.setText(getValidity(voucherKey));

							if (voucherKey.getNominalValue() != null)
								nominalValue.setText(NumberFormatter.formatCurrency(voucherKey.getNominalValue().getAmount(), voucherKey.getNominalValue().getCurrency()));

							if (voucherKey.getRestValue() != null)
								restValue.setText(NumberFormatter.formatCurrency(voucherKey.getRestValue().getAmount(), voucherKey.getRestValue().getCurrency()));

							voucherRedemptionTable.setVoucherKey(voucherKey);
						}

						layout(true, true);
						propertyChangeSupport.firePropertyChange(PROPERTY_NAME_VOUCHER_KEY, oldVoucherKey, voucherKey);
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	public static final String[] FETCH_GROUPS_VOUCHER_KEY = {
		FetchPlan.DEFAULT, VoucherKey.FETCH_GROUP_VOUCHER, VoucherKey.FETCH_GROUP_NOMINAL_VALUE, VoucherKey.FETCH_GROUP_REST_VALUE,
		Voucher.FETCH_GROUP_PRODUCT_LOCAL, Price.FETCH_GROUP_CURRENCY,
		VoucherKey.FETCH_GROUP_REDEMPTIONS, VoucherRedemption.FETCH_GROUP_PAYMENT, Payment.FETCH_GROUP_CURRENCY
		// , ProductLocal.FE
	};

	public String getVoucherKeyString()
	{
		return voucherKeyString;
	}

	public VoucherKey getVoucherKey()
	{
		return voucherKey;
	}
}

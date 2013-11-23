package org.nightlabs.jfire.trade.ui.articlecontainer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.base.ui.timelength.TimeLengthComposite;
import org.nightlabs.jfire.trade.config.OfferConfigModule;
import org.nightlabs.jfire.trade.ui.resource.Messages;

public class OfferCfModPrefPageComposite extends XComposite
{
	private IDirtyStateManager dirtyStateManager;
	private TimeLengthComposite expiryDurationUnfinalized;
	private TimeLengthComposite expiryDurationFinalized;

	private boolean suppressMarkDirty = false;

	public OfferCfModPrefPageComposite(Composite parent, int style, IDirtyStateManager _dirtyStateManager) {
		super(parent, style);

		this.dirtyStateManager = _dirtyStateManager;

		{
			Group group = new Group(this, SWT.NONE);
			group.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.OfferCfModPrefPageComposite.group.expiryDurationUnfinalizedOffer.text")); //$NON-NLS-1$
			group.setLayout(new GridLayout());
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Label label = new Label(group, SWT.WRAP);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			label.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.OfferCfModPrefPageComposite.label.expiryDurationUnfinalizedOffer")); //$NON-NLS-1$

			expiryDurationUnfinalized = new TimeLengthComposite(group);
			expiryDurationUnfinalized.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			expiryDurationUnfinalized.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent event) {
					if (!suppressMarkDirty)
						dirtyStateManager.markDirty();
				}
			});
		}

		{
			Group group = new Group(this, SWT.NONE);
			group.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.OfferCfModPrefPageComposite.group.expiryDurationFinalizedOffer.text")); //$NON-NLS-1$
			group.setLayout(new GridLayout());
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Label label = new Label(group, SWT.WRAP);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			label.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.OfferCfModPrefPageComposite.label.expiryDurationFinalizedOffer")); //$NON-NLS-1$

			expiryDurationFinalized = new TimeLengthComposite(group);
			expiryDurationFinalized.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			expiryDurationFinalized.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent event) {
					if (!suppressMarkDirty)
						dirtyStateManager.markDirty();
				}
			});
		}
	}

	/**
	 * Transfer the data from the config-module to the UI.
	 *
	 * @param offerConfigModule the config-module to read the data from.
	 */
	public void updatePreferencePage(OfferConfigModule offerConfigModule)
	{
		suppressMarkDirty = true;
		try {
			expiryDurationUnfinalized.setTimeLength(
					offerConfigModule.getExpiryDurationMSecUnfinalized()
			);
			expiryDurationFinalized.setTimeLength(
					offerConfigModule.getExpiryDurationMSecFinalized()
			);
		} finally {
			suppressMarkDirty = false;
		}
	}

	public void updateConfigModule(OfferConfigModule offerConfigModule)
	{
		offerConfigModule.setExpiryDurationMSecUnfinalized(
				expiryDurationUnfinalized.getTimeLength()
		);
		offerConfigModule.setExpiryDurationMSecFinalized(
				expiryDurationFinalized.getTimeLength()
		);
	}
}

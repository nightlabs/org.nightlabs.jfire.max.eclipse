package org.nightlabs.jfire.voucher.admin.ui.createvouchertype;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.voucher.accounting.VoucherPriceConfig;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.admin.ui.priceconfig.CurrencyAmountTable;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;

public class CreateVoucherPriceConfigPage
		extends WizardHopPage
{
	private I18nTextBuffer priceConfigName = new I18nTextBuffer();
	private I18nTextEditor priceConfigNameEditor;

	private CurrencyAmountTable currencyAmountTable;

	public CreateVoucherPriceConfigPage()
	{
		super(CreateVoucherPriceConfigPage.class.getName(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.CreateVoucherPriceConfigPage.title"), //$NON-NLS-1$
				SharedImages.getWizardPageImageDescriptor(VoucherAdminPlugin.getDefault(), CreateVoucherPriceConfigPage.class));
	}

	@Override
	public Control createPageContents(final Composite parent)
	{
		final XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		priceConfigNameEditor = new I18nTextEditor(page, Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.CreateVoucherPriceConfigPage.priceConfigNameEditor.caption")); //$NON-NLS-1$
		priceConfigNameEditor.setI18nText(priceConfigName);

		currencyAmountTable = new CurrencyAmountTable(page, true);
		currencyAmountTable.setMap(new HashMap<Currency, Long>());

		return page;
	}

	public VoucherPriceConfig createPriceConfig()
	{
		final VoucherPriceConfig voucherPriceConfig = new VoucherPriceConfig(null);
		voucherPriceConfig.getName().copyFrom(priceConfigName);
		final Map<Currency, Long> map = currencyAmountTable.getMap();
		for(final Map.Entry<Currency, Long> me : map.entrySet()) {
			voucherPriceConfig.setPrice(me.getKey(), me.getValue());
		}
		return voucherPriceConfig;
	}
}

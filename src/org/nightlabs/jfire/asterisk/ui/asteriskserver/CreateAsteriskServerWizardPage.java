package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.asterisk.AsteriskServer;
import org.nightlabs.jfire.asterisk.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateAsteriskServerWizardPage
extends WizardHopPage
{
	private AsteriskServer asteriskServer;

	public CreateAsteriskServerWizardPage(AsteriskServer asteriskServer) {
		super(CreateAsteriskServerWizardPage.class.getName(), "Create Asterisk server");
		this.asteriskServer = asteriskServer;
	}

	private I18nTextEditor name;
	private Text callFileDirectory;
	private Text internationalCallPrefix;

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.GRID_DATA);

		name = new I18nTextEditor(mainComposite, Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.CreateAsteriskServerWizardPage.nameLabel.text")); //$NON-NLS-1$
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label callFileDirectoryLabel = new Label(mainComposite, SWT.NONE);
		callFileDirectoryLabel.setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.CreateAsteriskServerWizardPage.callFileDirectoryLabel.text")); //$NON-NLS-1$
		callFileDirectoryLabel.setToolTipText(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.CreateAsteriskServerWizardPage.callFileDirectoryLabel.toolTipText")); //$NON-NLS-1$
		callFileDirectory = new Text(mainComposite, XComposite.getBorderStyle(mainComposite));
		callFileDirectory.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		callFileDirectory.setToolTipText(callFileDirectoryLabel.getToolTipText());

		Label internationalCallPrefixLabel = new Label(mainComposite, SWT.NONE);
		internationalCallPrefixLabel.setText(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.CreateAsteriskServerWizardPage.internationalCallPrefixLabel.text")); //$NON-NLS-1$
		internationalCallPrefixLabel.setToolTipText(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.CreateAsteriskServerWizardPage.internationalCallPrefixLabel.toolTipText")); //$NON-NLS-1$
		internationalCallPrefix = new Text(mainComposite, XComposite.getBorderStyle(mainComposite));
		internationalCallPrefix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		internationalCallPrefix.setToolTipText(internationalCallPrefixLabel.getToolTipText());

		return mainComposite;
	}

	@Override
	public void onShow() {
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	private WizardHopPage optionalPage;

	@Override
	public boolean isPageComplete() {
		boolean result = true;
		return result;
	}
}
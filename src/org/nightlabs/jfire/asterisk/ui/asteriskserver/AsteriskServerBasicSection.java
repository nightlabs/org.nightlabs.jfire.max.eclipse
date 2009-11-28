package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jfire.asterisk.AsteriskServer;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 */
public class AsteriskServerBasicSection
extends RestorableSectionPart
{
	private I18nTextEditor name;
	private Text callFileDirectory;
	private Text internationalCallPrefix;

	private boolean ignoreModifyEvents = false;

	public AsteriskServerBasicSection(FormPage page, Composite parent) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	private void createClient(Section section, FormToolkit toolkit) {
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setText("Basic settings");

		Composite container = EntityEditorUtil.createCompositeClient(toolkit, section, 1);

		ModifyListener markDirtyModifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (!ignoreModifyEvents)
					markDirty();
			}
		};

		name = new I18nTextEditor(container, "Name");
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		name.addModifyListener(markDirtyModifyListener);

		Label callFileDirectoryLabel = new Label(container, SWT.NONE);
		callFileDirectoryLabel.setText("Call file directory");
		callFileDirectoryLabel.setToolTipText("The directory on the server into which the call files will be placed. This directory should be the one which Asterisk scans as outgoing spool directory (default is '/var/spool/asterisk/outgoing').");
		callFileDirectory = new Text(container, XComposite.getBorderStyle(container));
		callFileDirectory.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		callFileDirectory.setToolTipText(callFileDirectoryLabel.getToolTipText());
		callFileDirectory.addModifyListener(markDirtyModifyListener);

		Label internationalCallPrefixLabel = new Label(container, SWT.NONE);
		internationalCallPrefixLabel.setText("International call prefix");
		internationalCallPrefixLabel.setToolTipText("The prefix which must be dialed for international connections before the country code. Usually, this is '00', but some countries use other prefixes.");
		internationalCallPrefix = new Text(container, XComposite.getBorderStyle(container));
		internationalCallPrefix.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		internationalCallPrefix.setToolTipText(internationalCallPrefixLabel.getToolTipText());
		internationalCallPrefix.addModifyListener(markDirtyModifyListener);
	}

	@Override
	public boolean setFormInput(Object input) {
		this.asteriskServer = (AsteriskServer) input;
		return super.setFormInput(input);
	}

	@Override
	public void refresh() {
		ignoreModifyEvents = true;
		try {
			if (asteriskServer != null) {
				name.setI18nText(asteriskServer.getName(), EditMode.DIRECT);
				callFileDirectory.setText(asteriskServer.getCallFileDirectory());
				internationalCallPrefix.setText(asteriskServer.getInternationalCallPrefix());
			}
			super.refresh();
		} finally {
			ignoreModifyEvents = false;
		}
	}

	@Override
	public void commit(boolean onSave) {
		if (asteriskServer != null) {
			asteriskServer.setCallFileDirectory(callFileDirectory.getText());
			asteriskServer.setInternationalCallPrefix(internationalCallPrefix.getText());
		}
		super.commit(onSave);
	}

	private AsteriskServer asteriskServer;
}
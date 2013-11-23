package org.nightlabs.jfire.trade.ui.repository.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.base.ui.language.I18nTextEditorTable;
import org.nightlabs.base.ui.language.II18nTextEditor;
import org.nightlabs.base.ui.language.ModificationFinishedEvent;
import org.nightlabs.base.ui.language.ModificationFinishedListener;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.jfire.store.Repository;
import org.nightlabs.jfire.trade.ui.resource.Messages;

class RepositoryGeneralSection
extends RestorableSectionPart
{
	private Repository repository;

	private II18nTextEditor repositoryName;

	public RepositoryGeneralSection(FormPage page, Composite parent)
	{
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		getSection().setText(Messages.getString("org.nightlabs.jfire.trade.ui.repository.editor.RepositoryGeneralSection.text")); //$NON-NLS-1$
		getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		repositoryName = new I18nTextEditorTable(client, Messages.getString("org.nightlabs.jfire.trade.ui.repository.editor.RepositoryGeneralSection.repositoryNameEditor.title")); //$NON-NLS-1$
		repositoryName.addModificationFinishedListener(new ModificationFinishedListener() {
			public void modificationFinished(ModificationFinishedEvent event)
			{
				markDirty();
			}
		});
		getSection().setClient(client);
	}

	/**
	 * This method is called on the UI thread and passes the working-copy of the current <code>Repository</code>
	 * to this section. All sections within the same page share this same {@link Repository} instance.
	 *
	 * @param repository the working copy
	 */
	public void setRepository(Repository repository)
	{
		this.repository = repository;

		repositoryName.setI18nText(repository.getName(), EditMode.DIRECT);
	}
}

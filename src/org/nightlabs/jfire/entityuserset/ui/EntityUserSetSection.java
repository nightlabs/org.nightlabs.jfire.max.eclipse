package org.nightlabs.jfire.entityuserset.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.action.InheritanceAction;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.entityuserset.id.EntityUserSetID;
import org.nightlabs.util.Util;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class EntityUserSetSection<Entity> 
extends ToolBarSectionPart 
{
	private I18nTextEditor name;
	private I18nTextEditor description;
	private Label nameLabel;
	private Label descriptionLabel;
	private EntityUserSetPageControllerHelper<Entity> entityUserSetPageControllerHelper;

	public EntityUserSetSection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE, 
				"EntityUserSet Details");
		((GridData)getSection().getLayoutData()).grabExcessVerticalSpace = false;

		Composite wrapper = new XComposite(getContainer(), SWT.NONE);
		wrapper.setLayout(new GridLayout(2, false));

		nameLabel = new Label(wrapper, SWT.NONE);
		nameLabel.setText("Name");
		name = new I18nTextEditor(wrapper);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		name.addModifyListener(markDirtyModifyListener);

		descriptionLabel = new Label(wrapper, SWT.NONE);
		descriptionLabel.setText("Description");
		GridData gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		descriptionLabel.setLayoutData(gd);
		description = new I18nTextEditorMultiLine(wrapper, name.getLanguageChooser());
		description.setLayoutData(new GridData(GridData.FILL_BOTH));
		description.addModifyListener(markDirtyModifyListener);

		assignEntityUserSetAction.setEnabled(false);
		getToolBarManager().add(assignEntityUserSetAction);
		inheritAction.setEnabled(false);
		getToolBarManager().add(inheritAction);
		updateToolBarManager();

		name.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				setEntityUserSetPageControllerHelper(null);
			}
		});

		setEnabled(false);
	}

	private ModifyListener markDirtyModifyListener = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent arg0) {
			markDirty();
		}
	};
	
	private Action assignEntityUserSetAction = new Action() {
		{
			setText("Assign");
			setToolTipText("Assign");
			setImageDescriptor(SharedImages.getSharedImageDescriptor(Activator.getDefault(), 
					EntityUserSetSection.class, "Assign", ImageFormat.gif));
		}

		@Override
		public void run() {
			if (entityUserSetPageControllerHelper == null)
				return;

			final AssignEntityUserSetWizard<Entity> assignEntityUserSetWizard = new AssignEntityUserSetWizard<Entity>(
				entityUserSetPageControllerHelper.getEntityUserSetID(),
				entityUserSetPageControllerHelper
			);
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(getSection().getShell(), assignEntityUserSetWizard);
			if (dialog.open() == Dialog.OK) {
				Job job = new Job(String.format("Loading %s", entityUserSetPageControllerHelper.getEntityUserSetName())) {
					@Override
					protected IStatus run(org.nightlabs.progress.ProgressMonitor monitor) throws Exception {
						entityUserSetPageControllerHelper.load(
								assignEntityUserSetWizard.getEntityUserSetID(),
								assignEntityUserSetWizard.getNewEntityUserSet(),
								monitor);

						getSection().getDisplay().asyncExec(new Runnable() {
							public void run() {
								inheritAction.setChecked(assignEntityUserSetWizard.isEntityUserSetIDInherited());
								entityUserSetChanged();
								markDirty();
							}
						});

						return Status.OK_STATUS;
					}
				};
				job.setPriority(Job.SHORT);
				job.schedule();
			}
		}
	};

	private InheritanceAction inheritAction = new InheritanceAction() {
		@Override
		public void run() {
			final boolean oldEnabled = inheritAction.isEnabled();
			inheritAction.setEnabled(false);
			Job job = new Job(String.format("Loading %s", entityUserSetPageControllerHelper.getEntityUserSetName())) {
				@Override
				protected org.eclipse.core.runtime.IStatus run(org.nightlabs.progress.ProgressMonitor monitor) throws Exception {
					try {
						boolean setInherited = isChecked();
						InheritedEntityUserSetResolver<Entity> resolver = entityUserSetPageControllerHelper.getInheritedEntityUserSetResolver(); 
						EntityUserSetID parentEntityUserSetID = resolver.getInheritedEntityUserSetID(monitor);
						resolver.setEntityUserSetInherited(setInherited);
						EntityUserSetID newEntityUserSetID = setInherited ? parentEntityUserSetID : entityUserSetPageControllerHelper.getEntityUserSetID();
						if (!Util.equals(newEntityUserSetID, entityUserSetPageControllerHelper.getEntityUserSetID())) {
							entityUserSetPageControllerHelper.load(
									newEntityUserSetID, 
									null, monitor);
						}

						getSection().getDisplay().asyncExec(new Runnable() {
							public void run() {
								entityUserSetChanged();
								markDirty();
							}
						});

						return Status.OK_STATUS;
					} finally {
						if (!getSection().isDisposed()) {
							getSection().getDisplay().asyncExec(new Runnable() {
								public void run() {
									inheritAction.setEnabled(oldEnabled);
								}
							});							
						}
					}
				}
			};
			job.setPriority(Job.SHORT);
			job.schedule();
		}
	};

	/**
	 * Get the object that has been set by {@link #setEntityUserSetPageControllerHelper(EntityUserSetPageControllerHelper)} before or <code>null</code>.
	 *
	 * @return an instance of <code>EntityUserSetPageControllerHelper</code> or <code>null</code>.
	 */
	protected EntityUserSetPageControllerHelper<Entity> getEntityUserSetPageControllerHelper() {
		return entityUserSetPageControllerHelper;
	}

	/**
	 * Set the {@link EntityUserSetPageControllerHelper} that is used for the current editor page. It is possible to
	 * pass <code>null</code> in order to indicate that there is nothing to be managed right now (and thus to clear
	 * the UI).
	 *
	 * @param entityUserSetPageControllerHelper an instance of <code>EntityUserSetPageControllerHelper</code> or <code>null</code>.
	 */
	protected void setEntityUserSetPageControllerHelper(final EntityUserSetPageControllerHelper<Entity> entityUserSetPageControllerHelper) {
		this.entityUserSetPageControllerHelper = entityUserSetPageControllerHelper;
		getSection().getDisplay().asyncExec(new Runnable() {
			public void run() {
				assignEntityUserSetAction.setEnabled(entityUserSetPageControllerHelper != null);
				entityUserSetChanged();
				inheritAction.setEnabled(entityUserSetPageControllerHelper != null);
				inheritAction.setChecked(false);
				if (entityUserSetPageControllerHelper != null) {
					inheritAction.setEnabled(true);
					inheritAction.setChecked(entityUserSetPageControllerHelper.isEntityUserSetInitiallyInherited());
					
					String name = entityUserSetPageControllerHelper.getEntityUserSetName();
					assignEntityUserSetAction.setText(String.format("Assign %s", name));
					assignEntityUserSetAction.setToolTipText(String.format("Assign %s", name));					
				}
			}
		});
	}

	private void entityUserSetChanged() {
		if (name.isDisposed())
			return;

		if (entityUserSetPageControllerHelper == null || entityUserSetPageControllerHelper.getEntityUserSet() == null) {
			name.setI18nText(null, EditMode.DIRECT);
			description.setI18nText(null, EditMode.DIRECT);
			if (entityUserSetPageControllerHelper == null) {
				setMessage("Nothing selected");	
			}
			else {
				String name = entityUserSetPageControllerHelper.getEntityUserSetName();
				setMessage(String.format("No %s assigned", name));	
			}
			setEnabled(false);
		}
		else {
			name.setI18nText(entityUserSetPageControllerHelper.getEntityUserSet().getName(), EditMode.DIRECT);
			description.setI18nText(entityUserSetPageControllerHelper.getEntityUserSet().getDescription(), EditMode.DIRECT);
			setMessage(null);
			setEnabled(true);
		}
	}

	private void setEnabled(boolean enabled) {
		name.setEnabled(enabled);
		description.setEnabled(enabled);
		nameLabel.setEnabled(enabled);
		descriptionLabel.setEnabled(enabled);
	}	
}

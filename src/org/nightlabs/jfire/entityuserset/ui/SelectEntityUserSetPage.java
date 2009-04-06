package org.nightlabs.jfire.entityuserset.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.admin.ui.resource.Messages;
import org.nightlabs.jfire.entityuserset.EntityUserSet;
import org.nightlabs.jfire.entityuserset.dao.EntityUserSetDAO;
import org.nightlabs.jfire.entityuserset.id.EntityUserSetID;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.NLLocale;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class SelectEntityUserSetPage<Entity> 
extends WizardHopPage 
{
	public static enum Action {
		inherit,
		none,
		create,
		select
	}

	private EntityUserSetID entityUserSetID;
	private EntityUserSetPageControllerHelper<Entity> entityUserSetPageControllerHelper;
	private InheritedEntityUserSetResolver<Entity> inheritedEntityUserSetResolver;
	private Action action;
	private Button radioButtonInherit;
	private Button radioButtonNone;
	private Button radioButtonCreate;
	private I18nText newEntityUserSetName = new I18nTextBuffer();
	private I18nTextEditor newEntityUserSetNameEditor;
	private Button radioButtonSelect;
	private ListComposite<EntityUserSet> entityUserSetList;
	private EntityUserSet<Entity> inheritedEntityUserSet = null;
	private EntityUserSetID selectedEntityUserSetID;
	private EntityUserSet<Entity> newEntityUserSet = null;
	private boolean initializationFinished = false;
	
	/**
	 * @param entityUserSetID
	 * @param inheritedEntityUserSetResolver
	 */
	public SelectEntityUserSetPage(
			EntityUserSetID entityUserSetID,
			EntityUserSetPageControllerHelper<Entity> entityUserSetPageControllerHelper) 
	{
		super(SelectEntityUserSetPage.class.getName(), "Select EntityUserSet");
		this.entityUserSetID = entityUserSetID;
		this.entityUserSetPageControllerHelper = entityUserSetPageControllerHelper;
		this.inheritedEntityUserSetResolver = entityUserSetPageControllerHelper.getInheritedEntityUserSetResolver();
	}

	private void setInheritedEntityUserSetName(String entityUserSetName)
	{
		radioButtonInherit.setText(String.format(("Inherit EntityUserSet (%s)"), entityUserSetName));
	}

	@Override
	public Control createPageContents(Composite parent) 
	{
		final XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		if (inheritedEntityUserSetResolver != null) {
			radioButtonInherit = new Button(page, SWT.RADIO);
			radioButtonInherit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			radioButtonInherit.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setAction(Action.inherit);
				}
			});
			setInheritedEntityUserSetName("Loading");
		}

		radioButtonNone = new Button(page, SWT.RADIO);
		radioButtonNone.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		radioButtonNone.setText("Do not assign EntityUserSet");
		radioButtonNone.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAction(Action.none);
			}
		});

		radioButtonCreate = new Button(page, SWT.RADIO);
		radioButtonCreate.setEnabled(false); // enabling this when the AuthorityType has been loaded.
		radioButtonCreate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		radioButtonCreate.setText("Create New EntityUserSet");
		radioButtonCreate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAction(Action.create);
			}
		});

		XComposite nameComp = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		nameComp.getGridData().grabExcessVerticalSpace = false;
		nameComp.getGridLayout().numColumns = 2;
		Label nameSpacer = new Label(nameComp, SWT.NONE);
		GridData gd = new GridData();
		gd.widthHint = 32;
		gd.heightHint = 1;
		nameSpacer.setLayoutData(gd);

		newEntityUserSetNameEditor = new I18nTextEditor(nameComp);
		newEntityUserSetNameEditor.setI18nText(newEntityUserSetName, EditMode.DIRECT);
		newEntityUserSetNameEditor.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				setAction(Action.create);
			}
		});
		newEntityUserSetNameEditor.addFocusListener(new FocusAdapter(){
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.FocusAdapter#focusGained(org.eclipse.swt.events.FocusEvent)
			 */
			@Override
			public void focusGained(FocusEvent e) {
				setAction(Action.create);
			}
		});

		radioButtonSelect = new Button(page, SWT.RADIO);
		radioButtonSelect.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		radioButtonSelect.setText("Select EntityUserSet");
		radioButtonSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setAction(Action.select);
			}
		});

		entityUserSetList = new ListComposite<EntityUserSet>(page, SWT.NONE, (String)null, new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((EntityUserSet<Entity>)element).getName().getText();
			}
		});

		entityUserSetList.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedEntityUserSetID = (EntityUserSetID) JDOHelper.getObjectId(entityUserSetList.getSelectedElement());
				setAction(Action.select);
			}
		});

		EntityUserSet<Entity> dummy = entityUserSetPageControllerHelper.createEntityUserSet();
		dummy.getName().setText(NLLocale.getDefault().getLanguage(), "Loading");
		entityUserSetList.addElement(dummy);

		Job loadJob = new Job("Loading EntityUserSets") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.base.admin.ui.editor.authority.SelectAuthorityPage.job.loadingAuthorityTypes"), 100); //$NON-NLS-1$

				if (inheritedEntityUserSetResolver == null) {
					inheritedEntityUserSet = null;
					monitor.worked(20);
				}
				else {
					EntityUserSetID inheritedEntityUserSetID = inheritedEntityUserSetResolver.getInheritedEntityUserSetID(new SubProgressMonitor(monitor, 10));
					if (inheritedEntityUserSetID == null) {
						inheritedEntityUserSet = null;
						monitor.worked(10);
					}
					else
						inheritedEntityUserSet = EntityUserSetDAO.sharedInstance().getEntityUserSet(
								inheritedEntityUserSetID,
								new String[] { FetchPlan.DEFAULT, EntityUserSet.FETCH_GROUP_NAME},
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(monitor, 10));
				}

				final Set<EntityUserSetID> entityUserSetIDs = EntityUserSetDAO.sharedInstance().getEntityUserSetIDs(
						SecurityReflector.getUserDescriptor().getOrganisationID(),
						entityUserSetPageControllerHelper.getEntityClass(),
						new SubProgressMonitor(monitor, 10));

				final List<EntityUserSet> entityUserSets = EntityUserSetDAO.sharedInstance().getEntityUserSets(
						entityUserSetIDs,
						new String[] {FetchPlan.DEFAULT, EntityUserSet.FETCH_GROUP_NAME},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 50));
				
				Collections.sort(entityUserSets, new Comparator<EntityUserSet>() {
						@Override
						public int compare(EntityUserSet o1, EntityUserSet o2) {
							return o1.getName().getText().compareTo(o2.getName().getText());
						}
				});

				monitor.done();

				if (page.isDisposed())
					return Status.CANCEL_STATUS;

				page.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (entityUserSetList.isDisposed())
							return;

						if (inheritedEntityUserSetResolver != null)
							setInheritedEntityUserSetName(inheritedEntityUserSet == null ? "None Assigned" : inheritedEntityUserSet.getName().getText());

						radioButtonCreate.setEnabled(true); // now we have the authorityType and thus can enable this.

						entityUserSetList.removeAll();
						entityUserSetList.addElements(entityUserSets);
						initializationFinished = true;

						getContainer().updateButtons();
					}
				});

				return Status.OK_STATUS;
			}
		};
		loadJob.setPriority(Job.SHORT);
		loadJob.schedule();

		// setAction accesses the container and if we do that directly here, it causes a NPE. Hence we do it in the next event cycle.
		page.getDisplay().asyncExec(new Runnable() {
			public void run() {
				// if the inheritance option is available, we make it default - if it's not, we use "select" as default
				if (radioButtonInherit != null)
					setAction(Action.inherit);
				else
					setAction(Action.select);
			}
		});

		return page;
	}

	@Override
	public boolean isPageComplete() {
		if (action == null || !initializationFinished) // not yet initialised
			return false;

		switch (action) {
			case create:
				return !newEntityUserSetName.isEmpty();

			case inherit:
			case none:
				return true;

			case select:
				return selectedEntityUserSetID != null;

			default:
				throw new IllegalStateException("Unknown action: " + action); //$NON-NLS-1$
		}
	}

	public Action getAction() {
		return action;
	}

	private void setAction(Action action) {
		this.action = action;

		if (radioButtonInherit != null)
			radioButtonInherit.setSelection(Action.inherit == action);

		radioButtonNone.setSelection(Action.none == action);
		radioButtonCreate.setSelection(Action.create == action);
		radioButtonSelect.setSelection(Action.select == action);

		getContainer().updateButtons();
	}

	public EntityUserSetID getEntityUserSetID() {
		if (action == null)
			return null;

		switch (action) {
			case inherit:
				return (EntityUserSetID) JDOHelper.getObjectId(inheritedEntityUserSet);

			case create:
			case none:
				return null;

			case select:
				return selectedEntityUserSetID;

			default:
				throw new IllegalStateException("Unknown action: " + action); //$NON-NLS-1$
		}
	}

	public EntityUserSet<Entity> getNewEntityUserSet() {
		if (action != Action.create)
			return null;

		if (newEntityUserSet == null) {
			newEntityUserSet = entityUserSetPageControllerHelper.createEntityUserSet();
		}

		newEntityUserSet.getName().copyFrom(newEntityUserSetName);
		return newEntityUserSet;
	}

}

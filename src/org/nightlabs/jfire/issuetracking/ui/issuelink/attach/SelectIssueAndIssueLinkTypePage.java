package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueLinkTypeDAO;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListFactory;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListViewer;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class SelectIssueAndIssueLinkTypePage 
extends DynamicPathWizardPage
{
	private Button createNewIssueLinkTypeRadio;
	private Button selectExistingIssueLinkTypeRadio;

	private I18nTextEditor newIssueLinkTypeNameEditor;
	private I18nTextBuffer newIssueLinkTypeName = new I18nTextBuffer();

	private ListComposite<IssueLinkType> issueLinkTypeList;

	//------------------------------------------------
	private Button createNewIssueRadio;
	private Button selectExistingIssueRadio;
	
	private Composite issueEntryListViewerComposite;
	private IssueEntryListViewer issueEntryListViewer;

	private IssueLinkType selectedIssueLinkType;
	private Issue selectedIssue;
	private Object attachedObject;

	public static enum ActionForIssueLinkType {
		createNewIssueLinkType,
		selectExistingIssueLinkType
	}
	
	public static enum ActionForIssue {
		createNewIssue,
		selectExistingIssue
	}

	private ActionForIssueLinkType actionForIssueLinkType;

	public ActionForIssueLinkType getIssueLinkTypeAction() {
		return actionForIssueLinkType;
	}

	public void setIssueLinkTypeAction(ActionForIssueLinkType actionForIssueLinkType) {
		this.actionForIssueLinkType = actionForIssueLinkType;
		createNewIssueLinkTypeRadio.setSelection(false);
		selectExistingIssueLinkTypeRadio.setSelection(false);

		switch (actionForIssueLinkType) {
			case createNewIssueLinkType:
				createNewIssueLinkTypeRadio.setSelection(true);
				break;
			case selectExistingIssueLinkType:
				selectExistingIssueLinkTypeRadio.setSelection(true);
				break;
			default:
				throw new IllegalStateException("Unknown actionForIssueLinkType: " + actionForIssueLinkType);
		}

		getContainer().updateButtons();
	}
	
	private ActionForIssue actionForIssue;

	public ActionForIssue getIssueAction() {
		return actionForIssue;
	}

	public void setIssueAction(ActionForIssue actionForIssue) {
		this.actionForIssue = actionForIssue;
		createNewIssueRadio.setSelection(false);
		selectExistingIssueRadio.setSelection(false);

		switch (actionForIssue) {
			case createNewIssue:
				createNewIssueRadio.setSelection(true);
				issueEntryListViewerComposite.setEnabled(false);
				break;
			case selectExistingIssue:
				selectExistingIssueRadio.setSelection(true);
				issueEntryListViewerComposite.setEnabled(true);
				break;
			default:
				throw new IllegalStateException("Unknown actionForIssue: " + actionForIssue);
		}

		getContainer().updateButtons();
	}

	public SelectIssueAndIssueLinkTypePage(Object attachedObject) {
		super(SelectIssueAndIssueLinkTypePage.class.getName());
		this.attachedObject = attachedObject;
		setDescription("");
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		createNewIssueLinkTypeRadio = new Button(mainComposite, SWT.RADIO);		
		createNewIssueLinkTypeRadio.setText("Create new issue link type");
		createNewIssueLinkTypeRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createNewIssueLinkTypeRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setIssueLinkTypeAction(ActionForIssueLinkType.createNewIssueLinkType);
			}
		});

		newIssueLinkTypeNameEditor = new I18nTextEditor(mainComposite);
		newIssueLinkTypeNameEditor.setI18nText(newIssueLinkTypeName);
		newIssueLinkTypeNameEditor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		newIssueLinkTypeNameEditor.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				setIssueLinkTypeAction(ActionForIssueLinkType.createNewIssueLinkType);
			}
		});

		selectExistingIssueLinkTypeRadio = new Button(mainComposite, SWT.RADIO);
		selectExistingIssueLinkTypeRadio.setText("Select issue link type from the list");
		selectExistingIssueLinkTypeRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectExistingIssueLinkTypeRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setIssueLinkTypeAction(ActionForIssueLinkType.selectExistingIssueLinkType);
			}
		});

		Group manageRelationGroup = new Group(mainComposite, SWT.NONE);
		manageRelationGroup.setLayout(new GridLayout(1, false));
		manageRelationGroup.setText("Predefined Relations");
		manageRelationGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		XComposite manageComposite = new XComposite(manageRelationGroup, SWT.NONE);
		manageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		manageComposite.getGridLayout().numColumns = 1;

		issueLinkTypeList = new ListComposite<IssueLinkType>(manageComposite, SWT.SINGLE);
		issueLinkTypeList.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IssueLinkType)element).getName().getText();
			}
		});
		GridData gridData = new GridData(GridData.FILL_BOTH);
		issueLinkTypeList.setLayoutData(gridData);
		issueLinkTypeList.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectedIssueLinkType = issueLinkTypeList.getSelectedElement();
				setIssueLinkTypeAction(ActionForIssueLinkType.selectExistingIssueLinkType);
			}
		});

		Job job = new Job("Loading IssueLinkTypes...") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) throws Exception {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						IssueLinkTypeDAO issueLinkTypeDAO = IssueLinkTypeDAO.sharedInstance();
						Collection<IssueLinkType> issueLinkTypes = issueLinkTypeDAO.getIssueLinkTypes(
								attachedObject.getClass(), 
								new String[] {FetchPlan.DEFAULT, IssueLinkType.FETCH_GROUP_NAME}, 
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
								new SubProgressMonitor(monitor, 10));
						issueLinkTypeList.setInput(issueLinkTypes);
						
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.schedule();

		//*********************************************************************************
		createNewIssueRadio = new Button(mainComposite, SWT.RADIO);		
		createNewIssueRadio.setText("Create new issue");
		createNewIssueRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createNewIssueRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setIssueAction(ActionForIssue.createNewIssue);
			}
		});
		
		selectExistingIssueRadio = new Button(mainComposite, SWT.RADIO);
		selectExistingIssueRadio.setText("Select issue");
		selectExistingIssueRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectExistingIssueRadio.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setIssueAction(ActionForIssue.selectExistingIssue);
			}
		});
		
		issueEntryListViewer = new IssueEntryListViewer(new IssueEntryListFactory().createEntry()) {
			@Override
			protected void addResultTableListeners(AbstractTableComposite tableComposite) {
				tableComposite.addDoubleClickListener(new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent evt) {
						//do nothing!!!
					}
				});

				tableComposite.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent e) {
						selectedIssue = issueEntryListViewer.getIssueTable().getFirstSelectedElement();
						getContainer().updateButtons();
					}
				});
			}
		};	

		issueEntryListViewerComposite = issueEntryListViewer.createComposite(mainComposite);
		issueEntryListViewer.search();
		issueEntryListViewer.getComposite().setLayoutData(new GridData(GridData.FILL_BOTH));

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				setIssueLinkTypeAction(ActionForIssueLinkType.selectExistingIssueLinkType);
			}
		});

		return mainComposite;
	}

	@Override
	public boolean isPageComplete() {
		if (createNewIssueLinkTypeRadio == null) // check if UI is already created
			return false;

		if (actionForIssueLinkType != null)
			switch (actionForIssueLinkType) {
				case createNewIssueLinkType:
					return !newIssueLinkTypeName.isEmpty() && selectedIssue != null;
				case selectExistingIssueLinkType:
					return selectedIssueLinkType != null && selectedIssue != null;
				default:
					throw new IllegalStateException("Unknown actionForIssueLinkType: " + actionForIssueLinkType);
			}
		
		else 
			return false;
	}

	public Object getAttachedObject() {
		return attachedObject;
	}

	public IssueLinkType getSelectedIssueLinkType() {
		return selectedIssueLinkType;
	}

	public Issue getSelectedIssue() {
		return selectedIssue;
	}
	
	private IssueLinkType newIssueLinkType;
	
	public IssueLinkType getIssueLinkType() {
		switch (actionForIssueLinkType) {
			case createNewIssueLinkType:
				if (newIssueLinkType == null) {
					newIssueLinkType = new IssueLinkType(
							IDGenerator.getOrganisationID(),
							ObjectIDUtil.longObjectIDFieldToString(IDGenerator.nextID(IssueLinkType.class))
					);
				}

				newIssueLinkType.getName().copyFrom(newIssueLinkTypeName);
				newIssueLinkType.clearLinkedObjectClasses();
				newIssueLinkType.addLinkedObjectClass(attachedObject.getClass());
				return newIssueLinkType;

			case selectExistingIssueLinkType:
				return selectedIssueLinkType;

			default:
				throw new IllegalStateException("Unknown actionForIssueLinkType: " + actionForIssueLinkType);
		}
	}
}
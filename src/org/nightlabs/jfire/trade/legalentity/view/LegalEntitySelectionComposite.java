/**
 * 
 */
package org.nightlabs.jfire.trade.legalentity.view;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradePlugin;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.legalentity.edit.LegalEntityPersonEditor;
import org.nightlabs.jfire.trade.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.overview.action.AbstractEditArticleContainerAction;
import org.nightlabs.jfire.trade.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationAdapterCallerThread;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.Util;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class LegalEntitySelectionComposite 
extends XComposite
{
	
	private static String[] FETCH_GROUPS_LEGALENTITY = new String[] { 
		LegalEntity.FETCH_GROUP_PERSON, 
		Person.FETCH_GROUP_FULL_DATA, 
		FetchPlan.DEFAULT 
		};
	
	private LegalEntityPersonEditor leEditor;
	private Control leEditorControl;
	
	private AnonymousLegalEntityComposite anonymousLegalEntityComposite;

	private AnchorID selectedLegalEntityID = null;
	private LegalEntity selectedLegalEntity = null;
	
	private SelectionListener searchListener = new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			LegalEntity legalEntity = LegalEntitySearchCreateWizard.open(getQuickSearchText(), true);
			if (legalEntity != null) {
				setSelectedLegalEntityID(
					(AnchorID) JDOHelper.getObjectId(legalEntity)
				);
			}
		}
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};
	
	private Form form;
	private Text quickSearchText;	
	private Button quickSearchButton;
	private Section editorSection;

	/**
	 * @param parent
	 * @param style
	 */
	public LegalEntitySelectionComposite(Composite parent, int style) {
		super(parent, style);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 */
	public LegalEntitySelectionComposite(Composite parent, int style,
			LayoutMode layoutMode) {
		super(parent, style, layoutMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutDataMode
	 */
	public LegalEntitySelectionComposite(Composite parent, int style,
			LayoutDataMode layoutDataMode) {
		super(parent, style, layoutDataMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public LegalEntitySelectionComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		initGUI();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param cols
	 */
	public LegalEntitySelectionComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, int cols) {
		super(parent, style, layoutMode, layoutDataMode, cols);
		initGUI();
	}
	
	private void initGUI() {
		FormToolkit toolkit = new FormToolkit(Display.getDefault());
//		setToolkit(new NightlabsFormsToolkit(Display.getDefault()));
//		adaptToToolkit();
		form = toolkit.createForm(this);
//		form = new Form(this, SWT.BORDER);
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout formLayout = new GridLayout();
		formLayout.verticalSpacing = 10;
		formLayout.marginWidth = 10;
		formLayout.marginHeight = 10;
		form.getBody().setLayout(formLayout);
		
		Section searchSection = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR);
		searchSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchSection.setText(Messages.getString("org.nightlabs.jfire.trade.legalentity.view.LegalEntitySelectionComposite.searchSection.text")); //$NON-NLS-1$
		searchSection.setLayout(new GridLayout());
		
//		quickSearchGroup = new Composite(wrapper, SWT.BORDER);
		Composite quickSearchGroup = new XComposite(searchSection, SWT.NONE);		
//		quickSearchGroup.setText(Messages.getString("org.nightlabs.jfire.trade.legalentity.view.LegalEntitySelectionComposite.quickSearchGroup.text")); //$NON-NLS-1$
		quickSearchGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.makeColumnsEqualWidth = false;
		quickSearchGroup.setLayout(gl);
		
		quickSearchText = new Text(quickSearchGroup, getBorderStyle());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		quickSearchText.setLayoutData(gd);
		quickSearchText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				searchListener.widgetSelected(e);
			}
		});
		
		quickSearchButton = new Button(quickSearchGroup, SWT.PUSH);
		quickSearchButton.setText(Messages.getString("org.nightlabs.jfire.trade.legalentity.view.LegalEntitySelectionComposite.quickSearchButton.text")); //$NON-NLS-1$
		quickSearchButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_END));
		quickSearchButton.addSelectionListener(searchListener);

		searchSection.setClient(quickSearchGroup);
		
		editorSection = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR);
		editorSection.setLayoutData(new GridData(GridData.FILL_BOTH));
		editorSection.setText(Messages.getString("org.nightlabs.jfire.trade.legalentity.view.LegalEntitySelectionComposite.editorSection.text")); //$NON-NLS-1$
		editorSection.setLayout(new GridLayout());
		
		// don't register this listener later! otherwise we'll miss the event sent by the AbstractEditArticleContainerAction 
		// this method triggers the given listener immediately
		SelectionManager.sharedInstance().addNotificationListener(TradePlugin.ZONE_SALE, 
				LegalEntity.class, notificationListenerCustomerSelected);


		quickSearchGroup.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				SelectionManager.sharedInstance().removeNotificationListener(TradePlugin.ZONE_SALE, 
						LegalEntity.class, notificationListenerCustomerSelected);
			}
		});
		
		// Because the SelectionManager triggers the last notification immediately on registration of a new listener, this will only be null,
		// if there was no event. In this case, we select the anonymous customer.
		if (selectedLegalEntityID == null)
			setSelectedLegalEntityID(null); // this will translate null to the AnchorID of the anonymous legal entity
	}
	
	protected LegalEntity getSelectedLegalEntity() {
		return selectedLegalEntity;
	}
	
	protected void setAnonymousVisualisation() 
	{
		if (leEditorControl != null) {
			leEditor.disposeControl();
//			leEditorControl.dispose();
			leEditorControl = null;
		}
		if (anonymousLegalEntityComposite == null)
			anonymousLegalEntityComposite = new AnonymousLegalEntityComposite(editorSection, SWT.NONE);
		editorSection.setClient(anonymousLegalEntityComposite);
		editorSection.setText(Messages.getString("org.nightlabs.jfire.trade.legalentity.view.LegalEntitySelectionComposite.editorSection.anonymous")); //$NON-NLS-1$
		layout(true, true);
//		form.layout(true, true);
		redraw();
	}
	
	protected void setLegalEntityVisualisation(LegalEntity legalEntity) 
	{
		if ( anonymousLegalEntityComposite != null) {
			anonymousLegalEntityComposite.dispose();
			anonymousLegalEntityComposite = null;
		}
		if (leEditor == null)
			leEditor = new LegalEntityPersonEditor();
		if (leEditorControl == null)
			leEditorControl = leEditor.createControl(editorSection, false);
		if (legalEntity != null) {
			Person person = legalEntity.getPerson();
			leEditor.setPropertySet(person);
			leEditor.refreshControl();
		}
		editorSection.setClient(leEditorControl);
		String displayName = legalEntity.getPerson().getDisplayName();
		if (displayName == null) 
			displayName = ""; //$NON-NLS-1$
		editorSection.setText(displayName);
		layout(true, true);
//		form.layout(true, true);
		redraw();
	}

	private void setSelectedLegalEntity(LegalEntity legalEntity) 
	{
		if (legalEntity == null)
			throw new IllegalArgumentException("legalEntity must not be null!"); //$NON-NLS-1$

		AnchorID leID = (AnchorID) JDOHelper.getObjectId(legalEntity);
		if (leID == null)
			throw new IllegalArgumentException("legalEntity does not have an object-id assigned!"); //$NON-NLS-1$

		this.selectedLegalEntityID = leID;
		this.selectedLegalEntity = legalEntity;
		if (legalEntity.isAnonymous()) 
			setAnonymousVisualisation();
		else
			setLegalEntityVisualisation(legalEntity);

		AnchorID anchorID = (AnchorID) (selectedLegalEntity == null ? null : JDOHelper.getObjectId(selectedLegalEntity));
		NotificationEvent event = new NotificationEvent(
				this, TradePlugin.ZONE_SALE, anchorID, LegalEntity.class);
		SelectionManager.sharedInstance().notify(event);
	}

	public void setSelectedLegalEntityID(AnchorID selectedLegalEntityID) {
		if (editorSection == null || editorSection.isDisposed())
			return;
		
		this.selectedLegalEntity = null;
		this.selectedLegalEntityID = null;

		if (selectedLegalEntityID == null) {
			selectedLegalEntityID = AnchorID.create(
					IDGenerator.getOrganisationID(),
					LegalEntity.ANCHOR_TYPE_ID_PARTNER,
					LegalEntity.ANCHOR_ID_ANONYMOUS);
		}

		this.selectedLegalEntityID = selectedLegalEntityID;

		final AnchorID leID = selectedLegalEntityID;
		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.legalentity.view.LegalEntitySelectionComposite.loadLegalEntityJob.name")) { //$NON-NLS-1$
			@Implement
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final LegalEntity entity = LegalEntityDAO.sharedInstance().getLegalEntity(
						leID, 
						FETCH_GROUPS_LEGALENTITY,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						monitor
					);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (editorSection == null || editorSection.isDisposed())
							return;

						if (!Util.equals(leID, LegalEntitySelectionComposite.this.selectedLegalEntityID))
							return;

						setSelectedLegalEntity(entity);
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	/**
	 * Listener setting the correct {@link LegalEntity}. This is used by the 
	 * {@link AbstractEditArticleContainerAction} which opens an editor from another perspective.
	 */
	private NotificationListener notificationListenerCustomerSelected = new NotificationAdapterCallerThread() {

		public void notify(final NotificationEvent event) {
			if (LegalEntitySelectionComposite.this.equals(event.getSource()))
				return;

			if (event.getSubjects().isEmpty())
				setSelectedLegalEntityID(null);
			else
				setSelectedLegalEntityID((AnchorID) event.getFirstSubject());
		}
	};
	
	protected void openSearchWizard() {
		
	}
	
	public String getQuickSearchText() {
//		return quickSearchText.getTextControl().getText();
		return quickSearchText.getText();		
	}
}

package org.nightlabs.jfire.trade.ui.overview.repository;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.store.Repository;
import org.nightlabs.jfire.store.query.RepositoryQuery;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class RepositorySearchComposite 
extends JDOQueryComposite
{
	public RepositorySearchComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	public RepositorySearchComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	private Button ownerActiveButton = null;
	private Text ownerText = null;
	private Button ownerBrowseButton = null;
	private Button anchorTypeIDActiveButton = null;
	private XComboComposite<String> anchorTypeIDs = null;
	
	@Override
	protected void createComposite(Composite parent) 
	{
		parent.setLayout(new GridLayout(2, false));
		
		final Group ownerGroup = new Group(parent, SWT.NONE);
		ownerGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.ownerGroup.text")); //$NON-NLS-1$
		ownerGroup.setLayout(new GridLayout(2, false));	
		ownerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ownerActiveButton = new Button(ownerGroup, SWT.CHECK);
		ownerActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.ownerActiveButton.text")); //$NON-NLS-1$
		GridData vendorLabelData = new GridData(GridData.FILL_HORIZONTAL);
		vendorLabelData.horizontalSpan = 2;
		ownerActiveButton.setLayoutData(vendorLabelData);		
		ownerText = new Text(ownerGroup, SWT.BORDER);
		ownerText.setEnabled(false);
		ownerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		ownerText.addSelectionListener(ownerSelectionListener);
		ownerBrowseButton = new Button(ownerGroup, SWT.NONE);
		ownerBrowseButton.setText("Browse"); //$NON-NLS-1$
		ownerBrowseButton.addSelectionListener(ownerSelectionListener);
		ownerBrowseButton.setEnabled(false);
		ownerActiveButton.addSelectionListener(new SelectionListener(){		
			public void widgetSelected(SelectionEvent e) {
				ownerText.setEnabled(((Button)e.getSource()).getSelection());
				ownerBrowseButton.setEnabled(((Button)e.getSource()).getSelection());
			}		
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});
		
		final Group anchorTypeIDGroup = new Group(parent, SWT.NONE);
		anchorTypeIDGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.anchorTypeIdGroup.text")); //$NON-NLS-1$
		anchorTypeIDGroup.setLayout(new GridLayout());	
		anchorTypeIDGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		anchorTypeIDActiveButton = new Button(anchorTypeIDGroup, SWT.CHECK);
		anchorTypeIDActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.RepositorySearchComposite.anchorTypeIdActiveButton.text")); //$NON-NLS-1$
		anchorTypeIDActiveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		anchorTypeIDs = new XComboComposite<String>(
				anchorTypeIDGroup, SWT.BORDER, 
				new LabelProvider() {
					@Override
					public String getText(Object element) {
						return RepositoryListComposite.getAnchorTypeIDName((String)element);
					}					
				}
		);
		anchorTypeIDs.setInput(getAvailableRepositoryAnchorTypeIDs());
		anchorTypeIDActiveButton.addSelectionListener(new SelectionListener(){		
			public void widgetSelected(SelectionEvent e) {
				anchorTypeIDs.setEnabled(((Button)e.getSource()).getSelection());
			}		
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}		
		});				
		
//		Group nameGroup = new Group(parent, SWT.NONE);
//		nameGroup.setText("Repository Name");
//		nameGroup.setLayout(new GridLayout());
//		nameGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
//		activeNameButton = new Button(nameGroup, SWT.CHECK);
//		activeNameButton.setText("Active");
//		activeNameButton.addSelectionListener(activeNameListener);
//		repositoryNameText = new Text(nameGroup, SWT.BORDER);
//		repositoryNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
//		activeNameButton.setSelection(false);
//		repositoryNameText.setEnabled(false);
//		
//		Group anchorIDGroup = new Group(parent, SWT.NONE);
//		anchorIDGroup.setText("Repository ID");
//		anchorIDGroup.setLayout(new GridLayout());
//		anchorIDGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
//		activeAnchorIDButton = new Button(anchorIDGroup, SWT.CHECK);
//		activeAnchorIDButton.setText("Active");
//		activeAnchorIDButton.addSelectionListener(activeNameListener);
//		anchorIDText = new Text(anchorIDGroup, SWT.BORDER);
//		anchorIDText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		activeAnchorIDButton.setSelection(false);
//		anchorIDText.setEnabled(false);		
	}
	
	private AnchorID selectedOwnerID = null;
	private SelectionListener ownerSelectionListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			LegalEntity _legalEntity = LegalEntitySearchCreateWizard.open(ownerText.getText(), false);
			if (_legalEntity != null) {
				selectedOwnerID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
				// TODO perform this expensive code in a job
				LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(selectedOwnerID, 
						new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor());
				ownerText.setText(legalEntity.getPerson().getDisplayName());
			}
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};

//	private SelectionListener activeNameListener = new SelectionListener(){	
//		public void widgetSelected(SelectionEvent e) {
//			repositoryNameText.setEnabled(activeNameButton.getSelection());
//		}	
//		public void widgetDefaultSelected(SelectionEvent e) {
//			widgetSelected(e);
//		}	
//	};
//		
//	private SelectionListener activeAnchorIDListener = new SelectionListener(){	
//		public void widgetSelected(SelectionEvent e) {
//			anchorIDText.setEnabled(activeAnchorIDButton.getSelection());
//		}	
//		public void widgetDefaultSelected(SelectionEvent e) {
//			widgetSelected(e);
//		}	
//	};
	
	@Override
	public JDOQuery getJDOQuery() 
	{
		RepositoryQuery repositoryQuery = new RepositoryQuery();
		
		if (ownerActiveButton.getSelection())
			repositoryQuery.setOwnerID(selectedOwnerID);
		
		if (anchorTypeIDActiveButton.getSelection() && anchorTypeIDs.getSelectedElement() != null)
			repositoryQuery.setAnchorTypeID(anchorTypeIDs.getSelectedElement());
		
//		if (activeNameButton.getSelection() && repositoryNameText.getText() != null && !repositoryNameText.getText().trim().equals(""))
//			repositoryQuery.setName(repositoryNameText.getText());
//
//		if (activeAnchorIDButton.getSelection() && anchorIDText.getText() != null && !anchorIDText.getText().trim().equals(""))
//			repositoryQuery.setAnchorID(anchorIDText.getText());
		
		return repositoryQuery;
	}
	
	private List<String> availableRepositoryAnchorTypeIDs = null;
	protected List<String> getAvailableRepositoryAnchorTypeIDs() 
	{
		if (availableRepositoryAnchorTypeIDs == null) {
			List<String> list = new ArrayList<String>();
			list.add(Repository.ANCHOR_TYPE_ID_HOME);
			list.add(Repository.ANCHOR_TYPE_ID_OUTSIDE);			
			availableRepositoryAnchorTypeIDs = list;
		}
		return availableRepositoryAnchorTypeIDs;
	}	
}

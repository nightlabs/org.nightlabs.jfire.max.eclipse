package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference;

import java.util.List;

import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jseditor.ui.editor.JSEditorComposite;

public abstract class AbstractCellReferencePage
extends WizardHopPage
{
	private TabFolder tabFolder;
	
	private List<Composite> pageCompositeList;
	
	private SourceViewer sourceViewer;
	private PriceConfigComposite priceConfigComposite;
	
	private JSEditorComposite scriptPreviewComposite = null;
	
	public AbstractCellReferencePage(SourceViewer sourceViewer, PriceConfigComposite priceConfigComposite){
		super(AbstractCellReferencePage.class.getName());
		this.sourceViewer = sourceViewer;
		this.priceConfigComposite = priceConfigComposite;

		setTitle(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.AbstractCellReferencePage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.AbstractCellReferencePage.description")); //$NON-NLS-1$
	}
	
	protected abstract List<Composite> createDimensionTabItems(TabFolder tabFolder);

	protected SourceViewer getSourceViewer()
	{
		return sourceViewer;
	}
	protected PriceConfigComposite getPriceConfigComposite()
	{
		return priceConfigComposite;
	}

	public List<Composite> getPageCompositeList() {
		return pageCompositeList;
	}

	
	@Override
	public Control createPageContents(Composite parent) {
		GridLayout gridLayout = new GridLayout(1, true);
		parent.setLayout(gridLayout);

		SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		/******************************
		 * Tab Folder
		 ******************************/
		tabFolder =	new TabFolder(sashForm, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		tabFolder.setLayoutData(gridData);
		
		GridLayout tabFolderLayout = new GridLayout(1, true);
		tabFolder.setLayout(tabFolderLayout);

//		createOverviewTabItem(tabFolder);
		/**********************************************************/
		this.pageCompositeList = createDimensionTabItems(tabFolder);
		/**********************************************************/
		Group scriptPreviewGroup = new Group(sashForm, SWT.NONE);
		
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		scriptPreviewGroup.setLayout(gridLayout);
		
		gridData = new GridData(GridData.FILL_BOTH);
		scriptPreviewGroup.setLayoutData(gridData);
		scriptPreviewGroup.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.cellreference.AbstractCellReferencePage.scriptPreviewGroup.text")); //$NON-NLS-1$
		
		scriptPreviewComposite = new JSEditorComposite(RCPUtil.getActiveWorkbenchWindow(), scriptPreviewGroup);
		scriptPreviewComposite.setLayoutData(gridData);
		
		return tabFolder;
	}

	protected JSEditorComposite getSourcePreviewComposite(){
		return scriptPreviewComposite;
	}
	
//	private TabItem overviewTabItem;
//	private JSEditorComposite srcText;
	
//	private Text descText;
//	private void createOverviewTabItem(TabFolder tabFolder){
//		overviewTabItem =
//			new TabItem(tabFolder, SWT.NONE);
//		overviewTabItem.setText("Overview");
//
//		/******************************
//		 * Overview
//		 ******************************/
//		//Main Composite
//		XComposite overviewComposite = new XComposite(tabFolder, SWT.NONE);
//		overviewComposite.getGridLayout().numColumns = 3;
//
//		//Dimension Group
//		Group dGroup = new Group(overviewComposite, SWT.BORDER);
//		dGroup.setText("Dimension Properties");
//		dGroup.setLayout(new GridLayout());
//
//		GridData d = new GridData(GridData.FILL_BOTH);
//		d.horizontalSpan = 3;
//		dGroup.setLayoutData(d);
//
//		//Dimension Overview
//		PropertyDimentionOverviewComposite dimensionOverviewComposite =
//			new PropertyDimentionOverviewComposite(dGroup, SWT.NONE);
//		dimensionOverviewComposite.getDimensionTree().addSelectionListener(dimensionSelectionListener);
//
//		//Description Composite
//		Group descGroup = new Group(overviewComposite, SWT.NONE);
//		descGroup.setText("Description");
//		descGroup.setLayout(new GridLayout());
//
//		XComposite descComposite = new XComposite(descGroup, SWT.NONE);
//		descComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
//		descComposite.getGridLayout().numColumns = 1;
//		descText = new Text(descComposite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
//		descText.setText("Description");
//		descText.setLayoutData(new GridData(GridData.FILL_BOTH));
//
//		d = new GridData(GridData.FILL_BOTH);
//		d.horizontalSpan = 3;
//		d.heightHint = 100;
//		descGroup.setLayoutData(d);
//
//		overviewTabItem.setControl(overviewComposite);
//
//		/******************************
//		 * Customer Group Composite
//		 ******************************/
//		XComposite customerGroupTabComposite =
//			new XComposite(tabFolder, SWT.NONE);
//		customerGroupTabComposite.getGridLayout().numColumns = 4;
//	}
//
//	private SelectionAdapter dimensionSelectionListener = new SelectionAdapter(){
//		@Override
//		public void widgetSelected(SelectionEvent e) {
//			Tree tree = (Tree)e.getSource();
//			descText.setText(tree.getSelection()[0].getText());
//		}
//	};
//
//	public JSEditorComposite getSrcText(){
//		return srcText;
//	}
	
//	protected List getScriptStringList(){
//		return scriptStringList;
//	}
}

package org.nightlabs.jfire.reporting.admin.parameter.ui;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.nightlabs.base.ui.composite.ComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.reporting.admin.parameter.ui.action.AutoLayoutAction;
import org.nightlabs.jfire.reporting.admin.parameter.ui.action.AutoLayoutPagesAction;
import org.nightlabs.jfire.reporting.admin.parameter.ui.dialog.ShowXMLInitialisationDialog;
import org.nightlabs.jfire.reporting.admin.parameter.ui.dialog.UseCaseDialog;
import org.nightlabs.jfire.reporting.admin.parameter.ui.resource.Messages;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireRemoteReportEditorInput;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionUseCase;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.dao.ReportParameterAcquisitionSetupDAO;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.Utils;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class ReportParameterEditor 
extends GraphicalEditorWithFlyoutPalette 
{
	private static final Logger logger = Logger.getLogger(ReportParameterEditor.class);
	
/******************************* Inner class OutlinePage BEGIN *********************************/	
	class OutlinePage
	extends ContentOutlinePage
	implements IAdaptable
	{
		private PageBook pageBook;
		private Control outline;
		private Canvas overview;
		private IAction showOutlineAction, showOverviewAction;
		static final int ID_OUTLINE  = 0;
		static final int ID_OVERVIEW = 1;
		private Thumbnail thumbnail;
		private DisposeListener disposeListener;

		public OutlinePage(EditPartViewer viewer){
			super(viewer);
		}
		public void init(IPageSite pageSite) {
			super.init(pageSite);
			ActionRegistry registry = getActionRegistry();
			IActionBars bars = pageSite.getActionBars();
			String id = ActionFactory.UNDO.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			id = ActionFactory.REDO.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			id = ActionFactory.DELETE.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			bars.updateActionBars();
		}

		protected void configureOutlineViewer(){
			getViewer().setEditDomain(getEditDomain());
			getViewer().setEditPartFactory(new TreeEditPartFactory());
			ContextMenuProvider provider = new ReportParameterContextMenuProvider(
					getViewer(), getActionRegistry());
			getViewer().setContextMenu(provider);
			getSite().registerContextMenu(
			"org.nightlabs.jfire.reporting.admin.parameter.ui.outline.contextmenu", //$NON-NLS-1$
			provider, getSite().getSelectionProvider());
			getViewer().setKeyHandler(getCommonKeyHandler());
//			IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
//			showOutlineAction = new Action() {
//				public void run() {
//					showPage(ID_OUTLINE);
//				}
//			};
//
//			showOutlineAction.setImageDescriptor(
//					SharedImages.getSharedImageDescriptor(
//							ReportingAdminParameterPlugin.getDefault(), 
//							ReportParameterEditor.class, 
//							"OutlinePage-Outline", 
//							ImageDimension._16x16, 
//							ImageFormat.gif));
//			tbm.add(showOutlineAction);
//			showOverviewAction = new Action() {
//				public void run() {
//					showPage(ID_OVERVIEW);
//				}
//			};
//
//			showOverviewAction.setImageDescriptor(
//					SharedImages.getSharedImageDescriptor(
//					ReportingAdminParameterPlugin.getDefault(), 
//					ReportParameterEditor.class, 
//					"OutlinePage-Overview", 
//					ImageDimension._16x16, 
//					ImageFormat.gif));
//			tbm.add(showOverviewAction);
//			showPage(ID_OUTLINE);
		}

		public void createControl(Composite parent){
			pageBook = new PageBook(parent, SWT.NONE);
			outline = getViewer().createControl(pageBook);
			overview = new Canvas(pageBook, SWT.NONE);
			pageBook.showPage(outline);
			configureOutlineViewer();
			hookOutlineViewer();
			initializeOutlineViewer();
		}

		public void dispose(){
			unhookOutlineViewer();
			if (thumbnail != null) {
				thumbnail.deactivate();
				thumbnail = null;
			}
			super.dispose();
			ReportParameterEditor.this.outlinePage = null;
			outlinePage = null;
		}

		public Object getAdapter(Class type) {
			if (type == ZoomManager.class)
				return getGraphicalViewer().getProperty(ZoomManager.class.toString());
			return null;
		}

		public Control getControl() {
			return pageBook;
		}

		protected void hookOutlineViewer(){
			getSelectionSynchronizer().addViewer(getViewer());
		}

		protected void initializeOutlineViewer(){
			setContents(getValueAcquisitionSetup());
		}

		protected void initializeOverview() 
		{
			if (overview != null && overview.isDisposed()) {
				LightweightSystem lws = new LightweightSystem(overview);
				RootEditPart rep = getGraphicalViewer().getRootEditPart();
				if (rep instanceof ScalableFreeformRootEditPart) {
					ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart)rep;
					thumbnail = new ScrollableThumbnail((Viewport)root.getFigure());
					thumbnail.setBorder(new MarginBorder(3));
					thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
					lws.setContents(thumbnail);
					disposeListener = new DisposeListener() {
						public void widgetDisposed(DisposeEvent e) {
							if (thumbnail != null) {
								thumbnail.deactivate();
								thumbnail = null;
							}
						}
					};
					getFigureCanvas().addDisposeListener(disposeListener);
				}				
			}
		}

		public void setContents(Object contents) {
			getViewer().setContents(contents);
		}

//		protected void showPage(int id) {
//			if (id == ID_OUTLINE) {
//				showOutlineAction.setChecked(true);
//				showOverviewAction.setChecked(false);
//				pageBook.showPage(outline);
//				if (thumbnail != null)
//					thumbnail.setVisible(false);
//			} else if (id == ID_OVERVIEW) {
//				if (thumbnail == null)
//					initializeOverview();
//				showOutlineAction.setChecked(false);
//				showOverviewAction.setChecked(true);
//				pageBook.showPage(overview);
//				thumbnail.setVisible(true);
//			}
//		}

		protected void unhookOutlineViewer(){
			getSelectionSynchronizer().removeViewer(getViewer());
			if (disposeListener != null && getFigureCanvas() != null && !getFigureCanvas().isDisposed())
				getFigureCanvas().removeDisposeListener(disposeListener);
		}
	}

/******************************* Inner class OutlinePage END *********************************/
	
	@Override
	public Object getAdapter(Class type)
	{
//		logger.info("type = "+type);
		
		if (type == IContentOutlinePage.class) {
			outlinePage = new OutlinePage(new TreeViewer());
			return outlinePage;
		}
		if (type == ZoomManager.class)
			return getGraphicalViewer().getProperty(ZoomManager.class.toString());

		if (type == IPropertySheetPage.class) {    		
//			IPropertySheetPage page = new ReportPropertySheetPage();
			PropertySheetPage page = new PropertySheetPage();
			page.setRootEntry(new UndoablePropertySheetEntry(getCommandStack()));
			return page;
		}
		
//		if (type == AttributeView.class) {
//
//		}
		return super.getAdapter(type);
	}
	
	private OutlinePage outlinePage;

	protected FigureCanvas getFigureCanvas(){
		return (FigureCanvas)getGraphicalViewer().getControl();
	}

	private KeyHandler sharedKeyHandler;
	/**
	 * Returns the KeyHandler with common bindings for both the Outline and Graphical Views.
	 * For example, delete is a common action.
	 */
	protected KeyHandler getCommonKeyHandler(){
		if (sharedKeyHandler == null){
			sharedKeyHandler = new KeyHandler();
			sharedKeyHandler.put(
				KeyStroke.getPressed(SWT.F2, 0),
				getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT));
		}
		return sharedKeyHandler;
	}	
	
	public ReportParameterEditor() {
		super();
	}

	protected DefaultEditDomain getEditDomain() {
		if (super.getEditDomain() == null)
			setEditDomain(new DefaultEditDomain(this));
		return super.getEditDomain();
	}

	private PaletteFactory paletteFactory;
	protected PaletteFactory getPaletteFactory() {
		if (paletteFactory == null)
			paletteFactory = createPaletteFactory();

		return paletteFactory;
	}

	protected PaletteFactory createPaletteFactory() {
		return new PaletteFactory(getSetupProvider());
	}

	private IValueAcquisitionSetupProvider setupProvider = null;
	public IValueAcquisitionSetupProvider getSetupProvider() 
	{
		if (setupProvider == null)
			setupProvider = new ValueAcquisitionSetupProvider(getValueAcquisitionSetup());
		
		return setupProvider;
	}
	
	private ValueAcquisitionSetup currentSetup;
	public ValueAcquisitionSetup getValueAcquisitionSetup() 
	{
		if (currentSetup != null)
			return currentSetup;
		
		if (reportParameterAcquisitionSetup != null) {
			if (reportParameterAcquisitionSetup.getDefaultSetup() != null) {
				currentSetup = reportParameterAcquisitionSetup.getDefaultSetup(); 
//				return currentSetup;
			}			
			else {
				if (reportParameterAcquisitionSetup.getValueAcquisitionSetups().values() != null &&
						!reportParameterAcquisitionSetup.getValueAcquisitionSetups().values().isEmpty()) 
				{
					ValueAcquisitionSetup valueAcquisitionSetup = 
						reportParameterAcquisitionSetup.getValueAcquisitionSetups().values().iterator().next();
					reportParameterAcquisitionSetup.setDefaultSetup(valueAcquisitionSetup);
					currentSetup = valueAcquisitionSetup; 
//					return currentSetup;
				} 
				else {
					ReportParameterAcquisitionUseCase useCase = new ReportParameterAcquisitionUseCase(
							reportParameterAcquisitionSetup, 
							ReportParameterAcquisitionUseCase.USE_CASE_ID_DEFAULT); 
					ValueAcquisitionSetup valueAcquisitionSetup = new ValueAcquisitionSetup(
							reportRegistryItemID.organisationID,
							IDGenerator.nextID(ValueAcquisitionSetup.class),
							reportParameterAcquisitionSetup,
							useCase);
					reportParameterAcquisitionSetup.setDefaultSetup(valueAcquisitionSetup);
					reportParameterAcquisitionSetup.getValueAcquisitionSetups().put(
							useCase, valueAcquisitionSetup);
					currentSetup = valueAcquisitionSetup; 
//					return currentSetup;					
				}
			}
		} 
		else {
			String[] FETCH_GROUP = new String[] {FetchPlan.DEFAULT}; 
			ReportRegistryItem reportRegistryItem = ReportRegistryItemDAO.sharedInstance().
				getReportRegistryItem(reportRegistryItemID, FETCH_GROUP, new NullProgressMonitor());
			if (!(reportRegistryItem instanceof ReportLayout))
				throw new IllegalStateException("ReportRegistryItem is not an instance of "+ReportLayout.class.getSimpleName()); //$NON-NLS-1$
			reportParameterAcquisitionSetup = new ReportParameterAcquisitionSetup(
					reportRegistryItem.getOrganisationID(), 
					IDGenerator.nextID(ReportParameterAcquisitionSetup.class),
					(ReportLayout)reportRegistryItem
				);
			ReportParameterAcquisitionUseCase useCase = new ReportParameterAcquisitionUseCase(
					reportParameterAcquisitionSetup, 
					ReportParameterAcquisitionUseCase.USE_CASE_ID_DEFAULT); 
			ValueAcquisitionSetup valueAcquisitionSetup = createNewValueAcquisitionSetup(useCase);
			reportParameterAcquisitionSetup.setDefaultSetup(valueAcquisitionSetup);
			reportParameterAcquisitionSetup.getValueAcquisitionSetups().put(
					useCase, valueAcquisitionSetup);
			currentSetup = valueAcquisitionSetup; 
//			return currentSetup;			
		}
		
		getSetupProvider().setValueAcquisitionSetup(currentSetup);
		return currentSetup;
	}

	private ValueAcquisitionSetup createNewValueAcquisitionSetup(ReportParameterAcquisitionUseCase useCase) 
	{
		return new ValueAcquisitionSetup(
				reportRegistryItemID.organisationID,
				IDGenerator.nextID(ValueAcquisitionSetup.class),
				reportParameterAcquisitionSetup,
				useCase);		
	}
	
	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		getGraphicalViewer().setContents(getValueAcquisitionSetup());		
		doAutoLayout();
	}

	@Override
	protected FlyoutPreferences getPalettePreferences() {
		return getPaletteFactory().createPalettePreferences();
	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		return getPaletteFactory().createPalette();
	}

	@Override
	public void doSave(IProgressMonitor monitor) 
	{
		try {
			reportParameterAcquisitionSetup = ReportingPlugin.getReportParameterManager().storeReportParameterAcquisitionSetup(
					reportParameterAcquisitionSetup, 
					true, 
					ReportParameterAcquisitionSetupDAO.DEFAULT_FETCH_GROUPS, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			getCommandStack().markSaveLocation();
			currentSetup = null;
			getGraphicalViewer().setContents(getValueAcquisitionSetup());
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}				
	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	protected void configureGraphicalViewer() 
	{
		super.configureGraphicalViewer();
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)getGraphicalViewer();
		List<String> zoomLevels = new ArrayList<String>(3);
		zoomLevels.add(ZoomManager.FIT_ALL);
		zoomLevels.add(ZoomManager.FIT_WIDTH);
		zoomLevels.add(ZoomManager.FIT_HEIGHT);
		getRootEditPart().getZoomManager().setZoomLevelContributions(zoomLevels);
		viewer.setRootEditPart(getRootEditPart());
		viewer.setEditPartFactory(getEditPartFactory());
		
		// configure the context menu provider
		ContextMenuProvider cmProvider =
				new ReportParameterContextMenuProvider(viewer, getActionRegistry());
		viewer.setContextMenu(cmProvider);
		getSite().registerContextMenu(cmProvider, viewer);
		
//		doAutoLayout();
	}	

	private ScalableFreeformRootEditPart rootEditPart;
	public ScalableFreeformRootEditPart getRootEditPart() {
		if (rootEditPart == null)
			rootEditPart = new ScalableFreeformRootEditPart();
		return rootEditPart;
	}

	private EditPartFactory editPartFactory;
	protected EditPartFactory getEditPartFactory() {
		if (editPartFactory == null)
			editPartFactory = new EditPartFactory(this);
		return editPartFactory;
	}

	private ReportRegistryItemID reportRegistryItemID;
	private ReportParameterAcquisitionSetup reportParameterAcquisitionSetup;
	@Override
	protected void setInput(IEditorInput input) 
	{
		super.setInput(input);
		if (reportRegistryItemID != null && reportParameterAcquisitionSetup != null)
			return;
		JFireRemoteReportEditorInput reportEditorInput = (JFireRemoteReportEditorInput) input;
		reportRegistryItemID = reportEditorInput.getReportRegistryItemID();
		try {
			ReportParameterAcquisitionSetup cachedSetup = ReportParameterAcquisitionSetupDAO.sharedInstance().getSetupForReportLayout(
					reportEditorInput.getReportRegistryItemID(), 
					ReportParameterAcquisitionSetupDAO.DEFAULT_FETCH_GROUPS, 
					new NullProgressMonitor());
			if (cachedSetup != null)
				reportParameterAcquisitionSetup = Utils.cloneSerializable(cachedSetup);
			else
				reportParameterAcquisitionSetup = null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}

	@Override
	protected void createActions() 
	{
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		IAction action;	
		
		action = new AutoLayoutAction(this);
		registry.registerAction(action);
		getPropertyActions().add(action.getId());
		
		action = new AutoLayoutPagesAction(this);
		registry.registerAction(action);
		getPropertyActions().add(action.getId());	
		
//		action = new UndoAction(this);
//		registry.registerAction(action);
//		getStackActions().add(action.getId());
//		
//		action = new RedoAction(this);
//		registry.registerAction(action);
//		getStackActions().add(action.getId());
//		
//		action = new SelectAllAction(this);
//		registry.registerAction(action);
//		
//		action = new DeleteAction((IWorkbenchPart)this);
//		registry.registerAction(action);
//		getSelectionActions().add(action.getId());
//		
//		action = new SaveAction(this);
//		registry.registerAction(action);
//		getPropertyActions().add(action.getId());
//		
//		action = new PrintAction(this);
//		registry.registerAction(action);		
	}
	
	public abstract ModuleHandle getReportHandle();
	
	public void doAutoLayout() {
//		getActionRegistry().getAction(AutoLayoutAction.ID).run();
	}

	private Composite wrapper = null;
	private Composite topWrapper = null;
	private Composite contentWrapper = null;
	private ComboComposite<ReportParameterAcquisitionUseCase> useCaseCombo = null;
	private Button defaultUseCaseButton = null;
	private Button newUseCaseButton = null;
	private Button editUseCaseButton = null;
	private Button showXMLInitializationCode = null;
	private Composite parent = null;
		
	@Override
	public void createPartControl(Composite parent) 
	{
		this.parent = parent;
		wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		
		topWrapper = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		topWrapper.setLayout(new GridLayout(5, false));
		topWrapper.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		List<ReportParameterAcquisitionUseCase> useCases = new ArrayList<ReportParameterAcquisitionUseCase>(
				reportParameterAcquisitionSetup.getValueAcquisitionSetups().keySet());
		useCaseCombo = new ComboComposite<ReportParameterAcquisitionUseCase>(
				topWrapper, SWT.NONE, useCases, useCaseLabelProvider);
		useCaseCombo.addSelectionChangedListener(useCaseComboListener);
		defaultUseCaseButton = new Button(topWrapper, SWT.CHECK);
		defaultUseCaseButton.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.ReportParameterEditor.defaultUseCaseButton.text")); //$NON-NLS-1$
		defaultUseCaseButton.addSelectionListener(defaultUseCaseButtonListener);
		editUseCaseButton = new Button(topWrapper, SWT.NONE);
		editUseCaseButton.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.ReportParameterEditor.editUseCaseButton.text")); //$NON-NLS-1$
		editUseCaseButton.addSelectionListener(editUseCaseButtonListener);
		newUseCaseButton = new Button(topWrapper, SWT.NONE);
		newUseCaseButton.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.ReportParameterEditor.newUseCaseButton.text")); //$NON-NLS-1$
		newUseCaseButton.addSelectionListener(newUseCaseButtonListener);
		
		showXMLInitializationCode = new Button(topWrapper, SWT.PUSH);
		showXMLInitializationCode.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if (reportParameterAcquisitionSetup != null)
					ShowXMLInitialisationDialog.open(reportParameterAcquisitionSetup);
			}
		});
		showXMLInitializationCode.setText(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.ReportParameterEditor.showXMLInitializationCodeButton.text")); //$NON-NLS-1$
		showXMLInitializationCode.setLayoutData(new GridData());
		
		for (Map.Entry<ReportParameterAcquisitionUseCase, ValueAcquisitionSetup> entry : reportParameterAcquisitionSetup.getValueAcquisitionSetups().entrySet()) {
			if (entry.getValue().equals(reportParameterAcquisitionSetup.getDefaultSetup())) {
				useCaseCombo.selectElement(entry.getKey());
			}
		}
		defaultUseCaseButton.setSelection(true);
		
		contentWrapper = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		contentWrapper.setLayout(new FillLayout());
		super.createPartControl(contentWrapper);
	}
		
	private ILabelProvider useCaseLabelProvider = new LabelProvider() {
		@Override
		public String getText(Object element) 
		{
			if (element instanceof ReportParameterAcquisitionUseCase) {
				ReportParameterAcquisitionUseCase useCase = (ReportParameterAcquisitionUseCase) element;
				return useCase.getName().getText();
			}
			return super.getText(element);
		}		
	};
	
	private ISelectionChangedListener useCaseComboListener = new ISelectionChangedListener(){	
		public void selectionChanged(SelectionChangedEvent event) {
			if (!event.getSelection().isEmpty() && event.getSelection() instanceof StructuredSelection) {
				StructuredSelection sel = (StructuredSelection) event.getSelection();
				ReportParameterAcquisitionUseCase useCase = (ReportParameterAcquisitionUseCase) sel.getFirstElement();
				ValueAcquisitionSetup setup = reportParameterAcquisitionSetup.getValueAcquisitionSetups().get(useCase);
				ReportParameterEditor.this.currentSetup = setup;
				if (reportParameterAcquisitionSetup.getDefaultSetup().equals(currentSetup))
					defaultUseCaseButton.setSelection(true);
				else
					defaultUseCaseButton.setSelection(false);
				
				setCurrentValueAcquisitionSetup(setup);
			}			
		}	
	};
	
	private SelectionListener defaultUseCaseButtonListener = new SelectionListener(){	
		public void widgetSelected(SelectionEvent e) {
			reportParameterAcquisitionSetup.setDefaultSetup(currentSetup);
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	}; 
	
	private SelectionListener editUseCaseButtonListener = new SelectionListener(){	
		public void widgetSelected(SelectionEvent e) {
			UseCaseDialog dialog = new UseCaseDialog(getSite().getShell(), 
					useCaseCombo.getSelectedElement(), reportParameterAcquisitionSetup, UseCaseDialog.EDIT_MODE);
			int returnCode = dialog.open();
			if (returnCode == Dialog.OK) {
				useCaseCombo.refresh();
			}
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	}; 

	private SelectionListener newUseCaseButtonListener = new SelectionListener(){	
		public void widgetSelected(SelectionEvent e) {
			UseCaseDialog dialog = new UseCaseDialog(getSite().getShell(), 
					null, reportParameterAcquisitionSetup, UseCaseDialog.NEW_MODE);
			int returnCode = dialog.open();
			if (returnCode == Dialog.OK) {
				ReportParameterAcquisitionUseCase useCase = dialog.getUseCase();
				ValueAcquisitionSetup setup = createNewValueAcquisitionSetup(useCase);
				reportParameterAcquisitionSetup.addValueAcquisitionSetup(setup);
//				reportParameterAcquisitionSetup.getValueAcquisitionSetups().put(
//						useCase, setup);				
				useCaseCombo.addElement(useCase);				
				useCaseCombo.selectElement(useCase);				
				setCurrentValueAcquisitionSetup(setup);
			}
		}	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
	}; 
	
	protected void setCurrentValueAcquisitionSetup(ValueAcquisitionSetup setup) 
	{
		this.currentSetup = setup;
		getSetupProvider().setValueAcquisitionSetup(currentSetup);
		
		JFireRemoteReportEditorInput oldInput = (JFireRemoteReportEditorInput) getEditorInput();
		IEditorInput input = new JFireRemoteReportEditorInput(oldInput.getReportRegistryItemID());
		try {
			getSelectionSynchronizer().removeViewer(getRootEditPart().getViewer());
			// necessary to avoid FigureCanvas SWT Bug (Scrollbar Widget is disposed)			
			getFigureCanvas().setViewport(new Viewport(true));
			getGraphicalViewer().getControl().dispose();
			contentWrapper.dispose();
			dispose();
			
			init((IEditorSite)getSite(), input);

//			if (getSite().getWorkbenchWindow().getSelectionService() instanceof AbstractSelectionService) {
//				AbstractSelectionService service = (AbstractSelectionService) getSite().getWorkbenchWindow().getSelectionService();
//				service.setActivePart(getEditorSite().getPart());
//			}
			
			contentWrapper = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
			contentWrapper.setLayout(new FillLayout());
			super.createPartControl(contentWrapper);
			parent.layout(true, true);						
			
//			DefaultEditDomain domain = (DefaultEditDomain) getEditDomain();
			getEditDomain().setActiveTool(getEditDomain().getDefaultTool());
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
	}

}

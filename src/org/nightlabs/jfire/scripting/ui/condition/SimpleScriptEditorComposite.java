package org.nightlabs.jfire.scripting.ui.condition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.AbstractListComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.scripting.ScriptExecutorJavaScript;
import org.nightlabs.jfire.scripting.condition.CombineOperator;
import org.nightlabs.jfire.scripting.condition.ConditionContainer;
import org.nightlabs.jfire.scripting.condition.GeneratorRegistry;
import org.nightlabs.jfire.scripting.condition.ICondition;
import org.nightlabs.jfire.scripting.condition.IConditionContainer;
import org.nightlabs.jfire.scripting.condition.IConditionGenerator;
import org.nightlabs.jfire.scripting.condition.ISimpleCondition;
import org.nightlabs.jfire.scripting.condition.Script;
import org.nightlabs.jfire.scripting.condition.ScriptConditioner;
import org.nightlabs.jfire.scripting.condition.SimpleCondition;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.resource.Messages;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleScriptEditorComposite
extends XComposite
{
	private static final Logger logger = Logger.getLogger(SimpleScriptEditorComposite.class);

	public SimpleScriptEditorComposite(Collection<ScriptConditioner> scriptConditioners,
			Composite parent, int style) {
		super(parent, style);
		this.scriptConditioners = scriptConditioners;
		createComposite(this);
	}

	public SimpleScriptEditorComposite(Collection<ScriptConditioner> scriptConditioners,
			Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		this.scriptConditioners = scriptConditioners;
		createComposite(this);
	}

	private Collection<ScriptConditioner> scriptConditioners;
	private Composite conditionArea;
	private TreeViewer treeViewer;
	private Text text;
	private Composite parent;
	private CombineOperator currentCombineOperator = CombineOperator.LOGICAL_AND;
	private IConditionContainer rootContainer;
	private Map<Button, ICondition> button2Condition = new HashMap<Button, ICondition>();
//	private Map<ICondition, IConditionContainer> condition2Container = new HashMap<ICondition, IConditionContainer>();
	private Map<SimpleConditionComposite, ISimpleCondition> conditionComp2SimpleCondition = new HashMap<SimpleConditionComposite, ISimpleCondition>();
	private ICondition condition = null;

	public ICondition getCondition() {
		return condition;
	}

//	private void createComposite(Composite parent)
//	{
//		this.parent = parent;
//		Composite comp = new XComposite(parent, SWT.BORDER);
//		comp.setLayout(new GridLayout(2, false));
//		GridData compData = new GridData(GridData.FILL_BOTH);
//		compData.minimumHeight = 150;
//		compData.minimumWidth = 300;
//		comp.setLayoutData(compData);
//
//		treeViewer = new TreeViewer(comp, SWT.SINGLE | SWT.FULL_SELECTION);
//		treeViewer.setContentProvider(new ConditionContentProvider());
//		treeViewer.setLabelProvider(new ConditionLabelProvider());
//		treeViewer.addSelectionChangedListener(treeSelectionListener);
//		treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
//		text = new Text(comp, SWT.BORDER);
//		text.setEnabled(false);
//		text.setLayoutData(new GridData(GridData.FILL_BOTH));
//
//		conditionArea = new XComposite(parent, SWT.BORDER);
//		setCondition(createSimpleCondition());
//	}

	private void createComposite(Composite parent)
	{
		initScriptConditioner();

		this.parent = parent;
		final Composite comp = new XComposite(parent, SWT.BORDER);
		comp.setLayout(new GridLayout(3, false));
		GridData compData = new GridData(GridData.FILL_BOTH);
		compData.minimumHeight = 100;
		compData.minimumWidth = 200;
		comp.setLayoutData (compData);

		final Sash sash = new Sash(comp, SWT.VERTICAL);
		final FormLayout form = new FormLayout ();
		comp.setLayout(form);

		FormData treeData = new FormData ();
		treeData.left = new FormAttachment (0, 0);
		treeData.right = new FormAttachment (sash, 0);
		treeData.top = new FormAttachment (0, 0);
		treeData.bottom = new FormAttachment (100, 0);

		treeViewer = new TreeViewer(comp, SWT.SINGLE | SWT.FULL_SELECTION);
		treeViewer.setContentProvider(new ConditionContentProvider());
		treeViewer.setLabelProvider(new ConditionLabelProvider());
		treeViewer.addSelectionChangedListener(treeSelectionListener);
		treeViewer.getControl().setLayoutData(treeData);

		final int limit = 20, percent = 50;
		final FormData sashData = new FormData ();
		sashData.left = new FormAttachment (percent, 0);
		sashData.top = new FormAttachment (0, 0);
		sashData.bottom = new FormAttachment (100, 0);
		sash.setLayoutData (sashData);

		sash.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Rectangle sashRect = sash.getBounds();
				Rectangle compRect = comp.getClientArea();
				int right = compRect.width - sashRect.width - limit;
				e.x = Math.max (Math.min (e.x, right), limit);
				if (e.x != sashRect.x)  {
					sashData.left = new FormAttachment(0, e.x);
					comp.layout();
				}
			}
		});

		text = new Text(comp, SWT.BORDER | SWT.WRAP);
		text.setEnabled(false);
		FormData textData = new FormData ();
		textData.left = new FormAttachment (sash, 0);
		textData.right = new FormAttachment (100, 0);
		textData.top = new FormAttachment (0, 0);
		textData.bottom = new FormAttachment (100, 0);
		text.setLayoutData(textData);

		setCondition(createSimpleCondition());
	}

	private ISimpleCondition createSimpleCondition()
	{
		ScriptConditioner sc = scriptConditioners.iterator().next();
		ISimpleCondition condition = new SimpleCondition(sc.getScriptRegistryItemID(),
				sc.getCompareOperators().get(0),
				sc.getPossibleValues().iterator().next());
		return condition;
	}

	private IConditionContainer createConditionContainer()
	{
		IConditionContainer container = new ConditionContainer();
		container.addCondition(createSimpleCondition());
		container.addCondition(createSimpleCondition());
		container.setCombineOperator(currentCombineOperator);
		return container;
	}

	public void setCondition(ICondition condition)
	{
		if (condition != null) {
			this.condition = condition;
			treeViewer.setInput(CollectionUtil.createArrayList(condition));
			clearMaps();
			conditionSelected(condition);
			treeViewer.collapseAll();
		}
	}

	private void clearMaps()
	{
		button2Condition.clear();
		combineCombo2Container.clear();
//		condition2Container.clear();
		conditionComp2SimpleCondition.clear();
	}

	private ISelectionChangedListener treeSelectionListener = new ISelectionChangedListener(){
		public void selectionChanged(SelectionChangedEvent event) {
			StructuredSelection selection = (StructuredSelection) event.getSelection();
			ICondition condition = (ICondition) selection.getFirstElement();
			if (condition != null) {
				conditionSelected(condition);
//				setCondition(condition);
			}
		}
	};

	private void conditionSelected(ICondition condition) {
		if (conditionArea != null)
			conditionArea.dispose();
		conditionArea = new XComposite(parent, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.minimumHeight = 150;
		conditionArea.setLayoutData(data);

		createContainerComp(conditionArea, condition);
		setScriptText(condition);
		parent.layout(true, true);
	}

//	private ScrolledComposite sc;
//	private void conditionSelected(ICondition condition)
//	{
//		if (sc != null)
//			sc.dispose();
//		conditionArea.dispose();
//		sc = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//		sc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		conditionArea = new XComposite(sc, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
//
//		conditionArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		createContainerComp(conditionArea, condition);
//		sc.setContent(conditionArea);
//		sc.setExpandHorizontal(true);
//		sc.setExpandVertical(true);
//
//		setScriptText(condition);
//		parent.layout(true, true);
//	}

	private void createSimpleConditionComp(Composite parent, ISimpleCondition condition,
			IConditionContainer container)
	{
		parent.setLayout(new GridLayout(3, false));
		SimpleConditionComposite simpleComp = new SimpleConditionComposite(scriptConditioners, parent, SWT.NONE);
		simpleComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		simpleComp.setSimpleCondition(condition);
		simpleComp.addConditionChangedListener(conditionChangeListener);
		Button addButton = new Button(parent, SWT.NONE);
		addButton.setText(Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorComposite.addButton.text")); //$NON-NLS-1$
		Button deleteButton = new Button(parent, SWT.NONE);
		deleteButton.setText(Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorComposite.deleteButton.text")); //$NON-NLS-1$
		if (container == null)
			deleteButton.setEnabled(false);

		button2Condition.put(addButton, container);
		button2Condition.put(deleteButton, condition);
		conditionComp2SimpleCondition.put(simpleComp, condition);

		addButton.addSelectionListener(addSimpleConditionListener);
		deleteButton.addSelectionListener(deleteConditionListener);
	}

	private SelectionListener addSimpleConditionListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			Button b = (Button) e.getSource();
			IConditionContainer container = (IConditionContainer) button2Condition.get(b);
			if (container != null) {
				container.addCondition(createSimpleCondition());
//				condition = container;
			}
			else {
				ICondition oldRoot = condition;
				IConditionContainer newRoot = new ConditionContainer();
				newRoot.addCondition(oldRoot);
				ISimpleCondition sc = createSimpleCondition();
				newRoot.addCondition(sc);
				newRoot.setCombineOperator(currentCombineOperator);
				condition = newRoot;
				rootContainer = newRoot;
			}
			setCondition(condition);
		}
	};

	private SelectionListener deleteConditionListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			Button b = (Button) e.getSource();
			ICondition con = button2Condition.get(b);
			IConditionContainer container = con.getParent();
			if (container != null) {
				if (container.getConditions().size() > 2)
					container.removeCondition(con);
				else
				{
					IConditionContainer parentContainer = container.getParent();
					if (parentContainer != null)
						parentContainer.removeCondition(container);
					else {
						ICondition con1 = container.getConditions().get(0);
						ICondition con2 = container.getConditions().get(1);
						if (con1.equals(con))
							condition = con2;
						if (con2.equals(con))
							condition = con1;

						container.removeCondition(condition);
					}
				}
			}
			setCondition(condition);
		}
	};

	private SelectionListener addContainerListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			Button b = (Button) e.getSource();
			IConditionContainer parent = (IConditionContainer) button2Condition.get(b);
			IConditionContainer container = createConditionContainer();
			parent.addCondition(container);
			setCondition(condition);
		}
	};

	private SelectionListener deleteContainerListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			Button b = (Button) e.getSource();
			IConditionContainer container = (IConditionContainer) button2Condition.get(b);
			IConditionContainer parent = container.getParent();
			if (parent != null)
				parent.removeCondition(container);

			setCondition(condition);
		}
	};

	private void createContainerComp(Composite parent, ICondition condition)
	{
		parent.setLayout(new GridLayout(1, true));
		if (condition instanceof ISimpleCondition) {
//			createSimpleConditionComp(parent, (ISimpleCondition)condition, rootContainer);
			createSimpleConditionComp(parent, (ISimpleCondition)condition, condition.getParent());
		}
		else if (condition instanceof IConditionContainer)
		{
			IConditionContainer container = (IConditionContainer) condition;
			Group conditionsGroup = new Group(parent, SWT.NONE);
			conditionsGroup.setText(Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorComposite.conditionsGroup.text")); //$NON-NLS-1$
			conditionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			conditionsGroup.setLayout(new GridLayout());
			for (ICondition con : container.getConditions())
			{
				if (con instanceof ISimpleCondition) {
					Composite wrapperComp = new XComposite(conditionsGroup, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
//					createSimpleConditionComp(wrapperComp, (ISimpleCondition)con, rootContainer);
					createSimpleConditionComp(wrapperComp, (ISimpleCondition)con, con.getParent());
				}
				else if (con instanceof IConditionContainer){
					XComposite containerComp = new XComposite(conditionsGroup, SWT.NONE);
					containerComp.setLayout(new GridLayout(3, false));
					Label label = new Label(containerComp, SWT.NONE);
					label.setText(Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorComposite.containerCompLabel.text")); //$NON-NLS-1$
					Label spacer = new Label(containerComp, SWT.NONE);
					spacer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

					Button deleteContainer = new Button(containerComp, SWT.NONE);
					deleteContainer.setText(Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorComposite.deleteContainerButton.text")); //$NON-NLS-1$
					deleteContainer.addSelectionListener(deleteContainerListener);
					button2Condition.put(deleteContainer, con);
				}
			}
			createContainerDetailComp(parent, container);
		}
	}

	private Map<XComboComposite<CombineOperator>, IConditionContainer> combineCombo2Container =
		new HashMap<XComboComposite<CombineOperator>, IConditionContainer>();

	private void createContainerDetailComp(Composite parent, IConditionContainer container)
	{
		Group containerComp = new Group(parent, SWT.NONE);
		containerComp.setText(Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorComposite.containerGroupCompLabel.text")); //$NON-NLS-1$
		containerComp.setLayout(new GridLayout(5, false));
		containerComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		XComboComposite<CombineOperator> combineOperatorCombo = new XComboComposite<CombineOperator>(
				containerComp, AbstractListComposite.getDefaultWidgetStyle(parent), (String) null);
		combineOperatorCombo.setInput( CollectionUtil.enum2List(CombineOperator.LOGICAL_AND) );
		combineOperatorCombo.selectElement(container.getCombineOperator());
		combineCombo2Container.put(combineOperatorCombo, container);
		Label l = new Label(containerComp, SWT.NONE);
		l.setText(Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorComposite.combineOperatorLabel.text")); //$NON-NLS-1$

		Label spacer = new Label(containerComp, SWT.NONE);
		spacer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button addContainerButton = new Button(containerComp, SWT.NONE);
		addContainerButton.setText(Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorComposite.accContainerButton.text")); //$NON-NLS-1$
		Button deleteContainerButton = new Button(containerComp, SWT.NONE);
		deleteContainerButton.setText(Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorComposite.deleteContainerButton.text")); //$NON-NLS-1$

		button2Condition.put(addContainerButton, condition);
		button2Condition.put(deleteContainerButton, condition);

		addContainerButton.addSelectionListener(addContainerListener);
		deleteContainerButton.addSelectionListener(deleteContainerListener);
		combineOperatorCombo.addSelectionListener(combineComboListener);
	}

	private SelectionListener combineComboListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e)
		{
			Control control = (Control) e.getSource();
			XComboComposite<CombineOperator> combo = (XComboComposite<CombineOperator>) control.getParent();
			IConditionContainer container = combineCombo2Container.get(combo);
			container.setCombineOperator(combo.getSelectedElement());
			setScriptText(condition);
		}
	};

	class ConditionContentProvider
	implements ITreeContentProvider
	{
		public Object[] getChildren(Object parentElement)
		{
			if (parentElement instanceof IConditionContainer) {
				return ((IConditionContainer)parentElement).getConditions().toArray();
			}
			return new Object[] {};
		}

		public Object getParent(Object element) {
			if (element instanceof ICondition) {
				ICondition condition = (ICondition) element;
				return condition.getParent();
			}
			return null;
		}

		public boolean hasChildren(Object element)
		{
			if (element instanceof IConditionContainer) {
				return !((IConditionContainer)element).getConditions().isEmpty();
			}
			return false;
		}

		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof Collection) {
				return ((Collection<Object>) inputElement).toArray();
			}
			return new Object[] {inputElement};
		}

		public void dispose() {

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}
	}

//	class ConditionLabelProvider
//	extends LabelProvider
//	{
//		@Override
//		public String getText(Object element)
//		{
//			if (element instanceof ISimpleCondition)
//				return ScriptingPlugin.getResourceString("SimpleScriptEditorComposite.ConditionLabelProvider.condition");
//			if (element instanceof IConditionContainer)
//				return ScriptingPlugin.getResourceString("SimpleScriptEditorComposite.ConditionLabelProvider.conditionContainer");
//
//			return super.getText(element);
//		}
//	}

	class ConditionLabelProvider
	extends LabelProvider
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof ISimpleCondition) {
				String label = Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorComposite.conditionNode.text"); //$NON-NLS-1$
				return label;
			}
			if (element instanceof IConditionContainer) {
				return String.format(Messages.getString("org.nightlabs.jfire.scripting.ui.condition.SimpleScriptEditorComposite.conditionContainerNode.text"), //$NON-NLS-1$
						((IConditionContainer)element).getConditions().size());
			}

			return super.getText(element);
		}
	}

	private ConditionChangeListener conditionChangeListener = new ConditionChangeListener()
	{
		public void conditonChanged(ConditionChangedEvent event) {
			ISimpleCondition newSimpleCondition = (ISimpleCondition) event.getCondition();
			ISimpleCondition oldSimpleCondition = conditionComp2SimpleCondition.get(event.getConditionComposite());
			ICondition oldCondition = condition;
			if (condition instanceof ISimpleCondition && rootContainer == null) {
				 condition = newSimpleCondition;
			}
			else if (condition instanceof IConditionContainer) {
				boolean succesful = replaceCondition((IConditionContainer)condition, oldSimpleCondition, newSimpleCondition);
				if (!succesful) {
					logger.error("replacing of condition failed!"); //$NON-NLS-1$
				}
			}
			treeViewer.setInput(CollectionUtil.createArrayList(condition));
			setScriptText(condition);

			if (logger.isDebugEnabled()) {
				logger.debug("conditonChanged"); //$NON-NLS-1$
				logger.debug("oldSimpleCondition = "+getGenerator().getScriptText(oldSimpleCondition)); //$NON-NLS-1$
				logger.debug("newSimpleCondition = "+getGenerator().getScriptText(newSimpleCondition));				 //$NON-NLS-1$
				logger.debug("oldCondition = "+getGenerator().getScriptText(oldCondition)); //$NON-NLS-1$
				logger.debug("newCondition = "+getGenerator().getScriptText(condition)); //$NON-NLS-1$
				logger.debug(""); //$NON-NLS-1$
			}
		}
	};

	private boolean replaceCondition(IConditionContainer container, ISimpleCondition original,
			ISimpleCondition replace)
	{
		for (ICondition con : container.getConditions())
		{
			if (con instanceof ISimpleCondition) {
				if (con.equals(original)) {
					container.getConditions().set(container.getConditions().indexOf(original), replace);
					return true;
				}
			}
			else if (con instanceof IConditionContainer) {
				replaceCondition((IConditionContainer)con, original, replace);
			}
		}
		return false;
	}

	private void setScriptText(ICondition condition)
	{
		String scriptText = getGenerator().getScriptText(condition);
		if (scriptText != null)
			text.setText(scriptText);
	}

	private IConditionGenerator generator;
	private IConditionGenerator getGenerator()
	{
		if (generator == null) {
//			generator = GeneratorRegistry.sharedInstance().getGenerator(getLanguage());
			generator = GeneratorRegistry.sharedInstance().getGenerator(
					getLanguage(), scriptConditioners);
		}
		return generator;
	}

	private String language = ScriptExecutorJavaScript.LANGUAGE_JAVA_SCRIPT;;
	protected String getLanguage() {
		return language;
	}

	public Script getScript()
	{
		Map<String, ScriptRegistryItemID> imports = new HashMap<String, ScriptRegistryItemID>();
		imports = getImports(condition, imports);
		Script script = new Script(getLanguage(), getGenerator().getScriptText(condition), imports);
		return script;
	}

	public void setScript(Script script)
	{
		if (script != null) {
			language = script.getLanguage();
			String scriptText = script.getText();
			ICondition condition = getGenerator().getCondition(scriptText, false);
			setCondition(condition);
		}
	}

	private Map<ScriptRegistryItemID, String> allImports = new HashMap<ScriptRegistryItemID, String>();
	private void initScriptConditioner()
	{
		// TODO override variableNames
		for (ScriptConditioner scriptConditioner : scriptConditioners) {
			allImports.put(scriptConditioner.getScriptRegistryItemID(), scriptConditioner.getVariableName());
		}
	}

	private Map<String, ScriptRegistryItemID> getImports(ICondition condition, Map<String, ScriptRegistryItemID> imports)
	{
		if (condition instanceof ISimpleCondition) {
			ISimpleCondition simpleCondition = (ISimpleCondition) condition;
			imports.put(allImports.get(simpleCondition.getScriptRegistryItemID()),
					simpleCondition.getScriptRegistryItemID());
		}
		else if (condition instanceof IConditionContainer) {
			IConditionContainer container = (IConditionContainer) condition;
			for (ICondition con : container.getConditions()) {
				imports.putAll(getImports(con, imports));
			}
		}
		return imports;
	}

}

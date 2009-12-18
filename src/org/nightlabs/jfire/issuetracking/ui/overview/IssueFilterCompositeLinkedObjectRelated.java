package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerCategory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkHandlerFactory;
import org.nightlabs.jfire.issuetracking.ui.issuelink.create.SelectIssueLinkHandlerFactoryTreeComposite;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFilterCompositeLinkedObjectRelated
extends AbstractQueryFilterComposite<IssueQuery>
{
	private static final Logger logger = Logger.getLogger(IssueFilterCompositeLinkedObjectRelated.class);

	private CheckboxTreeViewer checkboxTreeViewer;
	private volatile Set<Class> selectedLinkedObjectClasses = new HashSet<Class>();
	/**
	 * @param parent
	 *          The parent to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param layoutMode
	 *          The layout mode to use. See {@link XComposite.LayoutMode}.
	 * @param layoutDataMode
	 *          The layout data mode to use. See {@link XComposite.LayoutDataMode}.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public IssueFilterCompositeLinkedObjectRelated(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite();
	}

	/**
	 * @param parent
	 *          The this to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public IssueFilterCompositeLinkedObjectRelated(Composite parent, int style,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite();
	}

	@Override
	public Class<IssueQuery> getQueryClass() {
		return IssueQuery.class;
	}

	private SelectIssueLinkHandlerFactoryTreeComposite linkedObjectTreeComposite;
	@Override
	protected void createComposite()
	{
		this.setLayout(new GridLayout(3, false));

		XComposite mainComposite = new XComposite(this, SWT.NONE,
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 2;


		new Label(mainComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeLinkedObjectRelated.linkedObjectLabel")); //$NON-NLS-1$
		linkedObjectTreeComposite = new SelectIssueLinkHandlerFactoryTreeComposite(mainComposite, SWT.CHECK, null);

		checkboxTreeViewer = new CheckboxTreeViewer(linkedObjectTreeComposite.getTree());
		checkboxTreeViewer.setContentProvider(linkedObjectTreeComposite.getTreeViewer().getContentProvider());
		checkboxTreeViewer.setInput(linkedObjectTreeComposite.getTreeViewer().getInput());

		checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				selectedLinkedObjectClasses.clear();

				final boolean isChecked = event.getChecked();
				Object checkedNode = event.getElement();

				if (checkedNode instanceof IssueLinkHandlerFactory) {
					IssueLinkHandlerFactory factory = (IssueLinkHandlerFactory) checkedNode;

					for (TreeItem treeItem : checkboxTreeViewer.getTree().getItems()) {
						if (treeItem.getData() instanceof IssueLinkHandlerCategory) {
							IssueLinkHandlerCategory category = (IssueLinkHandlerCategory) treeItem.getData();
							if (category.getCategoryId().equals(factory.getCategoryId())) {
								//check for all children
								boolean allChecked = true;
								for (IssueLinkHandlerFactory f : category.getChildFactories()) {
									allChecked &= checkboxTreeViewer.getChecked(f);
								}
								checkboxTreeViewer.setChecked(category, allChecked);
							}

						}
					}
				}
				else if (checkedNode instanceof IssueLinkHandlerCategory) {
					checkboxTreeViewer.setSubtreeChecked(checkedNode, isChecked);
					if (isChecked)
						linkedObjectTreeComposite.getTreeViewer().expandToLevel(checkedNode, AbstractTreeViewer.ALL_LEVELS);
					else
						linkedObjectTreeComposite.getTreeViewer().collapseToLevel(checkedNode, AbstractTreeViewer.ALL_LEVELS);
				}

				for (Object checkElement : checkboxTreeViewer.getCheckedElements()) {
					if (checkElement instanceof IssueLinkHandlerFactory) {
						IssueLinkHandlerFactory factory = (IssueLinkHandlerFactory) checkElement;
						selectedLinkedObjectClasses.add(factory.getLinkedObjectClass());
					}
				}

				getQuery().setLinkedObjectClasses(selectedLinkedObjectClasses);
				boolean enable = !selectedLinkedObjectClasses.isEmpty();
				getQuery().setFieldEnabled(IssueQuery.FieldName.linkedObjectClasses, enable);
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		for (FieldChangeCarrier changedField : event.getChangedFields())
		{
			if (IssueQuery.FieldName.linkedObjectClasses.equals(changedField.getPropertyName()))
			{
				Set<Class> newClasses = (Set<Class>) changedField.getNewValue();
				if (newClasses == null)
				{
					checkboxTreeViewer.expandAll();
					TreeItem[] treeItems = checkboxTreeViewer.getTree().getItems();
					for (TreeItem treeItem : treeItems) {
						IssueLinkHandlerCategory category = (IssueLinkHandlerCategory) treeItem.getData();
						for (IssueLinkHandlerFactory childFactory : category.getChildFactories()) {
							checkboxTreeViewer.setChecked(childFactory, false);
						}
						treeItem.setChecked(false);
					}
				}
				else
				{
					checkboxTreeViewer.expandAll();
					for (Class newClass : newClasses) {
						TreeItem[] treeItems = checkboxTreeViewer.getTree().getItems();
						for (TreeItem treeItem : treeItems) {
							if (treeItem.getData() instanceof IssueLinkHandlerCategory) {
								IssueLinkHandlerCategory category = (IssueLinkHandlerCategory) treeItem.getData();
								boolean isCheckAll = true;
								for (IssueLinkHandlerFactory childFactory : category.getChildFactories()) {
									if (childFactory.getLinkedObjectClass().equals(newClass))
										checkboxTreeViewer.setChecked(childFactory, true);
									isCheckAll &= checkboxTreeViewer.getChecked(childFactory);
								}
								checkboxTreeViewer.setChecked(category, isCheckAll);
							}
						}
					}
				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.linkedObjectClasses).equals(
					changedField.getPropertyName()))
			{
				Boolean active = (Boolean) changedField.getNewValue();
				setSearchSectionActive(active);
				if (!active) {
					checkboxTreeViewer.setAllChecked(false);
					getQuery().getProjectIDs().clear();
				}
			}
		} // for (FieldChangeCarrier changedField : event.getChangedFields())
	}

	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>(1);
		fieldNames.add(IssueQuery.FieldName.linkedObjectClasses);
	}

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	/**
	 * Group ID for storing active states in the query.
	 */
	public static final String FILTER_GROUP_ID = "IssueFilterCompositeLinkedObjectRelated"; //$NON-NLS-1$

	@Override
	protected String getGroupID()
	{
		return FILTER_GROUP_ID;
	}
}
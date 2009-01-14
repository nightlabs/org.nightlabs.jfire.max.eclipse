package org.nightlabs.jfire.trade.ui.overview;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractArticleContainerListComposite<O extends ArticleContainer>
extends AbstractTableComposite<O>
{
	/**
	 * @param parent
	 * @param style
	 */
	public AbstractArticleContainerListComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 * @param viewerStyle
	 */
	public AbstractArticleContainerListComposite(Composite parent, int style,
			boolean initTable, int viewerStyle)
	{
		super(parent, style, initTable, viewerStyle);
	}

	private LinkedList<Integer> weightedColumns;
	private LinkedList<Integer> fixedColumns;

	protected void addFixedColumn(int columnWidth)
	{
		if (weightedColumns == null)
		{
			weightedColumns = new LinkedList<Integer>();
			fixedColumns = new LinkedList<Integer>();
		}
		weightedColumns.add(Integer.valueOf(-1));
		fixedColumns.add(Integer.valueOf(columnWidth));
	}

	protected void addWeightedColumn(int columnWeight)
	{
		if (weightedColumns == null)
		{
			weightedColumns = new LinkedList<Integer>();
			fixedColumns = new LinkedList<Integer>();
		}
		weightedColumns.add(Integer.valueOf(columnWeight));
		fixedColumns.add(Integer.valueOf(-1));
	}

	protected String getIDColumnText() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.column.id.text"); //$NON-NLS-1$
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
//		This layout is simply to buggy (columns may be resized to size = 0 => they disappear!
//		And not like the other layouts where you can make them visible again by pulling the divider in the reverse direction.)
//		TableColumnLayout tableLayout = new TableColumnLayout();
		TableColumn c;

		c = new TableColumn(table, SWT.LEFT);
		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.organisationIDTableColumn.text")); //$NON-NLS-1$
		c.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.organisationIDTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(c, new ColumnWeightData(10, 50));
		addWeightedColumn(15);

//		createArticleContainerIDPrefixTableColumn(tableViewer, table);
//		createArticleContainerIDTableColumn(tableViewer, table);

		c = new TableColumn(table, SWT.LEFT);
		c.setText(getIDColumnText());
		c.setToolTipText(getIDColumnText());
		addWeightedColumn(10);

		c = new TableColumn(table, SWT.LEFT);
		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.customerTableColumn.text")); //$NON-NLS-1$
		c.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.customerTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(c, new ColumnWeightData(8, 50));
		addWeightedColumn(15);

		c = new TableColumn(table, SWT.LEFT);
		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.vendorTableColumn.text")); //$NON-NLS-1$
		c.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.vendorTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(c, new ColumnWeightData(9, 50));
		addWeightedColumn(15);

		c = new TableColumn(table, SWT.LEFT);
		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.createDateTableColumn.text")); //$NON-NLS-1$
		c.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.createDateTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(c, new ColumnWeightData(10));
		addWeightedColumn(9);

		c = new TableColumn(table, SWT.LEFT);
		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.createUserTableColumn.text")); //$NON-NLS-1$
		c.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.createUserTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(c, new ColumnWeightData(10));
		addWeightedColumn(10);

		c = new TableColumn(table, SWT.LEFT);
		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.amountTableColumn.text")); //$NON-NLS-1$
		c.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.amountTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(c, new ColumnWeightData(10));
		addWeightedColumn(5);

		if (Statable.class.isAssignableFrom(getArticleContainerClass())) {
			c = new TableColumn(table, SWT.LEFT);
			c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.currentStateTableColumn.text")); //$NON-NLS-1$
			c.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.currentStateTableColumn.text")); //$NON-NLS-1$
//			tableLayout.setColumnData(c, new ColumnWeightData(10));
			addWeightedColumn(10);
		}

//		table.setLayout(new WeightedTableLayout(new int[] {10, 10, 10, 10, 10, 10, 10, 10}));

		createAdditionalTableColumns(tableViewer, table);

		getTable().setLayout( new WeightedTableLayout(getPrimitiveArray(weightedColumns), getPrimitiveArray(fixedColumns)) );
	}

	private int[] getPrimitiveArray(List<Integer> list)
	{
		int[] primitiveArray = new int[list.size()];
		int i = 0;
		for (Integer value : list)
		{
			primitiveArray[i] = (value == null || value < 0) ? -1 : value;
			i++;
		}
		return primitiveArray;
	}

	protected abstract Class<? extends ArticleContainer> getArticleContainerClass();
	protected abstract void createAdditionalTableColumns(TableViewer tableViewer, Table table);

//	protected abstract void createArticleContainerIDPrefixTableColumn(TableViewer tableViewer, Table table);
//	protected abstract void createArticleContainerIDTableColumn(TableViewer tableViewer, Table table);

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider(){
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return AbstractArticleContainerListComposite.this.getColumnImage(element, columnIndex);
			}
			@Override
			public String getColumnText(Object element, int columnIndex) {
				return AbstractArticleContainerListComposite.this.getColumnText(element, columnIndex);
			}
		});
	}

	protected String getCreateUserName(ArticleContainer articleContainer)
	{
		if (articleContainer.getCreateUser() != null) {
			return articleContainer.getCreateUser().getName();
		}

		return ""; //$NON-NLS-1$
	}

	protected String getStateName(Statable statable)
	{
		// I think we need to look for the newest State in both, statableLocal and statable! Marco.
		StatableLocal statableLocal = statable.getStatableLocal();
		State state = statable.getState();
		State state2 = statableLocal.getState();
		if (state2 != null) {
			if (state == null)
				state = state2;
			else if (state.getCreateDT().compareTo(state2.getCreateDT()) < 0)
				state = state2;
		}

		if (state != null)
			return state.getStateDefinition().getName().getText();

		return ""; //$NON-NLS-1$
	}

	protected Image getColumnImage(Object element, int columnIndex)
	{
		int firstAdditionalColumnIndex = 7;
		if (Statable.class.isAssignableFrom(getArticleContainerClass()))
			firstAdditionalColumnIndex = 8;

		int additionalColumnIndex = columnIndex - firstAdditionalColumnIndex;
		if (additionalColumnIndex < 0)
			return null;

		return getAdditionalColumnImage(element, additionalColumnIndex, firstAdditionalColumnIndex, columnIndex);
	}

	protected String getColumnText(Object element, int columnIndex)
	{
		if (element instanceof ArticleContainer) {
			ArticleContainer articleContainer = (ArticleContainer) element;
			switch (columnIndex) {
				case 0:
					return articleContainer.getOrganisationID();
				case 1:
					return ArticleContainerUtil.getArticleContainerID(articleContainer);
				case 2:
					return articleContainer.getCustomer().getPerson().getDisplayName();
				case 3:
					return articleContainer.getVendor().getPerson().getDisplayName();
				case 4:
					return DateFormatter.formatDateShortTimeHM(articleContainer.getCreateDT(), false);
				case 5:
					return getCreateUserName(articleContainer);
				case 6:
					return String.valueOf(articleContainer.getArticleCount());
			}
		}
		int firstAdditionalColumnIndex = 7;
		if (Statable.class.isAssignableFrom(getArticleContainerClass()))
			firstAdditionalColumnIndex = 8;

		if (element instanceof Statable && columnIndex == 7) {
			Statable statable = (Statable) element;
			return getStateName(statable);
		}
		if (columnIndex == 0)
			return String.valueOf(element);

		int additionalColumnIndex = columnIndex - firstAdditionalColumnIndex;
		if (additionalColumnIndex < 0)
			return ""; //$NON-NLS-1$

		return getAdditionalColumnText(element, additionalColumnIndex, firstAdditionalColumnIndex, columnIndex);
	}

	/**
	 * @param element The element for which to obtain a column's text value
	 * @param additionalColumnIndex The 0-based index for additional columns. It is the result of: <code>columnIndex - firstAdditionalColumnIndex</code>
	 * @param firstAdditionalColumnIndex The absolute column index of the first additional column.
	 * @param columnIndex The absolute column index (0-based).
	 * @return the String to display in the given column for the given element. Must not be <code>null</code>!
	 */
	protected abstract String getAdditionalColumnText(Object element, int additionalColumnIndex, int firstAdditionalColumnIndex, int columnIndex);

	/**
	 * @param element The element for which to obtain a column's image
	 * @param additionalColumnIndex The 0-based index for additional columns. It is the result of: <code>columnIndex - firstAdditionalColumnIndex</code>
	 * @param firstAdditionalColumnIndex The absolute column index of the first additional column.
	 * @param columnIndex The absolute column index (0-based).
	 * @return the <code>Image</code> to display in the given column for the given element or <code>null</code> if no image should be shown.
	 */
	protected Image getAdditionalColumnImage(Object element, int additionalColumnIndex, int firstAdditionalColumnIndex, int columnIndex) {
		return null;
	}
}

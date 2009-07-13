package org.nightlabs.jfire.trade.ui.overview;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.util.BaseComparator;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractArticleContainerListComposite<O extends ArticleContainer>
extends AbstractTableComposite<O>
{
	
	public static final Comparator<ArticleContainer> ARTICLE_CONTAINER_CREATE_DT_COMPARATOR = new Comparator<ArticleContainer>(){
		@Override
		public int compare(ArticleContainer o1, ArticleContainer o2)
		{
			int result = BaseComparator.comparatorNullCheck(o1, o2);
			if (result == BaseComparator.COMPARE_RESULT_NOT_NULL) {
				int result2 = BaseComparator.comparatorNullCheck(o1.getCreateDT(), o2.getCreateDT());
				if (result2 == BaseComparator.COMPARE_RESULT_NOT_NULL) {
					return TableLabelProvider.DATE_COMPARATOR.compare(o1.getCreateDT(), o2.getCreateDT());
				}
				return result2;
			}
			return result;
		}
	};

	public static final Comparator<Price> PRICE_COMPARATOR = new Comparator<Price>(){
		@Override
		public int compare(Price o1, Price o2)
		{
			int result = BaseComparator.comparatorNullCheck(o1, o2);
			if (result == BaseComparator.COMPARE_RESULT_NOT_NULL) {
				return (int) (o1.getAmount() - o2.getAmount());
			}
			return result;
		}
	};

	public static final Comparator<ArticleContainer> ARTICLE_COUNT_COMPARATOR = new Comparator<ArticleContainer>(){
		@Override
		public int compare(ArticleContainer o1, ArticleContainer o2)
		{
			return Integer.valueOf(o1.getArticleCount()).compareTo(o2.getArticleCount());
		}
	};
	
	public class LabelProvider extends TableLabelProvider
	{
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return AbstractArticleContainerListComposite.this.getColumnImage(element, columnIndex);
		}
		@Override
		public String getColumnText(Object element, int columnIndex) {
			return AbstractArticleContainerListComposite.this.getColumnText(element, columnIndex);
		}
		@Override
		public Comparator<?> getColumnComparator(Object element, int columnIndex) {
			return AbstractArticleContainerListComposite.this.getColumnComparator(element, columnIndex);
		}
	}

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

//		c = new TableColumn(table, SWT.LEFT);
//		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.organisationIDTableColumn.text")); //$NON-NLS-1$
//		c.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.organisationIDTableColumn.text")); //$NON-NLS-1$
////		tableLayout.setColumnData(c, new ColumnWeightData(10, 50));
//		addWeightedColumn(15);

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
		addWeightedColumn(12);

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
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
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

	protected Comparator<?> getColumnComparator(Object element, int columnIndex) {
		int firstAdditionalColumnIndex = getFirstAdditionalColumnIndex();
		if (columnIndex >= firstAdditionalColumnIndex) {
			return getAdditionalColumnComparator(element, columnIndex - firstAdditionalColumnIndex, firstAdditionalColumnIndex, columnIndex);
		}
		switch (columnIndex) {
			case 0: return ArticleContainerUtil.ARTICLE_CONTAINER_COMPARATOR;
			case 3: return ARTICLE_CONTAINER_CREATE_DT_COMPARATOR;
			case 5: return ARTICLE_COUNT_COMPARATOR;
			default: return null;
		}
	}

	protected Image getColumnImage(Object element, int columnIndex)
	{
		int firstAdditionalColumnIndex = 6;
		if (Statable.class.isAssignableFrom(getArticleContainerClass()))
			firstAdditionalColumnIndex = 7;

		int additionalColumnIndex = columnIndex - firstAdditionalColumnIndex;
		if (additionalColumnIndex < 0)
			return null;

		return getAdditionalColumnImage(element, additionalColumnIndex, firstAdditionalColumnIndex, columnIndex);
	}

	protected String formatDate(Date date) {
		return DateFormatter.formatDateShortTimeHM(date, true);
	}

//	protected String getColumnText(Object element, int columnIndex)
//	{
//		if (element instanceof ArticleContainer) {
//			ArticleContainer articleContainer = (ArticleContainer) element;
//			switch (columnIndex) {
//				case 0:
//					return articleContainer.getOrganisationID();
//				case 1:
//					return ArticleContainerUtil.getArticleContainerID(articleContainer);
//				case 2:
//					return articleContainer.getCustomer().getPerson().getDisplayName();
//				case 3:
//					return articleContainer.getVendor().getPerson().getDisplayName();
//				case 4:
//					return formatDate(articleContainer.getCreateDT());
//				case 5:
//					return getCreateUserName(articleContainer);
//				case 6:
//					return String.valueOf(articleContainer.getArticleCount());
//			}
//		}
//		int firstAdditionalColumnIndex = 7;
//		if (Statable.class.isAssignableFrom(getArticleContainerClass()))
//			firstAdditionalColumnIndex = 8;
//
//		if (element instanceof Statable && columnIndex == 7) {
//			Statable statable = (Statable) element;
//			return getStateName(statable);
//		}
//		if (columnIndex == 0)
//			return String.valueOf(element);
//
//		int additionalColumnIndex = columnIndex - firstAdditionalColumnIndex;
//		if (additionalColumnIndex < 0)
//			return ""; //$NON-NLS-1$
//
//		return getAdditionalColumnText(element, additionalColumnIndex, firstAdditionalColumnIndex, columnIndex);
//	}
	protected int getFirstAdditionalColumnIndex() {
		int firstAdditionalColumnIndex = 6;
		if (Statable.class.isAssignableFrom(getArticleContainerClass()))
			firstAdditionalColumnIndex = 7;
		return firstAdditionalColumnIndex;
	}
	
	protected String getColumnText(Object element, int columnIndex)
	{
		if (element instanceof ArticleContainer) {
			ArticleContainer articleContainer = (ArticleContainer) element;
			switch (columnIndex) {
				case 0:
					return ArticleContainerUtil.getArticleContainerID(articleContainer);
				case 1:
					return articleContainer.getCustomer().getPerson().getDisplayName();
				case 2:
					return articleContainer.getVendor().getPerson().getDisplayName();
				case 3:
					return formatDate(articleContainer.getCreateDT());
				case 4:
					return getCreateUserName(articleContainer);
				case 5:
					return String.valueOf(articleContainer.getArticleCount());
			}
		}
		
		int firstAdditionalColumnIndex = getFirstAdditionalColumnIndex();
		
		if (element instanceof Statable && columnIndex == 6) {
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
	 * @return the <code>Comparator</code> to use for the given column, or null to use the default comparator.
	 */
	protected Comparator<?> getAdditionalColumnComparator(Object element, int additionalColumnIndex, int firstAdditionalColumnIndex, int columnIndex) {
		return null;
	}
	
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

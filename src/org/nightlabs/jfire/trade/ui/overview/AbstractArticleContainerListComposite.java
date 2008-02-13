package org.nightlabs.jfire.trade.ui.overview;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractArticleContainerListComposite
extends AbstractTableComposite<ArticleContainer>
{
	/**
	 * @param parent
	 * @param style
	 */
	public AbstractArticleContainerListComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableLayout tableLayout = new TableLayout();
		TableColumn c;

		c = new TableColumn(table, SWT.LEFT);
		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.organisationIDTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));

		createArticleContainerIDPrefixTableColumn(tableViewer, table, tableLayout);
		createArticleContainerIDTableColumn(tableViewer, table, tableLayout);

		c = new TableColumn(table, SWT.LEFT);
		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.customerTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));

		c = new TableColumn(table, SWT.LEFT);
		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.vendorTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));

		c = new TableColumn(table, SWT.LEFT);
		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.createDateTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));

		c = new TableColumn(table, SWT.LEFT);
		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.createUserTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));

		c = new TableColumn(table, SWT.LEFT);
		c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.amountTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));

		if (Statable.class.isAssignableFrom(getArticleContainerClass())) {
			c = new TableColumn(table, SWT.LEFT);
			c.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite.currentStateTableColumn.text")); //$NON-NLS-1$
			tableLayout.addColumnData(new ColumnWeightData(10));
		}

//		table.setLayout(new WeightedTableLayout(new int[] {10, 10, 10, 10, 10, 10, 10, 10}));
		createAdditionalTableColumns(tableViewer, table, tableLayout);

		table.setLayout(tableLayout);
	}

	protected abstract Class<? extends ArticleContainer> getArticleContainerClass();
	protected abstract void createArticleContainerIDPrefixTableColumn(TableViewer tableViewer, Table table, TableLayout tableLayout);
	protected abstract void createArticleContainerIDTableColumn(TableViewer tableViewer, Table table, TableLayout tableLayout);
	protected abstract void createAdditionalTableColumns(TableViewer tableViewer, Table table, TableLayout tableLayout);

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider(){
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				return AbstractArticleContainerListComposite.this.getColumnImage(element, columnIndex);
			}
			@Implement
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
		int firstAdditionalColumnIndex = 8;
		if (Statable.class.isAssignableFrom(getArticleContainerClass()))
			firstAdditionalColumnIndex = 9;

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
					return articleContainer.getArticleContainerIDPrefix();
				case 2:
					return articleContainer.getArticleContainerIDAsString();
//					return ""+articleContainer.getArticleContainerID();
				case 3:
					return articleContainer.getCustomer().getPerson().getDisplayName();
				case 4:
					return articleContainer.getVendor().getPerson().getDisplayName();
				case 5:
					return DateFormatter.formatDateShort(articleContainer.getCreateDT(), false);
				case 6:
					return getCreateUserName(articleContainer);
				case 7:
					return String.valueOf(articleContainer.getArticleCount());
			}
		}
		int firstAdditionalColumnIndex = 8;
		if (Statable.class.isAssignableFrom(getArticleContainerClass()))
			firstAdditionalColumnIndex = 9;

		if (element instanceof Statable && columnIndex == 8) {
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

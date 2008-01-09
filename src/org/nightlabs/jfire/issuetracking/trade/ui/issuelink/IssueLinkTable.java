package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.query.OfferQuery;
import org.nightlabs.jfire.trade.ui.articlecontainer.OfferDAO;
import org.nightlabs.progress.NullProgressMonitor;

public class IssueLinkTable extends AbstractTableComposite<String> {

	public static final String[] FETCH_GROUPS_OFFERS = new String[] {
		FetchPlan.DEFAULT, 
		Offer.FETCH_GROUP_THIS_OFFER,
		Offer.FETCH_GROUP_OFFER_LOCAL,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON
	};

	public IssueLinkTable(Composite parent, int style)
	{
		super(parent, style);

		loadObjects();
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tc;
		TableLayout layout = new TableLayout();

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Object ID");
		layout.addColumnData(new ColumnWeightData(30));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Type");
		layout.addColumnData(new ColumnWeightData(40));

		table.setLayout(layout);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new IssueListLabelProvider());
		tableViewer.setContentProvider(new TableContentProvider());
	}

	private void loadObjects(){
		try {
			TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();		

			List<JDOQuery> queries = new ArrayList<JDOQuery>();
			queries.add(new OfferQuery());
			Set<OfferID> offerIDs = tradeManager.getOfferIDs(queries);
			setInput(OfferDAO.sharedInstance().getOffers(offerIDs, FETCH_GROUPS_OFFERS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()));
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	class IssueListLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex) 
		{
			if (element instanceof Offer) {
				Offer offer = (Offer) element;
				switch (columnIndex) 
				{
				case(0):
					return JDOHelper.getObjectId(offer).toString();
				case(1):
					return "Offer";
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}		
	}
}
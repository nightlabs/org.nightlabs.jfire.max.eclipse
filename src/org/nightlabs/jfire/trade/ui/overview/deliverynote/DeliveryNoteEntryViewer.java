package org.nightlabs.jfire.trade.ui.overview.deliverynote;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuery;
import org.nightlabs.jfire.trade.ui.overview.ArticleContainerEntryViewer;
import org.nightlabs.jfire.trade.ui.overview.deliverynote.action.EditDeliveryNoteAction;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class DeliveryNoteEntryViewer
	extends ArticleContainerEntryViewer<DeliveryNote, DeliveryNoteQuery>
{
	public static final String ID = DeliveryNoteEntryViewer.class.getName();
	public static String[] FETCH_GROUPS_DELIVERY_NOTES = new String[] {
		FetchPlan.DEFAULT,
		DeliveryNote.FETCH_GROUP_THIS_DELIVERY_NOTE,
		DeliveryNote.FETCH_GROUP_DELIVERY_NOTE_LOCAL,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON
	};
	
	public DeliveryNoteEntryViewer(Entry entry) {
		super(entry);
	}

	private DeliveryNoteListComposite list;
	
	@Override
	public AbstractTableComposite<DeliveryNote> createListComposite(Composite parent)
	{
		list = new DeliveryNoteListComposite(parent, SWT.NONE);
		return list;
	}

	@Override
	protected void addResultTableListeners(AbstractTableComposite<DeliveryNote> tableComposite) {
		super.addResultTableListeners(tableComposite);
		list.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				EditDeliveryNoteAction editAction = new EditDeliveryNoteAction();
				editAction.setSelection(list.getSelection());
				editAction.run();
			}
		});
	}
	
	public String getID() {
		return ID;
	}
		
	@Override
	protected Collection<DeliveryNote> doSearch(
		QueryCollection<DeliveryNote, ? extends DeliveryNoteQuery> queryMap, ProgressMonitor monitor)
	{
		return ArticleContainerDAO.sharedInstance().getArticleContainersForQueries(
			queryMap,
			FETCH_GROUPS_DELIVERY_NOTES, 
			NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
			monitor
			);
	}

	@Override
	protected Class<DeliveryNote> getResultType()
	{
		return DeliveryNote.class;
	}

}

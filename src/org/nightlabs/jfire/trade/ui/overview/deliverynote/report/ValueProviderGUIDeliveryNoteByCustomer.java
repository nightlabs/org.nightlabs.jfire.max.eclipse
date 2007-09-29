/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.deliverynote.report;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.reporting.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.DeliveryNoteLocal;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.query.ArticleContainerQuery;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.DeliveryNoteDAO;
import org.nightlabs.jfire.trade.ui.overview.deliverynote.DeliveryNoteListComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class ValueProviderGUIDeliveryNoteByCustomer 
extends AbstractValueProviderGUI 
{
	public static final String[] FETCH_GROUPS_DELIVERY_NOTES = new String[] {
		FetchPlan.DEFAULT,
		DeliveryNote.FETCH_GROUP_THIS_DELIVERY_NOTE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON,
		DeliveryNoteLocal.FETCH_GROUP_THIS_DELIVERY_NOTE_LOCAL
	};
	
	private DeliveryNoteListComposite deliveryNoteListComposite = null;
	
	public ValueProviderGUIDeliveryNoteByCustomer(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}
	
	public Control createGUI(Composite wrapper) {
		deliveryNoteListComposite = new DeliveryNoteListComposite(wrapper, SWT.NONE);
		deliveryNoteListComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent arg0) {
				notifyOutputChanged();
			}
		});
		return deliveryNoteListComposite;
	}

	public Object getOutputValue() {
		if (deliveryNoteListComposite.getSelectedElements().size() >= 1)
			return JDOHelper.getObjectId(deliveryNoteListComposite.getSelectedElements().iterator().next());
		return null;
	}

	public boolean isAcquisitionComplete() {
		return getOutputValue() != null;
	}

	public void setInputParameterValue(String parameterID, final Object value) {
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.overview.deliverynote.report.ValueProviderGUIDeliveryNoteByCustomer.loadDeliveryNotesJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				ArticleContainerQuery query = new ArticleContainerQuery(DeliveryNote.class);
				query.setCustomerID((AnchorID) value);
				Collection<ArticleContainerQuery> qs = new HashSet<ArticleContainerQuery>();
				qs.add(query);
				Set<DeliveryNoteID> deliveryNoteIDs = null;
				try {
					deliveryNoteIDs = TradePlugin.getDefault().getTradeManager().getArticleContainerIDs(qs);
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}
				final Collection<DeliveryNote> deliveryNotes = (Collection<DeliveryNote>) DeliveryNoteDAO.
					sharedInstance().getDeliveryNotes(deliveryNoteIDs, FETCH_GROUPS_DELIVERY_NOTES, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						deliveryNoteListComposite.setInput(deliveryNotes);
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}

}

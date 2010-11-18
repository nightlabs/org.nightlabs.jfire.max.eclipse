package org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.internal.forms.widgets.SWTUtil;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.accounting.dao.PaymentDAO;
import org.nightlabs.jfire.accounting.pay.ModeOfPaymentFlavour;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.accounting.pay.id.PaymentID;
import org.nightlabs.jfire.base.jdo.JDOObjectsChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectTableComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.GlobalDateFormatter;
import org.nightlabs.l10n.GlobalNumberFormatter;
import org.nightlabs.l10n.IDateFormatter;
import org.nightlabs.progress.ProgressMonitor;

/**
 * 
 * @author Fitas Amine - fitas at NightLabs dot de
 */
public class InvoicePaymentsListTable 
extends ActiveJDOObjectTableComposite<PaymentID, Payment>
{
	private ObjectID payableObjectID = null;

	public void setPayableObjectID(final ObjectID payableObjectID)
	{
		this.payableObjectID = payableObjectID;
		load();
	}

	public InvoicePaymentsListTable(Composite parent, int style) {
		super(parent, style);	
	}

	@Override
	protected ActiveJDOObjectController<PaymentID, Payment> createActiveJDOObjectController() {	
		return new PaymentsListController();
	}

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new InvoicePaymentsListTableLabelProvider();
	}



	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {

		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoicePaymentsListTable.iDTableColumn.text")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoicePaymentsListTable.dateTableColumn.text")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoicePaymentsListTable.amountTableColumn.text")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoicePaymentsListTable.paymentTableColumn.text")); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[]{1,2,1,1}));
	}

	public static final String[] FETCH_GROUPS_PAYMENT = {
		FetchPlan.DEFAULT,
		Payment.FETCH_GROUP_CURRENCY,
		Payment.FETCH_GROUP_MODE_OF_PAYMENT_FLAVOUR,
		ModeOfPaymentFlavour.FETCH_GROUP_NAME,
		ModeOfPaymentFlavour.FETCH_GROUP_ICON_16X16_DATA
	};

	private class PaymentsListController extends ActiveJDOObjectController<PaymentID, Payment> {

		@Override
		protected Class<? extends Payment> getJDOObjectClass() {
			return Payment.class;
		}

		@Override
		protected Collection<Payment> retrieveJDOObjects(Set<PaymentID> objectIDs, ProgressMonitor monitor) {
			return retrieveJDOObjects(monitor);		
		}

		@Override
		protected Collection<Payment> retrieveJDOObjects(ProgressMonitor monitor) {
			if(payableObjectID != null)
				return PaymentDAO.sharedInstance().getPaymentsForPayableObject(payableObjectID, FETCH_GROUPS_PAYMENT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
			return null;
		}

		@Override
		protected void sortJDOObjects(List<Payment> objects) {
			Collections.sort(objects, new Comparator<Payment>() {
				@Override
				public int compare(Payment o1, Payment o2)
				{
					int res = o1.getPaymentDT().compareTo(o2.getPaymentDT());
					if (res != 0)
						return res;

					if (o1.getOrganisationID().equals(o2.getOrganisationID())) {
						res = o1.getPaymentID() < o2.getPaymentID() ? -1 : (o1.getPaymentID() == o2.getPaymentID() ? 0 : 1);
					}

					return res;
				}

			});
		}
		@Override
		protected void onJDOObjectsChanged(JDOObjectsChangedEvent<PaymentID, Payment> event) {
		}
	}

	class InvoicePaymentsListTableLabelProvider
	extends TableLabelProvider
	{

		public Image getColumnImage(Object element, int columnIndex) {
			if (element instanceof Payment) {
				Payment payment = (Payment) element;
				switch (columnIndex) {
				case 3:
					if (payment.getModeOfPaymentFlavour()!= null)
					{
						byte[] iconData = payment.getModeOfPaymentFlavour().getIcon16x16Data();

						if (iconData == null)
							return null;
						ByteArrayInputStream in = new ByteArrayInputStream(iconData);
						Image image = new Image(SWTUtil.getStandardDisplay(), in);
						return image;
					}
					else
						return null;

				default:
					return null;
				}
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof Payment) {
				Payment payment = (Payment) element;
				switch (columnIndex) {
				case 0:
					return Long.toString(payment.getPaymentID());
				case 1:
					return  GlobalDateFormatter.sharedInstance().formatDate(payment.getPaymentDT(), IDateFormatter.FLAGS_DATE_LONG_TIME_HM);
				case 2:
					if (payment.getCurrency()!= null)
						return GlobalNumberFormatter.sharedInstance().formatCurrency(payment.getAmount(),payment.getCurrency());
				case 3:
					if (payment.getModeOfPaymentFlavour()!= null)
						return	payment.getModeOfPaymentFlavour().getName().getText();
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}
	}
}

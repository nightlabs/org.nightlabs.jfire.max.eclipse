/**
 *
 */
package org.nightlabs.jfire.trade.ui.reserve;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.jfire.store.id.ProductTypeID;

/**
 * @author daniel
 *
 */
public class ReservationEditor extends EditorPart
{
	public static final String EDITOR_ID = ReservationEditor.class.getName();

//	private ReservationTable reservationComposite;
	private ReservationComposite reservationComposite;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor arg0) {

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException
	{
		setSite(site);
		setInput(input);
		setPartName(getEditorInput().getName());
	}

//	private void loadReservations(IEditorInput input)
//	{
//		if (input instanceof ReservationEditorInput) {
//			ReservationEditorInput editorInput = (ReservationEditorInput) input;
//			ProductTypeID productTypeID = editorInput.getProductTypeID();
//
//			QueryCollection<AbstractJDOQuery> queryCollection = new QueryCollection<AbstractJDOQuery>(Offer.class);
//			OfferQuery offerQuery = new OfferQuery();
//			offerQuery.setReserved(true);
//			offerQuery.setProductTypeID(productTypeID);
//			queryCollection.add(offerQuery);
//
//			try {
//				Collection<Offer> offers = OfferDAO.sharedInstance().getOffersByQuery(
//						queryCollection, ReservationTable.FETCH_GROUP_RESERVATIONS,
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//				reservationComposite.setInput(offers);
//			}
//			catch (Exception e)
//			{
//				throw new RuntimeException(e);
//			}
//		}
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		reservationComposite = new ReservationComposite(parent, SWT.NONE);
//		loadReservations(getEditorInput());
		IEditorInput input = getEditorInput();
		if (input instanceof ReservationEditorInput) {
			ReservationEditorInput editorInput = (ReservationEditorInput) input;
			ProductTypeID productTypeID = editorInput.getProductTypeID();
			reservationComposite.setInput(productTypeID, editorInput.getProductTypeName());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
		reservationComposite.setFocus();
	}

}

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.util.Util;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 */
public class ArticleContainerEditorInputRecurringOffer
extends ArticleContainerEditorInput
implements IEditorInput{

	private OfferID offerID;

	private static final String IMAGE = "icons/articlecontainer/detail/recurringoffer/ArticleContainerEditorInputRecurringOffer.16x16.png"; //$NON-NLS-1$

	
	public ArticleContainerEditorInputRecurringOffer()
	{
	}

	public ArticleContainerEditorInputRecurringOffer(OfferID offerID)
	{
		this.offerID = offerID;
	}
	
	/**
	 * @return Returns the offerID.
	 */
	public OfferID getOfferID()
	{
		return offerID;
	}
	
	
	@Override
	public ArticleContainerID getArticleContainerID() {
		// TODO Auto-generated method stub
		return getOfferID();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;

		if (!(obj instanceof ArticleContainerEditorInputRecurringOffer))
			return false;

		ArticleContainerEditorInputRecurringOffer other = (ArticleContainerEditorInputRecurringOffer)obj;

		return Util.equals(this.offerID, other.offerID);
	}
	
	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, IMAGE);
	}
	
	
	public String getName()
	{
		return String.format(
				"RecurringOffer %s", //$NON-NLS-1$" +
				(offerID == null ? "" : offerID.offerIDPrefix + '/' + ObjectIDUtil.longObjectIDFieldToString(offerID.offerID))); //$NON-NLS-1$
	}
	

}

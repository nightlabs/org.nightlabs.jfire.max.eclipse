package org.nightlabs.jfire.simpletrade.ui.detail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.datafield.II18nTextDataField;
import org.nightlabs.jfire.prop.datafield.ImageDataField;
import org.nightlabs.jfire.prop.id.StructFieldID;
import org.nightlabs.jfire.simpletrade.store.prop.SimpleProductTypeStruct;
import org.nightlabs.jfire.simpletrade.ui.SimpletradePlugin;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.detail.AbstractProductTypeDetailViewComposite;
import org.nightlabs.progress.ProgressMonitor;

/**
 * This is the Composite which is used by the {@link SimpleProductTypeDetailView}
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class SimpleProductTypeDetailViewComposite
extends AbstractProductTypeDetailViewComposite
{
	private Logger logger = Logger.getLogger(SimpleProductTypeDetailViewComposite.class);
	public SimpleProductTypeDetailViewComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
	}

	public SimpleProductTypeDetailViewComposite(Composite parent, int style) {
		super(parent, style);
	}

	public static final String[] FETCH_GROUP_PRODUCT_TYPE_DETAIL = new String[] {
		ProductType.FETCH_GROUP_NAME, ProductType.FETCH_GROUP_OWNER, ProductType.FETCH_GROUP_VENDOR,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_GROUPS};

	private String description;
	private byte[] img;
	
	@Override
	protected ProductType fetchProductType(ProductTypeID productTypeID,
			ProgressMonitor monitor) {
		
		
		ProductType productType = ProductTypeDAO.sharedInstance().getProductType(productTypeID,
				FETCH_GROUP_PRODUCT_TYPE_DETAIL, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		
		description = null;
		img = null;
		
		Set<ProductTypeID> ids = new HashSet<ProductTypeID>(Arrays.asList(new ProductTypeID[] {productTypeID}));
		Set<StructFieldID> fields = new HashSet<StructFieldID>(Arrays.asList(new StructFieldID[] {
				SimpleProductTypeStruct.DESCRIPTION_LONG, SimpleProductTypeStruct.IMAGES_SMALL_IMAGE}
		));
		Map<ProductTypeID, PropertySet> propertySets = null;
		propertySets = SimpletradePlugin.getSimpleTradeManager().getSimpleProductTypesPropertySets(
				ids, fields, new String[] {PropertySet.FETCH_GROUP_FULL_DATA}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
			);
		final PropertySet props = propertySets.get(productTypeID);
		IStruct struct = StructLocalDAO.sharedInstance().getStructLocal(props.getStructLocalObjectID(), monitor);
		if (props != null)
			props.inflate(struct);
		
		II18nTextDataField desc = null;
		try {
			if (props != null)
				desc = props.getDataField(SimpleProductTypeStruct.DESCRIPTION_LONG, II18nTextDataField.class);
		} catch (Exception e) {
			logger.warn("Loading image from propertySet failed!", e); //$NON-NLS-1$
		}
		description = desc == null ? "" : desc.getI18nText().getText(); //$NON-NLS-1$
		
		ImageDataField smallImg = null;
		try {
			if (props != null)
				smallImg = (ImageDataField) props.getDataField(SimpleProductTypeStruct.IMAGES_SMALL_IMAGE);
		} catch (Exception e) {
			logger.warn("Loading image from propertySet failed!", e); //$NON-NLS-1$
		}
		if (smallImg != null) {
			img = smallImg.getContent();
		}
		
		return productType;
	}

	@Override
	protected String fetchProductTypeDescription(ProductType productType,
			ProgressMonitor monitor) {
		return description;
	}

	@Override
	protected byte[] fetchProductTypeImage(ProductType productType,
			ProgressMonitor monitor) {
		return img;
	}

	@Override
	protected String fetchProductTypeName(ProductType productType,
			ProgressMonitor monitor) {
		return productType.getName().getText();
	}
}

package org.nightlabs.jfire.dynamictrade.ui.detail;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.jfire.prop.DataBlock;
import org.nightlabs.jfire.prop.DataBlockGroup;
import org.nightlabs.jfire.prop.DataField;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.prop.datafield.ImageDataField;
import org.nightlabs.jfire.prop.exception.DataBlockNotFoundException;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.detail.AbstractProductTypeDetailViewComposite;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * This is the Composite which is used by the {@link DynamicProductTypeDetailView}
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class DynamicProductTypeDetailViewComposite
extends AbstractProductTypeDetailViewComposite
{

	public DynamicProductTypeDetailViewComposite(Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
	}

	public DynamicProductTypeDetailViewComposite(Composite parent, int style) {
		super(parent, style);
	}

	public static final String[] FETCH_GROUP_PRODUCT_TYPE_DETAIL = new String[] {
		ProductType.FETCH_GROUP_NAME, ProductType.FETCH_GROUP_OWNER, ProductType.FETCH_GROUP_VENDOR,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_GROUPS, ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID,
		DynamicProductType.FETCH_GROUP_PROPERTY_SET, PropertySet.FETCH_GROUP_FULL_DATA
	};

	public static final String[] FETCH_GROUP_PRODUCT_TYPE_CATEGORY = new String[] {
		ProductType.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_NO_LIMIT
	};

	private String description;
	private byte[] img;
	
	@Override
	protected ProductType fetchProductType(ProductTypeID productTypeID,
			ProgressMonitor monitor) {
		final DynamicProductType productType = (DynamicProductType) ProductTypeDAO.sharedInstance().getProductType(
				productTypeID,
				FETCH_GROUP_PRODUCT_TYPE_DETAIL,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new SubProgressMonitor(monitor, 50)
		);
		
		description = null;
		img = null;
		
		final DynamicProductType category = (DynamicProductType) ProductTypeDAO.sharedInstance().getProductType(
				productType.getExtendedProductTypeID(),
				FETCH_GROUP_PRODUCT_TYPE_CATEGORY,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new SubProgressMonitor(monitor, 50)
		);
		
		StringBuilder categoryText = new StringBuilder();
		ProductType cat = category;
		while (cat != null) {
			if (categoryText.length() != 0)
				categoryText.insert(0, Messages.getString("org.nightlabs.jfire.dynamictrade.ui.detail.DynamicProductTypeDetailViewComposite.categoryText.separator")); //$NON-NLS-1$

			categoryText.insert(0, cat.getName().getText());
			cat = cat.getExtendedProductType();
		}
		description = categoryText.toString();
		
		if (productType.getPropertySet() != null) {
			PropertySet props = productType.getPropertySet(); 
			IStruct struct = StructLocalDAO.sharedInstance().getStructLocal(props.getStructLocalObjectID(), monitor);
			if (props != null)
				props.inflate(struct);
			
			Collection<DataBlockGroup> dataBlockGroups = props.getDataBlockGroups();
			imageSearch: for (DataBlockGroup dataBlockGroup : dataBlockGroups) {
				DataBlock block = null;
				try {
					block = dataBlockGroup.getDataBlockByIndex(0);
				} catch (DataBlockNotFoundException e) {
					continue imageSearch;
				}
				Collection<DataField> dataFields = block.getDataFields();
				for (DataField dataField : dataFields) {
					if (dataField instanceof ImageDataField) {
						img = ((ImageDataField)dataField).getContent();
						break imageSearch;
					}
				}
			}
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

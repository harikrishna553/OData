package com.app.util;

import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;

import com.app.service.DemoEdmProvider;


/**
 * Responsible to perform CRUD operations on entities.
 * 
 * @author Krishna
 *
 */
public class EntityUtil {

	public static EntityCollection getData(EdmEntitySet edmEntitySet) {
		if (DemoEdmProvider.ES_PRODUCTS_NAME.equals(edmEntitySet.getName())) {
			return ProductUtil.getProductsData();
		}

		return null;
	}

	public static Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
			throws ODataApplicationException {

		EdmEntityType edmEntityType = edmEntitySet.getEntityType();

		// actually, this is only required if we have more than one Entity Type
		if (edmEntityType.getName().equals(DemoEdmProvider.ET_PRODUCT_NAME)) {
			return ProductUtil.getProduct(edmEntityType, keyParams);
		}

		return null;
	}

}

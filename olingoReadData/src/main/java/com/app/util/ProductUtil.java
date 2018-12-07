package com.app.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;

import com.app.model.Product;

public class ProductUtil {
	private static List<Product> products = new ArrayList<>();

	static {
		Product prod1 = new Product(1, "Washing Machine by AVC", "Good for morth east countries", 876544);
		Product prod2 = new Product(2, "XYZ Camera", "Catch pics while travelling", 965987);

		products.add(prod1);
		products.add(prod2);
	}

	private static Entity getProductEntity(final Product product) {
		Entity entity = new Entity().addProperty(new Property(null, "ID", ValueType.PRIMITIVE, product.getId()))
				.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, product.getName()))
				.addProperty(new Property(null, "Description", ValueType.PRIMITIVE, product.getDescription()))
				.addProperty(new Property(null, "Price", ValueType.PRIMITIVE, product.getPrice()));
		entity.setId(createId("Products", product.getId()));
		return entity;
	}

	public static EntityCollection getProductsData() {
		EntityCollection dataToSend = new EntityCollection();
		List<Entity> productList = dataToSend.getEntities();

		for (Product prod : products) {
			productList.add(getProductEntity(prod));
		}

		return dataToSend;
	}

	private static URI createId(final String entitySetName, final Object id) {
		try {
			return new URI(entitySetName + "(" + String.valueOf(id) + ")");
		} catch (URISyntaxException e) {
			throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
		}
	}

	public static Entity getProduct(EdmEntityType edmEntityType, List<UriParameter> keyParams)
			throws ODataApplicationException {
		EntityCollection entitySet = getProductsData();

		Entity requestedEntity = findEntity(edmEntityType, entitySet, keyParams);

		if (requestedEntity == null) {
			throw new ODataApplicationException("Entity for requested key doesn't exist",
					HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
		}

		return requestedEntity;
	}

	public static Entity findEntity(EdmEntityType edmEntityType, EntityCollection entitySet,
			List<UriParameter> keyParams) throws ODataApplicationException {

		List<Entity> entityList = entitySet.getEntities();

		for (Entity entity : entityList) {
			boolean foundEntity = entityMatchesAllKeys(edmEntityType, entity, keyParams);
			if (foundEntity) {
				return entity;
			}
		}

		return null;
	}

	private static String getValueOfTheProperty(EdmEntityType edmEntityType, Entity entity, String propertyName)
			throws ODataApplicationException {
		EdmProperty edmKeyProperty = (EdmProperty) edmEntityType.getProperty(propertyName);

		/*
		 * Get some basic information to convert the property value of this
		 * entity to string
		 */
		Boolean isNullable = edmKeyProperty.isNullable();
		Integer maxLength = edmKeyProperty.getMaxLength();
		Integer precision = edmKeyProperty.getPrecision();
		Boolean isUnicode = edmKeyProperty.isUnicode();
		Integer scale = edmKeyProperty.getScale();

		EdmType edmType = edmKeyProperty.getType();
		Object valueObject = entity.getProperty(propertyName).getValue();

		try {
			EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) edmType;
			return edmPrimitiveType.valueToString(valueObject, isNullable, maxLength, precision, scale, isUnicode);
		} catch (EdmPrimitiveTypeException e) {
			throw new ODataApplicationException("Failed to retrieve String value",
					HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH, e);
		}
	}

	public static boolean entityMatchesAllKeys(EdmEntityType edmEntityType, Entity entity, List<UriParameter> keyParams)
			throws ODataApplicationException {

		/* We need to make sure that all the predicates are matched */
		for (final UriParameter key : keyParams) {
			String keyName = key.getName();
			String keyText = key.getText();

			String valueAsString = getValueOfTheProperty(edmEntityType, entity, keyName);

			if (valueAsString == null || !valueAsString.equals(keyText)) {
				return false;
			}
		}

		return true;
	}

}

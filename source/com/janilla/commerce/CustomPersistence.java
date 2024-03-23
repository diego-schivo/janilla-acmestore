package com.janilla.commerce;

import com.janilla.persistence.Crud;
import com.janilla.persistence.Persistence;

public class CustomPersistence extends Persistence {

	@Override
	protected <E> Crud<E> newCrud(Class<E> type) {
		if (type == Product.class) {
			@SuppressWarnings("unchecked")
			var c = (Crud<E>) new ProductCrud();
			return c;
		}
		return super.newCrud(type);
	}
}

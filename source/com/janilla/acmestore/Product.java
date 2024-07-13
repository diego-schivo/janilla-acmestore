/*
 * MIT License
 *
 * Copyright (c) 2024 Diego Schivo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.janilla.acmestore;

import java.math.BigDecimal;
import java.net.URI;

import com.janilla.persistence.Index;
import com.janilla.persistence.Store;

@Store
@Index
public class Product {

	private long id;

	@Index
	private String handle;

	private boolean availableForSale;

	@Index(keyGetter = Index.KeywordSet.class)
	private String title;

	private String description;

	private String descriptionHtml;

	private ProductOption[] options;

	private URI featuredImage;

	private URI[] images;

	private BigDecimal price;

	@Index
	private long[] collections;

	private boolean hidden;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public boolean isAvailableForSale() {
		return availableForSale;
	}

	public void setAvailableForSale(boolean availableForSale) {
		this.availableForSale = availableForSale;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescriptionHtml() {
		return descriptionHtml;
	}

	public void setDescriptionHtml(String descriptionHtml) {
		this.descriptionHtml = descriptionHtml;
	}

	public ProductOption[] getOptions() {
		return options;
	}

	public void setOptions(ProductOption[] options) {
		this.options = options;
	}

	public URI getFeaturedImage() {
		return featuredImage;
	}

	public void setFeaturedImage(URI featuredImage) {
		this.featuredImage = featuredImage;
	}

	public URI[] getImages() {
		return images;
	}

	public void setImages(URI[] images) {
		this.images = images;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public long[] getCollections() {
		return collections;
	}

	public void setCollections(long[] collections) {
		this.collections = collections;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
}

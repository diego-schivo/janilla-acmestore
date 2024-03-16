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
package com.janilla.commerce;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

import com.janilla.persistence.Index;
import com.janilla.persistence.Store;

@Store
public class CartItem {

	@Index
	private long cart;

	private long id;

	private String title1;

	private String title2;

	private URI image;

	private int quantity;

	private BigDecimal totalAmount;

	private String handle;

	private List<Entry<String, String>> selectedOptions;

	private long variant;

	public long getCart() {
		return cart;
	}

	public void setCart(long cart) {
		this.cart = cart;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle1() {
		return title1;
	}

	public void setTitle1(String title1) {
		this.title1 = title1;
	}

	public String getTitle2() {
		return title2;
	}

	public void setTitle2(String title2) {
		this.title2 = title2;
	}

	public URI getImage() {
		return image;
	}

	public void setImage(URI image) {
		this.image = image;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public List<Entry<String, String>> getSelectedOptions() {
		return selectedOptions;
	}

	public void setSelectedOptions(List<Entry<String, String>> selectedOptions) {
		this.selectedOptions = selectedOptions;
	}

	public long getVariant() {
		return variant;
	}

	public void setVariant(long variant) {
		this.variant = variant;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(id);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || (obj != null && obj.getClass() == getClass() && ((CartItem) obj).id == id);
	}
}

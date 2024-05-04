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

import java.util.List;

import com.janilla.frontend.RenderEngine;
import com.janilla.frontend.Renderer;
import com.janilla.web.Render;

@Render("CartModal.html")
public record CartModal(Cart cart, List<@Render("CartModal-item.html") CartItem> items) implements Renderer {

	public String emptyClass() {
		return items == null || items.isEmpty() ? "empty" : "";
	}

	@Override
	public boolean evaluate(RenderEngine engine) {
		record A(CartModal modal, Object content) {
		}
		record B(CartItem[] items, int index, Object decrease) {
		}
		record C(CartItem[] items, int index, Object increase) {
		}
		record D(EditItemQuantity edit, String icon) {
		}
		return engine.match(A.class, (x, y) -> {
			y.setValue("");
			y.setTemplate("CartModal-" + (items == null || items.isEmpty() ? "empty" : "nonempty") + ".html");
		}) || engine.match(B.class, (x, y) -> {
			y.setValue(new EditItemQuantity(x.items[x.index].getQuantity() - 1, "minus"));
			y.setTemplate("CartModal-editItemQuantity.html");
		}) || engine.match(C.class, (x, y) -> {
			y.setValue(new EditItemQuantity(x.items[x.index].getQuantity() + 1, "plus"));
			y.setTemplate("CartModal-editItemQuantity.html");
		}) || engine.match(D.class, (x, y) -> y.setTemplate("CartModal-" + x.icon + "Icon.html"));
	}

	public record EditItemQuantity(int quantity, String icon) {
	}
}

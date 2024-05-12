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

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.janilla.frontend.RenderEngine;
import com.janilla.frontend.Renderer;
import com.janilla.http.HttpRequest;
import com.janilla.net.Net;
import com.janilla.persistence.Persistence;
import com.janilla.reflect.Parameter;
import com.janilla.util.EntryList;
import com.janilla.web.Handle;
import com.janilla.web.Render;

public class ProductWeb {

	private Persistence persistence;

	public void setPersistence(Persistence persistence) {
		this.persistence = persistence;
	}

	private static Map<Long, long[]> productRecommendations = new ConcurrentHashMap<>();

	@Handle(method = "GET", path = "/product/(.*)")
	public Page getPage(String handle, @Parameter("image") int image, HttpRequest request) throws IOException {
		var i = persistence.getCrud(Product.class).find("handle", handle);
		var p = persistence.getCrud(Product.class).read(i);
		var jj = persistence.getCrud(ProductVariant.class).filter("product", i);
		var vv = persistence.getCrud(ProductVariant.class).read(jj).toArray(ProductVariant[]::new);
		var pp = Net.parseQueryString(request.getURI().getRawQuery());
		var kk = productRecommendations.computeIfAbsent(i,
				k -> ThreadLocalRandom.current().longs(10, 1, 19).distinct().filter(x -> x != i).toArray());
		var qq = persistence.getCrud(Product.class).read(kk).toList();
		return new Page(p, vv, image, pp, qq);
	}

	@Render("Product.html")
	public record Page(Product product, ProductVariant[] variants, int image, EntryList<String, String> parameters,
			List<@Render("Carousel-product.html") Product> relatedProducts)
			implements Layout.Page, Renderer {

		@Override
		public SEO getSEO() {
			return new SEO(product.getTitle(), product.getDescription());
		}

		public Arrows arrows() {
			var n = product.getImages().length;
			if (n <= 1)
				return null;

			var q = parameters != null ? new EntryList<>(parameters) : new EntryList<String, String>();
			q.set("image", String.valueOf(image > 0 ? image - 1 : n - 1));
			var l = new Arrow(-1,
					Net.createURI(null, null, -1, "/product/" + product.getHandle(), Net.formatQueryString(q)));
			q.set("image", String.valueOf(image < n - 1 ? image + 1 : 0));
			var r = new Arrow(1,
					Net.createURI(null, null, -1, "/product/" + product.getHandle(), Net.formatQueryString(q)));
			return new Arrows(l, r);
		}

		public Object addToCart() {
			if (!product.isAvailableForSale())
				return new OutOfStock();
			var e = Arrays.stream(product.getOptions()).allMatch(x -> !Objects
					.toString(parameters != null ? parameters.get(x.name().toLowerCase()) : null, "").isEmpty());
			var v = Arrays.stream(variants).filter(x -> x.getSelectedOptions().stream().allMatch(
					y -> y.getValue().equals(parameters != null ? parameters.get(y.getKey().toLowerCase()) : null)))
					.findFirst().orElse(null);
			return new AddToCart(e, v != null ? v.getId() : 0);
		}

		@Override
		public boolean evaluate(RenderEngine engine) {
			record A(Arrow arrow, Object icon) {
			}
			record B(URI[] images, int index) {
			}
			record C(ProductOption[] options, int index) {
			}
			record D(ProductOption[] options, int index1, String[] values, int index2) {
			}
			return engine.match(A.class, (x, y) -> {
				y.setValue("");
				y.setTemplate("Product-Arrow" + (x.arrow.step < 0 ? "Left" : "Right") + "Icon.html");
			}) || engine.match(B.class, (x, y) -> {
				var q = parameters != null ? new EntryList<>(parameters) : new EntryList<String, String>();
				q.set("image", String.valueOf(x.index));
				y.setValue(new Image(x.images[x.index],
						Net.createURI(null, null, -1, "/product/" + product.getHandle(), Net.formatQueryString(q)),
						x.index == image ? "active" : null));
			}) || engine.match(C.class, (x, y) -> y.setTemplate("Product-option.html"))
					|| engine.match(D.class, (x, y) -> {
						var n = x.options[x.index1].name();
						var v = x.values[x.index2];
						y.setValue(new OptionValue(n.toLowerCase(), v, n + " " + v,
								parameters != null && v.equals(parameters.get(n.toLowerCase())) ? "active" : null,
								Arrays.stream(variants).filter(w -> w.isAvailableForSale() && w.getSelectedOptions()
										.stream().filter(e -> !e.getKey().equals(n)).allMatch(e -> {
											var z = parameters != null ? parameters.get(e.getKey().toLowerCase())
													: null;
											return z == null || z.equals(e.getValue());
										})).anyMatch(w -> w.getSelectedOptions().stream()
												.anyMatch(e -> e.getKey().equals(n) && e.getValue().equals(v)))));
					});
		}
	}

	@Render("Product-Arrows.html")
	public record Arrows(Arrow left, Arrow right) {
	}

	@Render("Product-Arrow.html")
	public record Arrow(int step, URI href) {

		String ariaLabel() {
			return (step < 0 ? "Previous" : "Next") + " product image";
		}
	}

	@Render("Product-Image.html")
	public record Image(URI src, URI href, String activeClass) {
	}

	@Render("Product-OptionValue.html")
	public record OptionValue(String name, String value, String title, String activeClass, boolean enabled) {

		public String disabledAttribute() {
			return !enabled ? "disabled" : null;
		}

		public boolean ariaDisabled() {
			return !enabled;
		}
	}

	@Render("Product-AddToCart.html")
	public record AddToCart(boolean enabled, long variant) {

		public @Render("Product-PlusIcon.html") Object icon() {
			return "";
		}

		public String disabledAttribute() {
			return !enabled ? "disabled" : null;
		}

		public boolean ariaDisabled() {
			return !enabled;
		}

		public String ariaLabel() {
			return enabled ? "Add to cart" : "Please select an option";
		}
	}

	@Render("Product-OutOfStock.html")
	public record OutOfStock() {
	}
}

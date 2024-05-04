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
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.janilla.frontend.RenderEngine;
import com.janilla.frontend.Renderer;
import com.janilla.html.Html;
import com.janilla.net.Net;
import com.janilla.persistence.Index;
import com.janilla.persistence.Persistence;
import com.janilla.reflect.Parameter;
import com.janilla.util.EntryList;
import com.janilla.web.Handle;
import com.janilla.web.Render;

public class SearchWeb {

	private Persistence persistence;

	public void setPersistence(Persistence persistence) {
		this.persistence = persistence;
	}

	@Handle(method = "GET", path = "/search")
	public Page getPage(@Parameter(name = "q") String query, @Parameter(name = "sort") String sort) throws IOException {
		return getPage(null, query, sort);
	}

	@Handle(method = "GET", path = "/search/(.*)")
	public Page getPage(String collection, String query, @Parameter(name = "sort") String sort) throws IOException {
		var c1 = persistence.getCrud(Collection.class);
		var ii = c1.list();
		var cc = c1.read(ii);

		Collection c;
		if (collection != null && !collection.isEmpty()) {
			var i = c1.find("handle", collection);
			c = i > 0 ? c1.read(i) : null;
		} else
			c = null;

		var c2 = persistence.getCrud(Product.class);
		if (c != null)
			ii = c2.filter("collection", c.getId());
		else if (query != null && !query.isBlank()) {
			ii = c2.filter("title", (Object[]) Index.KeywordSet.space.split(query.toLowerCase()));
		} else
			ii = c2.list();
		if (sort == null || sort.isEmpty())
			sort = "latest-desc";
		var pp = c2.read(ii).sorted(switch (sort) {
		case "latest-desc" -> Comparator.comparing(Product::getId); // .reversed();
		case "price-asc" -> Comparator.comparing(p -> p.getPrice());
		case "price-desc" -> Comparator.<Product, BigDecimal>comparing(p -> p.getPrice()).reversed();
		default -> throw new RuntimeException();
		}).toList();

		var m = (pp.isEmpty() ? "There are no products that match"
				: "Showing " + pp.size() + " " + (pp.size() == 1 ? "result" : "results") + " for") + " <span>&quot;"
				+ Html.escape(query) + "&quot;</span>";

		var d = new Collection();
		d.setTitle("All");

		var p = collection != null && !collection.isEmpty() ? "/search/" + collection : "/search";
		var l = new EntryList<String, String>();
		if (query != null && !query.isBlank())
			l.add("q", query);

		return new Page(() -> Stream.concat(Stream.of(d), cc).iterator(), c != null ? c.getId() : 0, m, pp,
				Arrays.stream("""
						latest-desc	Latest arrivals
						price-asc	Price: Low to high
						price-desc	Price: High to low""".split("\n")).map(x -> {
					var y = x.split("\t");
					l.set("sort", y[0]);
					var u = Net.createURI(null, null, -1, p, Net.formatQueryString(l));
					return new Sorting(y[0], y[1], u);
				}).toList(), sort);
	}

	@Render("Search.html")
	public record Page(Iterable<@Render("Search-Collection.html") Collection> collections, long collection,
			String message, List<@Render("Search-Product.html") Product> products,
			Iterable<Sorting> sortings, String sort) implements Layout.Page, Renderer {

		@Override
		public SEO getSEO() {
			return new SEO("Search", "Search for products in the store.");
		}

		@Override
		public boolean evaluate(RenderEngine engine) {
			// TODO Auto-generated method stub
			return false;
		}

//		@Override
//		public Object render(RenderEngine engine) throws IOException {
//			var o = engine.getObject();
//			return o != null ? switch (o) {
//			case Collection c -> {
//				if (c.getId() == collection)
//					engine.setTemplate("Search-Collection-active.html");
//				yield CANNOT_RENDER;
//			}
//			case Sorting s -> {
//				if (s.handle.equals(sort))
//					engine.setTemplate("Search-Sorting-active.html");
//				yield CANNOT_RENDER;
//			}
//			default -> CANNOT_RENDER;
//			} : CANNOT_RENDER;
//		}
	}

	@Render("Search-Sorting.html")
	public record Sorting(String handle, String title, URI uri) {
	}
}

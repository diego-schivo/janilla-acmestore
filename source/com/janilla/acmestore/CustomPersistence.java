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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.janilla.persistence.Crud;
import com.janilla.persistence.Persistence;

public class CustomPersistence extends Persistence {

	@Override
	protected <E> Crud<E> createCrud(Class<E> type) {
		if (type == Product.class) {
			@SuppressWarnings("unchecked")
			var c = (Crud<E>) new ProductCrud();
			return c;
		}

		return super.createCrud(type);
	}

	void seed() {
		var r = ThreadLocalRandom.current();

		try (var s = getClass().getResourceAsStream("collections.csv")) {
			for (var x : new String(s.readAllBytes()).split("\n")) {
				var y = x.split("\t", -1);
				var z = new Collection();
				z.setHandle(y[0]);
				z.setTitle(y[1]);
				crud(Collection.class).create(z);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		try (var s = getClass().getResourceAsStream("products.csv")) {
			for (var x : new String(s.readAllBytes()).split("\n")) {
				var y = x.split("\t", -1);
				var z = new Product();
				z.setHandle(y[0]);
				z.setAvailableForSale(Boolean.parseBoolean(y[1]));
				z.setTitle(y[2]);
				if (!y[3].isEmpty())
					z.setDescriptionHtml(y[3]);
				else
					try (var d = getClass().getResourceAsStream(y[0] + ".html")) {
						if (d != null)
							z.setDescriptionHtml(new String(d.readAllBytes()));
					}
				z.setOptions(!y[4].isEmpty() ? Arrays.stream(y[4].split(";")).map(w -> {
					var v = w.split(":");
					return new ProductOption(v[0], v[1].split(","));
				}).toArray(ProductOption[]::new) : new ProductOption[0]);
				z.setImages(Arrays.stream(y[5].split(",")).map(w -> URI.create("/images/" + w))
						.toArray(URI[]::new));
				z.setFeaturedImage(z.getImages()[0]);
				z.setPrice(new BigDecimal(y[6]));
				if (!y[7].isEmpty())
					z.setCollections(Arrays.stream(y[7].split(",")).mapToLong(Long::parseLong).toArray());
				z.setHidden(Boolean.parseBoolean(y[8]));
				crud(Product.class).create(z);

				var oo = z.getOptions();
				if (oo.length > 0) {
					var ii = new int[oo.length];
					int j;
					do {
						var v = new ProductVariant();
						v.setProduct(z.getId());
						v.setAvailableForSale(r.nextBoolean());
						v.setSelectedOptions(IntStream.range(0, oo.length)
								.mapToObj(i -> Map.entry(oo[i].name(), oo[i].values()[ii[i]])).toList());
						v.setTitle(v.getSelectedOptions().stream().map(Map.Entry::getValue)
								.collect(Collectors.joining(" / ")));
						v.setPrice(z.getPrice().multiply(BigDecimal.valueOf(7)).divide(BigDecimal.valueOf(12),
								RoundingMode.HALF_UP));
						crud(ProductVariant.class).create(v);

						for (j = oo.length - 1; j >= 0; j--) {
							if (++ii[j] < oo[j].values().length)
								break;
							ii[j] = 0;
						}
					} while (j >= 0);
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		try (var s = getClass().getResourceAsStream("pages.csv")) {
			for (var x : new String(s.readAllBytes()).split("\n")) {
				var y = x.split("\t", -1);
				var z = new Page();
				z.setTitle(y[0]);
				z.setHandle(y[1]);
				try (var t = getClass().getResourceAsStream(y[2])) {
					z.setBody(new String(t.readAllBytes()));
				}
				crud(Page.class).create(z);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}

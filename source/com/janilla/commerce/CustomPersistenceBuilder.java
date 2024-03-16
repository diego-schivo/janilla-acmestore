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
import java.math.RoundingMode;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.janilla.persistence.ApplicationPersistenceBuilder;
import com.janilla.persistence.Persistence;

class CustomPersistenceBuilder extends ApplicationPersistenceBuilder {

	@Override
	public Persistence build() throws IOException {
		var e = Files.exists(file);
		var p = super.build();
		if (!e)
			populate(p);
		return p;
	}

	void populate(Persistence persistence) throws IOException {
		for (var x : """
				hidden-homepage-featured-items	Homepage featured items
				hidden-homepage-carousel	Homepage carousel
				bags	Bags
				drinkware	Drinkware
				electronics	Electronics
				footware	Footware
				headwear	Headwear
				hoodies	Hoodies
				jackets	Jackets
				kids	Kids
				pets	Pets
				shirts	Shirts
				stickers	Stickers""".split("\n")) {
			var y = x.split("\t");
			var z = new Collection();
			z.setHandle(y[0]);
			z.setTitle(y[1]);
			persistence.getCrud(Collection.class).create(z);
		}

		for (var x : """
				acme-slip-on-shoes	Acme Slip-On Shoes			shoes-1	45	6
				acme-geometric-circles-t-shirt	Acme Circles T-Shirt	60% combed ringspun cotton/40% polyester jersey tee.	Color:Black,White,Blue;Size:XS,S,M,L,XL,XXL,XXXL	t-shirt-1,t-shirt-2,t-shirt-circles-blue	20	12,1
				acme-drawstring-bag	Acme Drawstring Bag		Color:Black,White;Size:6 x 8 inch,7 x 9 inch,8 x 11 inch,9 x 12 inch,10 x 15 inch,12 x 16 inch	bag-1-dark	12	3,1
				acme-mechanical-keyboard	Acme Keyboard			keyboard	150	5
				acme-rainbow-prism-t-shirt	Acme Prism T-Shirt			t-shirt-spiral-1	25	12
				acme-t-shirt	Acme T-Shirt			t-shirt-color-black	20	12
				acme-sticker	Acme Sticker			sticker	4	13
				acme-rainbow-sticker	Acme Rainbow Sticker			sticker-rainbow	4	13
				acme-pacifier	Acme Pacifier			pacifier-1	10	10
				acme-mug	Acme Mug			mug-1	15	4
				acme-hoodie	Acme Hoodie			hoodie-1	50	8
				acme-cap	Acme Cap			hat-1	20	7
				acme-dog-sweater	Acme Dog Sweater			dog-sweater-1	20	11
				acme-cup	Acme Cup			cup-black	15	4,1
				acme-cowboy-hat	Acme Cowboy Hat			cowboy-hat-black-1	160	7
				acme-bomber-jacket	Acme Bomber Jacket			bomber-jacket-army	50	9
				acme-baby-onesie	Acme Baby Onesie			baby-onesie-beige-1	10	10
				acme-baby-cap	Acme Baby Cap			baby-cap-black	10	7"""
				.split("\n")) {
			var y = x.split("\t");
			var z = new Product();
			z.setHandle(y[0]);
			z.setTitle(y[1]);
			z.setDescriptionHtml(y[2]);
			z.setOptions(!y[3].isEmpty() ? Arrays.stream(y[3].split(";")).map(w -> {
				var v = w.split(":");
				return new ProductOption(v[0], v[1].split(","));
			}).toArray(ProductOption[]::new) : new ProductOption[0]);
			z.setImages(Arrays.stream(y[4].split(","))
					.map(w -> URI.create("https://cdn.shopify.com/s/files/1/0754/3727/7491/files/" + w + ".png"))
					.toArray(URI[]::new));
			z.setFeaturedImage(z.getImages()[0]);
			z.setPrice(new BigDecimal(y[5]));
			z.setCollections(Arrays.stream(y[6].split(",")).mapToLong(Long::parseLong).toArray());
			persistence.getCrud(Product.class).create(z);

			var oo = z.getOptions();
			if (oo.length > 0) {
				var ii = new int[oo.length];
				int j;
				do {
					var v = new ProductVariant();
					v.setProduct(z.getId());
					v.setAvailableForSale(ii.length < 2 || ii[1] != (ii[0] % 2 == 0 ? 0 : oo[1].values().length - 1));
					v.setSelectedOptions(IntStream.range(0, oo.length)
							.mapToObj(i -> Map.entry(oo[i].name(), oo[i].values()[ii[i]])).toList());
					v.setTitle(v.getSelectedOptions().stream().map(Entry::getValue).collect(Collectors.joining(" / ")));
					v.setPrice(z.getPrice().multiply(BigDecimal.valueOf(7)).divide(BigDecimal.valueOf(12),
							RoundingMode.HALF_UP));
					persistence.getCrud(ProductVariant.class).create(v);

					for (j = oo.length - 1; j >= 0; j--) {
						if (++ii[j] < oo[j].values().length)
							break;
						ii[j] = 0;
					}
				} while (j >= 0);
			}
		}
	}
}

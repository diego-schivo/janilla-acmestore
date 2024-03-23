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
				acme-slip-on-shoes	Acme Slip-On Shoes		Size:1,1.5,2,2.5,3,3.5,4,4.5,5,5.5,6,6.5,7,7.5,8,8.5,9,9.5,10,10.5,11,11.5,12,12.5,13	shoes-1,shoes-2,shoes-3,shoes-4	45	6	false
				acme-geometric-circles-t-shirt	Acme Circles T-Shirt	60% combed ringspun cotton/40% polyester jersey tee.	Color:Black,White,Blue;Size:XS,S,M,L,XL,XXL,XXXL	t-shirt-circles-black,t-shirt-circles-white,t-shirt-circles-blue	20	12,1	false
				acme-drawstring-bag	Acme Drawstring Bag		Color:Black,White;Size:6 x 8 inch,7 x 9 inch,8 x 11 inch,9 x 12 inch,10 x 15 inch,12 x 16 inch	bag-black,bag-white	12	3,1	false
				acme-mechanical-keyboard	Acme Keyboard			keyboard	150	5	false
				acme-rainbow-prism-t-shirt	Acme Prism T-Shirt	60% combed ringspun cotton/40% polyester jersey tee.	Size:XS,S,M,L,XL,XXL,XXXL	t-shirt-spiral-1,t-shirt-spiral-2,t-shirt-spiral-3,t-shirt-spiral-4	25	12	false
				acme-t-shirt	Acme T-Shirt	60% combed ringspun cotton/40% polyester jersey tee.	Color:Black,Blue,Gray,Pink,White;Size:XS,S,M,L,XL,XXL,XXXL	t-shirt-color-black,t-shirt-color-blue,t-shirt-color-gray,t-shirt-color-pink,t-shirt-color-white	20	12	false
				acme-sticker	Acme Sticker			sticker	4	13	false
				acme-rainbow-sticker	Acme Rainbow Sticker			sticker-rainbow	4	13	false
				acme-pacifier	Acme Pacifier			pacifier-1,pacifier-2	10	10	false
				acme-mug	Acme Mug	12 oz Beck Cork-Bottom Mug.		mug-1,mug-2	15	4,2	false
				acme-hoodie	Acme Hoodie	Fabric blend of Supima Cotton and Micromodal.	Size:XS,S,M,L,XL,XXL,XXXL	hoodie-1,hoodie-2	50	8,2	false
				acme-cap	Acme Cap	100% peach-washed cotton.		hat-1,hat-2,hat-3	20	7	false
				acme-dog-sweater	Acme Dog Sweater		Size:0 - 5 lbs,5 - 20 lbs,20 - 50 lbs,50 - 75 lbs,75+ lbs	dog-sweater-1,dog-sweater-2	20	11	false
				acme-cup	Acme Cup	12oz double wall ceramic body with a padded bottom.	Color:Black,White	cup-black,cup-white	15	4,1	false
				acme-cowboy-hat	Acme Cowboy Hat		Color:Black,Tan;Size:6 3/4,6 7/8,7,7 1/8,7 1/4,7 3/8,7 1/2,7 5/8,7 3/4	cowboy-hat-black-1,cowboy-hat-black-2,cowboy-hat-black-3,cowboy-hat-black-4,cowboy-hat-black-5,cowboy-hat-black-6,cowboy-hat-tan-1,cowboy-hat-tan-2,cowboy-hat-tan-3,cowboy-hat-tan-4,cowboy-hat-tan-5,cowboy-hat-tan-6	160	7	false
				acme-bomber-jacket	Acme Bomber Jacket	The multi-season must-have jacket: light and classic for daily wear, with a soft fleece lining for extra warmth.	Size:XS,S,M,L,XL,XXL,XXXL;Color:Army,Black	bomber-jacket-army,bomber-jacket-black	50	9	false
				acme-baby-onesie	Acme Baby Onesie	Short sleeve 5-oz, 100% combed ringspun cotton onesie	Size:NB,3M,6M,12M,18M,24M;Color:Black,White,Beige	baby-onesie-beige-1,baby-onesie-beige-2,baby-onesie-black-1,baby-onesie-black-2,baby-onesie-white-1,baby-onesie-white-2	10	10,2	false
				acme-baby-cap	Acme Baby Cap	100% combed ringspun cotton	Color:Black,Gray,White	baby-cap-black,baby-cap-gray,baby-cap-white	10	7,2	false
				acme-webcam-cover	Acme Webcam Cover			webcam-cover	5		true"""
				.split("\n")) {
			var y = x.split("\t", -1);
			var z = new Product();
			z.setHandle(y[0]);
			z.setTitle(y[1]);
			if (!y[2].isEmpty())
				z.setDescriptionHtml(y[2]);
			else
				try (var d = getClass().getResourceAsStream(y[0] + ".html")) {
					if (d != null)
						z.setDescriptionHtml(new String(d.readAllBytes()));
				}
			z.setOptions(!y[3].isEmpty() ? Arrays.stream(y[3].split(";")).map(w -> {
				var v = w.split(":");
				return new ProductOption(v[0], v[1].split(","));
			}).toArray(ProductOption[]::new) : new ProductOption[0]);
			z.setImages(
					Arrays.stream(y[4].split(",")).map(w -> URI.create("/images/" + w + ".webp")).toArray(URI[]::new));
			z.setFeaturedImage(z.getImages()[0]);
			z.setPrice(new BigDecimal(y[5]));
			if (!y[6].isEmpty())
				z.setCollections(Arrays.stream(y[6].split(",")).mapToLong(Long::parseLong).toArray());
			z.setHidden(Boolean.parseBoolean(y[7]));
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

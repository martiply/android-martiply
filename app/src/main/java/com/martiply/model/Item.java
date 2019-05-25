package com.martiply.model;

import com.martiply.model.interfaces.IItem;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class Item implements IItem {

	final String id;
	final int ownerId;
	final String idCustom;
	final String gtin;
	final String description;
	final String name;
	final String category;
	final String brand;
	final int hits;
	final String price;
	final Condition condition;
	final ApparelExtension apparelExtension;
	final String url;
	final Sale sale;
	final Img img;

	public enum RootCategory {
		product,
		culinary,
		all
	}

	@ParcelConstructor
	public Item(String id, int ownerId, String idCustom, String gtin, String description, String name, String category, String brand, int hits, String price, Condition condition, ApparelExtension apparelExtension, String url, Sale sale, Img img) {
		this.id = id;
		this.ownerId = ownerId;
		this.idCustom = idCustom;
		this.gtin = gtin;
		this.description = description;
		this.name = name;
		this.category = category;
		this.brand = brand;
		this.hits = hits;
		this.price = price;
		this.condition = condition;
		this.apparelExtension = apparelExtension;
		this.url = url;
		this.sale = sale;
		this.img = img;
	}

	public Sale getSale() {
		return sale;
	}

	public int getOwnerId() {
		return ownerId;
	}

	public ApparelExtension getApparelExtension() {
		return apparelExtension;
	}

	public String getIdCustom() {
		return idCustom;
	}

	public String getGtin() {
		return gtin;
	}

	public String getDescription() {
		return description;
	}

	public int getHits() {
		return hits;
	}

	public String getBrand() {
		return brand;
	}

	public String getCategory() {
		return category;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPrice() {
		return price;
	}

	public Condition getCondition() {
		return condition;
	}

	public String getUrl() {
		return url;
	}

	public Img getImg() {
		return img;
	}

}

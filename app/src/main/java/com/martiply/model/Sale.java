package com.martiply.model;

import com.martiply.model.interfaces.ISale;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class Sale implements ISale {
	final String id;
	final String salePrice;
	final long saleStart;
	final long saleEnd;

	@ParcelConstructor
	public Sale(String id, String salePrice, long saleStart, long saleEnd) {
		this.id = id;
		this.salePrice = salePrice;
		this.saleStart = saleStart;
		this.saleEnd = saleEnd;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getSalePrice() {
		return salePrice;
	}

	@Override
	public long getSaleStart() {
		return saleStart;
	}

	@Override
	public long getSaleEnd() {
		return saleEnd;
	}
}

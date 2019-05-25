package com.martiply.model;

import com.martiply.model.interfaces.IIPP;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class IPP implements IIPP {

	final Item item;
	final Store store;

	@ParcelConstructor
	public IPP(Item item, Store store) {
		this.item = item;
		this.store = store;
	}

	public Store getStore() {
		return store;
	}

	public Item getItem() {
		return item;
	}

}
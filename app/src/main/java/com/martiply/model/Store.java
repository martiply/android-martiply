package com.martiply.model;

import com.martiply.model.interfaces.IStore;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class Store implements IStore {

	final int storeId;
	final String name;
	final String email;
	final String zip;
	final String address;
	final String city;
	final String phone;
	final double lng;
	final double lat;
	final String open;
	final String close;
	final double distance;
	final String story;
	final String currency;
	final int tz;
	final Img img;

	@ParcelConstructor
	public Store(int storeId, String name, String email, String zip, String address, String city, String phone, double lng, double lat, String open, String close, double distance, String story, String currency, int tz, Img img) {
		this.storeId = storeId;
		this.name = name;
		this.email = email;
		this.zip = zip;
		this.address = address;
		this.city = city;
		this.phone = phone;
		this.lng = lng;
		this.lat = lat;
		this.open = open;
		this.close = close;
		this.distance = distance;
		this.story = story;
		this.currency = currency;
		this.tz = tz;
		this.img = img;
	}

	private static String getCurrencyFromTz(int tz){
		switch (tz) {
		case 9:			
			return "JPY";
		default:
			return "IDR";
		}		
	}

	public int getStoreId() {
		return storeId;
	}

	public String getName() {
		return name;
	}

	public String getZip() {
		return zip;
	}

	public String getAddress() {
		return address;
	}

	public String getPhone() {
		return phone;
	}

	public double getLng() {
		return lng;
	}

	public double getLat() {
		return lat;
	}

	public String getOpen() {
		return open;
	}

	public String getClose() {
		return close;
	}

	public double getDistance() {
		return distance;
	}

	public String getStory() {
		return story;
	}

	public String getCurrency() {
		return currency;
	}

	public int getTz() {
		return tz;
	}

	public String getCity() {
		return city;
	}

	public Img getImg() {
		return img;
	}

	public String getEmail() {
		return email;
	}

}

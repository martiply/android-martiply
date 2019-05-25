package com.martiply.model;

import com.martiply.model.interfaces.IOwner;
import org.parceler.ParcelConstructor;

public class Owner implements IOwner {
	final int ownerId;
	final String email;
	final String name;
	final String phone;

	@ParcelConstructor
	public Owner(int ownerId, String email, String name, String phone) {
		this.ownerId = ownerId;
		this.email = email;
		this.name = name;
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public int getOwnerId() {
		return ownerId;
	}

}

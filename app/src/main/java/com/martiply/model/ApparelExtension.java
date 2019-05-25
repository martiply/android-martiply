package com.martiply.model;

import com.martiply.model.interfaces.IApparelExtension;
import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class ApparelExtension implements IApparelExtension {

	final String id;
	final String groupId;
	final Gender gender;
	final Age age;
	final SizeSystem sizeSystem;
	final String size;
	final String color;
	final String material;
	final String feature;

	@ParcelConstructor
	public ApparelExtension(String id, String groupId, Gender gender, Age age, SizeSystem sizeSystem, String size, String color, String material, String feature) {
		this.id = id;
		this.groupId = groupId;
		this.gender = gender;
		this.age = age;
		this.sizeSystem = sizeSystem;
		this.size = size;
		this.color = color;
		this.material = material;
		this.feature = feature;
	}


	public String getId() {
		return id;
	}

	public Gender getGender() {
		return gender;
	}

	public String getGroupId() {
		return groupId;
	}

	public SizeSystem getSizeSystem() {
		return sizeSystem;
	}

	public String getFeature() {
		return feature;
	}
	public Age getAge() {
		return age;
	}

	public String getSize() {
		return size;
	}

	public String getColor() {
		return color;
	}

	public String getMaterial() {
		return material;
	}

}


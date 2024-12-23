package com.retno.storyapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Story(
	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("lon")
	val lon: Any? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("lat")
	val lat: Any? = null
) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(photoUrl)
		parcel.writeString(createdAt)
		parcel.writeString(name)
		parcel.writeString(description)
		parcel.writeString(lon.toString())
		parcel.writeString(id)
		parcel.writeString(lat.toString())
	}

	override fun describeContents(): Int = 0

	companion object CREATOR : Parcelable.Creator<Story> {
		override fun createFromParcel(parcel: Parcel): Story {
			return Story(parcel)
		}

		override fun newArray(size: Int): Array<Story?> {
			return arrayOfNulls(size)
		}
	}
}

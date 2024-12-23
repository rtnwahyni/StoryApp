package com.retno.storyapp.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class StoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class ListStoryItem(
	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null
) : Parcelable {
	constructor(parcel: Parcel) : this(
		id = parcel.readString(),
		name = parcel.readString(),
		description = parcel.readString(),
		photoUrl = parcel.readString()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(id)
		parcel.writeString(name)
		parcel.writeString(description)
		parcel.writeString(photoUrl)
	}

	override fun describeContents(): Int = 0

	companion object CREATOR : Parcelable.Creator<ListStoryItem> {
		override fun createFromParcel(parcel: Parcel): ListStoryItem {
			return ListStoryItem(parcel)
		}

		override fun newArray(size: Int): Array<ListStoryItem?> {
			return arrayOfNulls(size)
		}
	}
}
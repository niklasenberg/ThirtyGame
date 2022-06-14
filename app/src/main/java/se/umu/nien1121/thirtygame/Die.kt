package se.umu.nien1121.thirtygame

import android.os.Parcel
import android.os.Parcelable

private val imageMap = mapOf(
    1 to R.drawable.red1,
    2 to R.drawable.red2,
    3 to R.drawable.red3,
    4 to R.drawable.red4,
    5 to R.drawable.red5,
    6 to R.drawable.red6,
    7 to R.drawable.white1,
    8 to R.drawable.white2,
    9 to R.drawable.white3,
    10 to R.drawable.white4,
    11 to R.drawable.white5,
    12 to R.drawable.white6,
    13 to R.drawable.grey1,
    14 to R.drawable.grey2,
    15 to R.drawable.grey3,
    16 to R.drawable.grey4,
    17 to R.drawable.grey5,
    18 to R.drawable.grey6
)

class Die(var rollable: Boolean, var value: Int, var selected: Boolean, var counted: Boolean) :
    Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    fun select() {
        if (rollable) {
            selected = !selected
        }
    }

    fun roll() {
        if (selected) {
            rollable = false
            selected = false
        } else if (rollable) {
            value = (1..6).random()
        }
    }

    fun reset() {
        selected = false
        rollable = true
        counted = false
        roll()
    }

    fun getImageResId(): Int {
        return if (selected) {
            imageMap[value]!!
        } else if (rollable) {
            imageMap[value + 6]!!
        } else {
            imageMap[value + 12]!!
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (rollable) 1 else 0)
        parcel.writeInt(value)
        parcel.writeByte(if (selected) 1 else 0)
        parcel.writeByte(if (counted) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Die> {
        override fun createFromParcel(parcel: Parcel): Die {
            return Die(parcel)
        }

        override fun newArray(size: Int): Array<Die?> {
            return arrayOfNulls(size)
        }
    }
}
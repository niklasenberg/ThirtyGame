package se.umu.nien1121.thirtygame

import android.os.Parcel
import android.os.Parcelable

/**
 * Class representing dice used in game application. Implements [Parcelable] interface.
 * @param enabled whether this die can be thrown or not
 * @param value side of die facing up
 * @param selected whether this die is selected to be "held" by user
 * @param counted whether this die has been counted when calculating score
 */
class Die(var enabled: Boolean, var value: Int, var selected: Boolean, var counted: Boolean) :
    Parcelable {

    /**
     * Secondary constructor for parcelled objects, enables transfer between lifecycles.
     * @param parcel constructed parcel of prior die object, initialized in [writeToParcel]
     */
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    /**
     * Marks this die as selected, if enabled
     */
    fun select() {
        if (enabled) {
            selected = !selected
        }
    }

    /**
     * If die is selected, store value and disable die. Else, randomize new value.
     */
    fun roll() {
        if (selected) {
            enabled = false
            selected = false
        } else if (enabled) {
            value = (1..6).random()
        }
    }

    /**
     * Resets die to its original state, and roll for a randomized value.
     */
    fun reset() {
        selected = false
        enabled = true
        counted = false
        roll()
    }

    /**
     * Writes properties to [Parcel] for current object.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (enabled) 1 else 0)
        parcel.writeInt(value)
        parcel.writeByte(if (selected) 1 else 0)
        parcel.writeByte(if (counted) 1 else 0)
    }

    /**
     * Helper method required by [Parcelable], not used.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Companion object required by [Parcelable]
     */
    companion object CREATOR : Parcelable.Creator<Die> {
        /**
         * Creates single object from parcel, by use of secondary constructor.
         */
        override fun createFromParcel(parcel: Parcel): Die {
            return Die(parcel)
        }

        /**
         * Helper method for creation of objects for multiple parcels, not used.
         */
        override fun newArray(size: Int): Array<Die?> {
            return arrayOfNulls(size)
        }
    }
}
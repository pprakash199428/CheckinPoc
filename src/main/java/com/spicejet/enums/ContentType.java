package com.spicejet.enums;

/**
 * Enum to define All type of Content that a kiosk can process.
 */
public enum ContentType {
	BOARDING_PASS("Boarding Pass"),
    BAG_TAG("Bag Tag");

	String displayName;

	private ContentType(String displayName) {
		this.displayName = displayName;
	}

    /**
     * Returns displayName of ContentTypes
     * @return - Display Name.
     */
	public String getDisplayName() {
		return this.displayName;
	}
}

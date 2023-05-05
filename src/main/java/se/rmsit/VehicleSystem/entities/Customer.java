package se.rmsit.VehicleSystem.entities;

import java.util.Objects;

public class Customer extends User {
	private String address;

	// Använder String då postnummer inte är numeriska över hela världen.
	private String postcode;
	private String postTown;
	private String phoneNumber;

	// Använder isPublicAuthority med is, då publicAuthority antagligen hade misstolkats som vilken specifika publika entitet (kommun) det är och inte om kunden generellt är en publik entitet.
	private boolean isPublicAuthority;

	/**
	 * Standard konstruktor, används för att skapa objekt från persistent lagring.
	 */
	public Customer() {}

	public Customer(String customerId, String firstName, String lastName, String address, String postTown, String postcode, String phoneNumber, boolean isPublicAuthority, String email, String hashedPassword) {
		super(customerId, firstName, lastName, email, hashedPassword);
		setAddress(address);
		setPostTown(postTown);
		setIsPublicAuthority(isPublicAuthority);
		setPostcode(postcode);
		setPhoneNumber(phoneNumber);
	}

	@Override
	public String serialize() {
		String serialized = super.serialize() + "\n";
		serialized += "address: " + getAddress() + "\n" +
						"postcode: " + getPostcode() + "\n" +
						"postTown: " + getPostTown() + "\n" +
						"phoneNumber: " + getPhoneNumber() + "\n" +
						"isPublicAuthority: " + isPublicAuthority();
		return serialized;
	}

	/**
	 * Laddar in ett nyckel-värde par till objektet
	 * @param key Nyckeln för värdet
	 * @param value Värdet
	 */
	@Override
	public void loadData(String key, String value) {
		super.loadData(key, value);
		switch (key) {
			case "address" -> setAddress(value);
			case "postcode" -> setPostcode(value);
			case "postTown" -> setPostTown(value);
			case "phoneNumber" -> setPhoneNumber(value);
			case "isPublicAuthority" -> setIsPublicAuthority(value.equals("true"));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Customer customer = (Customer) o;

		if (isPublicAuthority != customer.isPublicAuthority) return false;
		if (!Objects.equals(address, customer.address)) return false;
		if (!Objects.equals(postcode, customer.postcode)) return false;
		if (!Objects.equals(postTown, customer.postTown)) return false;
		return Objects.equals(phoneNumber, customer.phoneNumber);
	}

	@Override
	public String toString() {
		return "Customer{" +
				"id=" + getId() +
				", firstName='" + getFirstName() + '\'' +
				", lastName='" + getLastName() + '\'' +
				", email='" + getEmail() + '\'' +
				", hashedPassword='" + getHashedPassword() + '\'' +
				", address='" + address + '\'' +
				", postcode='" + postcode + '\'' +
				", postTown='" + postTown + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", isPublicAuthority=" + isPublicAuthority +
				'}';
	}

	public String getCustomerId() {
		return getId();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getPostTown() {
		return postTown;
	}

	public void setPostTown(String postTown) {
		this.postTown = postTown;
	}

	public boolean isPublicAuthority() {
		return isPublicAuthority;
	}

	public void setIsPublicAuthority(boolean government) {
		isPublicAuthority = government;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		// TODO: Validera format
		this.phoneNumber = phoneNumber;
	}
}

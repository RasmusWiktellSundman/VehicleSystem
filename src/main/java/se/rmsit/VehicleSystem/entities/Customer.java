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

	public Customer(long customerId, String firstName, String lastName, String address, String postTown, String phoneNumber, boolean isPublicAuthority, String email, String hashedPassword) {
		super(customerId, firstName, lastName, email, hashedPassword);
		setAddress(address);
		setPostTown(postTown);
		setIsPublicAuthority(isPublicAuthority);
		setPhoneNumber(phoneNumber);
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
	public int hashCode() {
		int result = address != null ? address.hashCode() : 0;
		result = 31 * result + (postcode != null ? postcode.hashCode() : 0);
		result = 31 * result + (postTown != null ? postTown.hashCode() : 0);
		result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
		result = 31 * result + (isPublicAuthority ? 1 : 0);
		return result;
	}

	public long getCustomerId() {
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

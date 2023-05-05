package se.rmsit.VehicleSystem.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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

	public Customer(long customerId, String firstName, String lastName, String address, String postTown, String postcode, String phoneNumber, boolean isPublicAuthority, String email, String hashedPassword) {
		super(customerId, firstName, lastName, email, hashedPassword);
		setAddress(address);
		setPostTown(postTown);
		setIsPublicAuthority(isPublicAuthority);
		setPostcode(postcode);
		setPhoneNumber(phoneNumber);
	}

	@Override
	public void store(PrintWriter printWriter) {
		super.store(printWriter);
		printWriter.println("address: " + getAddress());
		printWriter.println("postcode: " + getPostcode());
		printWriter.println("postTown: " + getPostTown());
		printWriter.println("phoneNumber: " + getPhoneNumber());
		printWriter.println("isPublicAuthority: " + isPublicAuthority());
	}

	@Override
	public void load(BufferedReader reader) throws IOException {
		// Skapar objekt från reader data (samma som store metoden)
		while (true) {
			String line = reader.readLine();
			if(line == null) {
				break;
			}
			// Delar upp raden i nyckel-data par
			String[] tokens = line.split(": ");
			String data = tokens[1];
			if(data.equals("null")) {
				data = null;
			}
			switch (tokens[0]) {
				case "user_id" -> setId(Long.parseLong(data));
				case "first_name" -> setFirstName(data);
				case "last_name" -> setLastName(data);
				case "email" -> setEmail(data);
				case "hashed_password" -> setHashedPassword(data);
				case "address" -> setAddress(data);
				case "postcode" -> setPostcode(data);
				case "postTown" -> setPostTown(data);
				case "phoneNumber" -> setPhoneNumber(data);
				case "isPublicAuthority" -> setIsPublicAuthority(data.equals("true"));
			}
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

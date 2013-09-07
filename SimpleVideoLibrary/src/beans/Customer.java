/**
 * 
 */
package beans;

/**
 * @author Siddhi Chogle
 *
 */
public class Customer {
	private Long membership_id, ssn;
	private Integer outstanding;
	private String fname, lname, address, city, state, password, email, zipCode;
	private Boolean premium;

	/**
	 * @return the membership_id
	 */
	public Long getMembership_id() {
		return membership_id;
	}
	/**
	 * @param membership_id the membership_id to set
	 */
	public void setMembership_id(Long membership_id) {
		this.membership_id = membership_id;
	}
	/**
	 * @return the ssn
	 */
	public Long getSsn() {
		return ssn;
	}
	/**
	 * @param ssn the ssn to set
	 */
	public void setSsn(Long ssn) {
		this.ssn = ssn;
	}
	/**
	 * @return the outstanding
	 */
	public Integer getOutstanding() {
		return outstanding;
	}
	/**
	 * @param outstanding the outstanding to set
	 */
	public void setOutstanding(Integer outstanding) {
		this.outstanding = outstanding;
	}
	/**
	 * @return the fname
	 */
	public String getFname() {
		return fname;
	}
	/**
	 * @param fname the fname to set
	 */
	public void setFname(String fname) {
		this.fname = fname;
	}
	/**
	 * @return the lname
	 */
	public String getLname() {
		return lname;
	}
	/**
	 * @param lname the lname to set
	 */
	public void setLname(String lname) {
		this.lname = lname;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}
	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	/**
	 * @return the premium
	 */
	public Boolean getPremium() {
		return premium;
	}
	/**
	 * @param premium the premium to set
	 */
	public void setPremium(Boolean premium) {
		this.premium = premium;
	} 
		
}

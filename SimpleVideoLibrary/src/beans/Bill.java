package beans;



public class Bill {
	
	private long membership_Id;
	private String fname,lname,email;
	String[] movieNames;
	public String[] getMovieNames() {
		return movieNames;
	}
	public void setMovieNames(String[] movieNames) {
		this.movieNames = movieNames;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public String getLname() {
		return lname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	private Integer outstanding;
	private boolean premium;
	private Integer fine,totalFee;
	
	
	public long getMembership_Id() {
		return membership_Id;
	}
	public void setMembership_Id(long membership_Id) {
		this.membership_Id = membership_Id;
	}
	
	public Integer getOutstanding() {
		return outstanding;
	}
	public void setOutstanding(Integer outstanding) {
		this.outstanding = outstanding;
	}
	public boolean isPremium() {
		return premium;
	}
	public void setPremium(boolean premium) {
		this.premium = premium;
	}
	public Integer getFine() {
		return fine;
	}
	public void setFine(Integer fine) {
		this.fine = fine;
	}
	public Integer getTotalFee() {
		return totalFee;
	}
	public void setTotalFee(Integer totalFee) {
		this.totalFee = totalFee;
	}
	
	
	
	

}

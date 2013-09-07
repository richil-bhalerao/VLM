/**
 * 
 */
package beans;


/**
 * @author Siddhi Chogle
 *
 */
public class Movie {
	private Integer movieId, availableCopies;
	private String movieName, movieBanner, category, releaseDate, issuedDate, dueDate;
	private Float rentAmt;
	/**
	 * @return the movieId
	 */
	public Integer getMovieId() {
		return movieId;
	}
	/**
	 * @param movieId the movieId to set
	 */
	public void setMovieId(Integer movieId) {
		this.movieId = movieId;
	}
	/**
	 * @return the availableCopies
	 */
	public Integer getAvailableCopies() {
		return availableCopies;
	}
	/**
	 * @param availableCopies the availableCopies to set
	 */
	public void setAvailableCopies(Integer availableCopies) {
		this.availableCopies = availableCopies;
	}
	/**
	 * @return the movieName
	 */
	public String getMovieName() {
		return movieName;
	}
	/**
	 * @param movieName the movieName to set
	 */
	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}
	/**
	 * @return the movieBanner
	 */
	public String getMovieBanner() {
		return movieBanner;
	}
	/**
	 * @param movieBanner the movieBanner to set
	 */
	public void setMovieBanner(String movieBanner) {
		this.movieBanner = movieBanner;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the releaseDate
	 */
	public String getReleaseDate() {
		return releaseDate;
	}
	/**
	 * @param releaseDate the releaseDate to set
	 */
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	/**
	 * @return the issuedDate
	 */
	public String getIssuedDate() {
		return issuedDate;
	}
	/**
	 * @param issuedDate the issuedDate to set
	 */
	public void setIssuedDate(String issuedDate) {
		this.issuedDate = issuedDate;
	}
	/**
	 * @return the dueDate
	 */
	public String getDueDate() {
		return dueDate;
	}
	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	/**
	 * @return the rentAmt
	 */
	public Float getRentAmt() {
		return rentAmt;
	}
	/**
	 * @param rentAmt the rentAmt to set
	 */
	public void setRentAmt(Float rentAmt) {
		this.rentAmt = rentAmt;
	}
	
	
}

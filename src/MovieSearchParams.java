import jakarta.servlet.http.HttpServletRequest;

public class MovieSearchParams {
    private String genre;
    private String firstChar;
    private String title;
    private String year;
    private String director;
    private String starName;

    // Sort param
    private String orderBy;

    // Pagination params
    private int perPage = 10;
    private int pageNum = 1;

    public MovieSearchParams() {}

    // Getters
    public String getGenre()     { return genre; }
    public String getFirstChar() { return firstChar; }
    public String getTitle()     { return title; }
    public String getYear()      { return year; }
    public String getDirector()  { return director; }
    public String getStarName()  { return starName; }
    public String getOrderBy()   { return orderBy; }
    public int getPerPage()      { return perPage; }
    public int getPageNum()      { return pageNum; }

    // Setters
    public void setGenre(String genre)         { this.genre = genre; }
    public void setFirstChar(String firstChar) { this.firstChar = firstChar; }
    public void setTitle(String title)         { this.title = title; }
    public void setYear(String year)           { this.year = year; }
    public void setDirector(String director)   { this.director = director; }
    public void setStarName(String starName)   { this.starName = starName; }
    public void setOrderBy(String orderBy)     { this.orderBy = orderBy; }
    public void setPerPage(int perPage)        { this.perPage = perPage; }
    public void setPageNum(int pageNum)        { this.pageNum = pageNum; }

    public static MovieSearchParams fromRequest (HttpServletRequest request) {
        MovieSearchParams params = new MovieSearchParams();

        params.setGenre(request.getParameter("genre"));
        params.setFirstChar(request.getParameter("firstChar"));
        params.setTitle(request.getParameter("title"));
        params.setYear(request.getParameter("year"));
        params.setDirector(request.getParameter("director"));
        params.setStarName(request.getParameter("star_name"));
        params.setOrderBy(request.getParameter("orderBy"));

        String perPage = request.getParameter("perPage");
        if (perPage != null && !perPage.isEmpty()) {
            params.setPerPage(Integer.parseInt(perPage));
        }

        String pageNum = request.getParameter("pageNum");
        if (pageNum != null && !pageNum.isEmpty()) {
            params.setPageNum(Integer.parseInt(pageNum));
        }

        return params;
    }
}

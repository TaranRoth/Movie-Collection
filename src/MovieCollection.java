import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
public class MovieCollection
{
  private ArrayList<Movie> movies;
  private ArrayList<Movie> baconMovies;
  private Scanner scanner;
  private ArrayList<String> actors;
  private ArrayList<String> genres;

  public MovieCollection(String fileName)
  {
    importMovieList(fileName);
    try {
      baconMovies = Bacon.loadBaconMovies();
    } catch (Exception e) {
      e.printStackTrace();
    }
    scanner = new Scanner(System.in);
    populateActors();
    populateGenres();
    Bacon.populateMoviesOfCast(baconMovies);
  }

  public ArrayList<Movie> getMovies()
  {
    return movies;
  }
  
  public void menu()
  {
    String menuOption = "";
    
    System.out.println("Welcome to the movie collection!");
    System.out.println("Total: " + movies.size() + " movies");
    
    while (!menuOption.equals("q"))
    {
      System.out.println("------------ Main Menu ----------");
      System.out.println("- search (t)itles");
      System.out.println("- search (k)eywords");
      System.out.println("- search (c)ast");
      System.out.println("- see all movies of a (g)enre");
      System.out.println("- list top 50 (r)ated movies");
      System.out.println("- list top 50 (h)igest revenue movies");
      System.out.println("- print out the (b)acon number of an actor");
      System.out.println("- (q)uit");
      System.out.print("Enter choice: ");
      menuOption = scanner.nextLine();
      
      if (!menuOption.equals("q"))
      {
        processOption(menuOption);
      }
    }
  }
  
  private void processOption(String option)
  {
    if (option.equals("t"))
    {
      searchTitles();
    }
    else if (option.equals("c"))
    {
      searchCast();
    }
    else if (option.equals("k"))
    {
      searchKeywords();
    }
    else if (option.equals("g"))
    {
      listGenres();
    }
    else if (option.equals("r"))
    {
      listHighestRated();
    }
    else if (option.equals("h"))
    {
      listHighestRevenue();
    }
    else if (option.equals("b")) {
      baconSearch();
      
    }
    else
    {
      System.out.println("Invalid choice!");
    }
  }
  
  private void baconSearch() {
    System.out.print("Enter a cast member here: ");
    String castMember = scanner.nextLine();
    System.out.println("Searching, please wait....");
    int baconNumber = Bacon.baconSearch(castMember);
    if (baconNumber == 7) System.out.println("You either spelled the name wrong or that cast member has no connection to Kevin Bacon.");
    else System.out.println("The Bacon number of " + castMember + " is " + baconNumber);
  }

  private void searchTitles()
  {
    System.out.print("Enter a title search term: ");
    String searchTerm = scanner.nextLine();
    
    // prevent case sensitivity
    searchTerm = searchTerm.toLowerCase();
    
    // arraylist to hold search results
    ArrayList<Movie> results = new ArrayList<Movie>();
    
    // search through ALL movies in collection
    for (int i = 0; i < movies.size(); i++)
    {
      String movieTitle = movies.get(i).getTitle();
      movieTitle = movieTitle.toLowerCase();
      
      if (movieTitle.indexOf(searchTerm) != -1)
      {
        //add the Movie objest to the results list
        results.add(movies.get(i));
      }
    }
    showResults(results, true, false, false);
  }
  
  private void sortResults(ArrayList<Movie> listToSort)
  {
    for (int j = 1; j < listToSort.size(); j++)
    {
      Movie temp = listToSort.get(j);
      String tempTitle = temp.getTitle();
      
      int possibleIndex = j;
      while (possibleIndex > 0 && tempTitle.compareTo(listToSort.get(possibleIndex - 1).getTitle()) < 0)
      {
        listToSort.set(possibleIndex, listToSort.get(possibleIndex - 1));
        possibleIndex--;
      }
      listToSort.set(possibleIndex, temp);
    }
  }

  public static void sortStrings(ArrayList<String> listToSort)
  {
    for (int j = 1; j < listToSort.size(); j++)
    {
      String temp = listToSort.get(j);
      
      int possibleIndex = j;
      while (possibleIndex > 0 && temp.compareTo(listToSort.get(possibleIndex - 1)) < 0)
      {
        listToSort.set(possibleIndex, listToSort.get(possibleIndex - 1));
        possibleIndex--;
      }
      listToSort.set(possibleIndex, temp);
    }
  }
  
  private void displayMovieInfo(Movie movie)
  {
    System.out.println();
    System.out.println("Title: " + movie.getTitle());
    System.out.println("Tagline: " + movie.getTagline());
    System.out.println("Runtime: " + movie.getRuntime() + " minutes");
    System.out.println("Year: " + movie.getYear());
    System.out.println("Directed by: " + movie.getDirector());
    System.out.println("Cast: " + movie.getCast());
    System.out.println("Overview: " + movie.getOverview());
    System.out.println("User rating: " + movie.getUserRating());
    System.out.println("Box office revenue: " + movie.getRevenue());
  }
  
  private void searchCast()
  {
    System.out.print("Enter a cast search term: ");
    String search = scanner.nextLine().toLowerCase();

    ArrayList<String> castResults = new ArrayList<String>();
    
    for (String s: actors) {
      if (s.toLowerCase().indexOf(search.toLowerCase()) != -1) castResults.add(s);
    }
    sortStrings(castResults);
    int c = 1;
    for (String s: castResults) {
      System.out.println(c + ". " + s);
      c++;
    }
    System.out.print("Which actor are you looking for? ");
    int selectedIndex = scanner.nextInt() - 1;
    String selectedActor = castResults.get(selectedIndex);
    System.out.println();

    ArrayList<Movie> results = new ArrayList<Movie>();
    for (Movie m: movies) {
      if (m.getCast().indexOf(selectedActor) != -1) results.add(m);
    }

    showResults(results, true, false, false);
  }

  private void populateActors() {
    actors = new ArrayList<String>();
    for (Movie m: movies) {
      String[] castMembers = m.getCast().split("\\|");
      for (String s: castMembers) {
        boolean alreadyInList = false;
        for (String s2: actors) {
          if (s.equals(s2)) alreadyInList = true;
        }
        if (!alreadyInList) actors.add(s);
      }
    }
    sortStrings(actors);
  }

  private void populateGenres() {
    genres = new ArrayList<String>();
    for (Movie m: movies) {
      String[] movieGenres = m.getGenres().split("\\|");
      for (String s: movieGenres) {
        boolean inList = false;
        for (String s2: genres) {
          if (s.equals(s2)) inList = true;
        }
        if (!inList) genres.add(s);
      }
    }
    sortStrings(genres);
  }

  private void searchKeywords()
  {
    System.out.print("Enter a keyword search term: ");
    String search = scanner.nextLine().toLowerCase();

    ArrayList<Movie> results = new ArrayList<Movie>();

    for (Movie m: movies) {
      String[] keywords = m.getKeywords().split("\\|");
      for (String s: keywords) {
        if (s.equals(search)) results.add(m);
      }
    }
    showResults(results, true, false, false);
  }

  private void showResults(ArrayList<Movie> results, boolean sort, boolean showRatings, boolean showRevenue) {
    if (sort) sortResults(results);
    int c = 1;
    for (Movie m: results) {
      if (showRatings) System.out.println(c + ". " + m.getTitle() + ": " + m.getUserRating());
      else if (showRevenue) System.out.println(c + ". " + m.getTitle() + ": " + m.getRevenue());
      else System.out.println(c + ". " + m.getTitle());
      c++;
    }

    System.out.println("Which movie would you like to learn more about?");
    System.out.print("Enter number: ");
    int selectedIndex = scanner.nextInt() - 1;

    System.out.println();
    displayMovieInfo(results.get(selectedIndex));

    System.out.println("\n ** Press Enter to Return to Main Menu **");
    scanner.nextLine();
  }
  
  private void listGenres()
  {
    int c = 1;
    for (String s: genres) {
      System.out.println(c + ". " + s);
      c++;
    }
    
    System.out.println("Which genre are you looking for? ");
    System.out.print("Enter number: ");
    int genre = scanner.nextInt();

    String selectedGenre = genres.get(genre - 1);

    ArrayList<Movie> results = new ArrayList<Movie>();

    for (Movie m: movies) {
      if (m.getGenres().indexOf(selectedGenre) != -1) results.add(m);
    }
    showResults(results, true, false, false);
  }
  
  private void listHighestRated()
  {
    ArrayList<Movie> highestRatedMovies = new ArrayList<Movie>();
    ArrayList<Movie> mutableMovies = (ArrayList<Movie>) movies.clone();
    for (int i = 0; i < 50; i++) {
      int index = findHighestMeasureMovieIndex(mutableMovies, true);
      highestRatedMovies.add(mutableMovies.remove(index));
    }
    showResults(highestRatedMovies, false, true, false);
  }

  private int findHighestMeasureMovieIndex(ArrayList<Movie> movies, boolean isRating) {
    double highestMeasure;
    if (isRating) highestMeasure = movies.get(0).getUserRating();
    else highestMeasure = movies.get(0).getRevenue();
    int highestMeasureMovieIndex = 0;
    for (int i = 1; i < movies.size(); i++) {
      double measure;
      if (isRating) measure = movies.get(i).getUserRating();
      else measure = movies.get(i).getRevenue();
      if (measure > highestMeasure) {
        highestMeasureMovieIndex = i;
        highestMeasure = measure;
      }
    }
    return highestMeasureMovieIndex;
  }
  
  private void listHighestRevenue()
  {
    ArrayList<Movie> highestRevenueMovies =  new ArrayList<Movie>();
    ArrayList<Movie> mutableMovies = (ArrayList<Movie>) movies.clone();
    for (int i = 0; i < 50; i++) {
      int index = findHighestMeasureMovieIndex(mutableMovies, false);
      highestRevenueMovies.add(mutableMovies.remove(index));
    }
    showResults(highestRevenueMovies, false, false, true);
  }
  
  private void importMovieList(String fileName)
  {
    try
    {
      FileReader fileReader = new FileReader(fileName);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      String line = bufferedReader.readLine();
      
      movies = new ArrayList<Movie>();
      
      while ((line = bufferedReader.readLine()) != null) 
      {
        String[] movieFromCSV = line.split(",");
     
        String title = movieFromCSV[0];
        String cast = movieFromCSV[1];
        String director = movieFromCSV[2];
        String tagline = movieFromCSV[3];
        String keywords = movieFromCSV[4];
        String overview = movieFromCSV[5];
        int runtime = Integer.parseInt(movieFromCSV[6]);
        String genres = movieFromCSV[7];
        double userRating = Double.parseDouble(movieFromCSV[8]);
        int year = Integer.parseInt(movieFromCSV[9]);
        int revenue = Integer.parseInt(movieFromCSV[10]);
        
        Movie nextMovie = new Movie(title, cast, director, tagline, keywords, overview, runtime, genres, userRating, year, revenue);
        movies.add(nextMovie);  
      }
      bufferedReader.close();
    }
    catch(IOException exception)
    {
      // Print out the exception that occurred
      System.out.println("Unable to access " + exception.getMessage());              
    }
  }
}
public class MovieCollectionRunner
{
    public static void main(String arg[])
    {
        MovieCollection myCollection = new MovieCollection("data/movies_data.csv");
        myCollection.menu();
    }
}
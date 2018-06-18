package clienteOdysseyV2;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javafx.scene.image.Image;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Metadata de una cancion
 */
public class Metadata extends RecursiveTreeObject<Metadata> {
    /**
     * Lista de generos de musica soportados por ID3
     */
    private static final String[] genres =
            {"Blues",
            "Classic Rock",
            "Country",
            "Dance",
            "Disco",
            "Funk",
            "Grunge",
            "Hip-Hop",
            "Jazz",
            "Metal",
            "New Age",
            "Oldies",
            "Other",
            "Pop",
            "R&B",
            "Rap",
            "Reggae",
            "Rock",
            "Techno",
            "Industrial",
            "Alternative",
            "Ska",
            "Death Metal",
            "Pranks",
            "Soundtrack",
            "Euro-Techno",
            "Ambient",
            "Trip-Hop",
            "Vocal",
            "Jazz+Funk",
            "Fusion",
            "Trance",
            "Classical",
            "Instrumental",
            "Acid",
            "House",
            "Game",
            "Sound Clip",
            "Gospel",
            "Noise",
            "Alternative Rock",
            "Bass",
            "Soul",
            "Punk",
            "Space",
            "Meditative",
            "Instrumental Pop",
            "Instrumental Rock",
            "Ethnic",
            "Gothic",
            "Darkwave",
            "Techno-Industrial",
            "Electronic",
            "Pop-Folk",
            "Eurodance",
            "Dream",
            "Southern Rock",
            "Comedy",
            "Cult",
            "Gangsta Rap",
            "Top 40",
            "Christian Rap",
            "Pop/Funk",
            "Jungle",
            "Native American",
            "Cabaret",
            "New Wave",
            "Psychedelic",
            "Rave",
            "Showtunes",
            "Trailer",
            "Lo-Fi",
            "Tribal",
            "Acid Punk",
            "Acid Jazz",
            "Polka",
            "Retro",
            "Musical",
            "Rock & Roll",
            "Hard Rock",
            "Folk",
            "Folk-Rock",
            "National Folk",
            "Swing",
            "Fast Fusion",
            "Bebob",
            "Latin",
            "Revival",
            "Celtic",
            "Bluegrass",
            "Avantgarde",
            "Gothic Rock",
            "Progressive Rock",
            "Psychedelic Rock",
            "Symphonic Rock",
            "Slow Rock",
            "Big Band",
            "Chorus",
            "Easy Listening",
            "Acoustic",
            "Humour",
            "Speech",
            "Chanson",
            "Opera",
            "Chamber Music",
            "Sonata",
            "Symphony",
            "Booty Bass",
            "Primus",
            "Porn Groove",
            "Satire",
            "Slow Jam",
            "Club",
            "Tango",
            "Samba",
            "Folklore",
            "Ballad",
            "Power Ballad",
            "Rhytmic Soul",
            "Freestyle",
            "Duet",
            "Punk Rock",
            "Drum Solo",
            "Acapella",
            "Euro-House",
            "Dance Hall",
            "Goa",
            "Drum & Bass",
            "Club-House",
            "Hardcore",
            "Terror",
            "Indie",
            "BritPop",
            "Negerpunk",
            "Polsk Punk",
            "Beat",
            "Christian Gangsta",
            "Heavy Metal",
            "Black Metal",
            "Crossover",
            "Contemporary C",
            "Christian Rock",
            "Merengue",
            "Salsa",
            "Thrash Metal",
            "Anime",
            "JPop",
            "SynthPop"};

    /**
     * Nombre de la cancion
     */
    public String name = "";
    /**
     * Nombre del artista
     */
    public String artist = "";
    /**
     * Anno de publicacion
     */
    public String year = "";
    /**
     * Album de la cancion
     */
    public String album = "";
    /**
     * Genero al que pertenece
     */
    public String genre = "";
    /**
     * Letra de la cancion
     */
    public String lyrics = "";
    /**
     * Imagen de portada
     */
    public Image cover;


    /**
     * Contructor desde archivo
     * @param path Direccion de la cancion en disco
     */
    public Metadata(String path){
        try {
            Mp3File mp3File = new Mp3File(path);
            if(mp3File.hasId3v1Tag()){
                ID3v1 tag = mp3File.getId3v1Tag();
                name += tag.getTitle();
                artist += tag.getArtist();
                year += tag.getYear();
                album += tag.getAlbum();
                genre += genres[tag.getGenre()];

            }else if(mp3File.hasId3v2Tag()){
                ID3v2 tag = mp3File.getId3v2Tag();
                name += tag.getTitle();
                artist += tag.getArtist();
                year += tag.getYear();
                album += tag.getAlbum();
                int genreId = tag.getGenre();
                if(genreId > -1 && genreId < genres.length) {
                    genre = genres[tag.getGenre()];
                }else{
                    genre = "Unknown";
                }
                byte[] image = tag.getAlbumImage();
                if(image != null) {
                    cover = new Image(new ByteArrayInputStream(tag.getAlbumImage()));
                }
            }else{
                System.out.println("Other tag");
            }


        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Constructor por defecto
     */
    public Metadata(){}

    /**
     * Anadir letra de la cancion desde el API de ChartLyrics
     */
    public void addLyrics(){
        String BASE_URL = "http://api.chartlyrics.com/apiv1.asmx/SearchLyricDirect";

        try {
            String parameters = "?artist=" + URLEncoder.encode(artist, "UTF-8") + "&song=" + URLEncoder.encode(name, "UTF-8");
            URL url = new URL(BASE_URL + parameters);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            StringBuffer content = new StringBuffer();

            int size;
            char[] buf = new char[4096];
            while ((size = in.read(buf)) != -1) {
                content.append(new String(buf, 0, size));
            }
            in.close();

            Document document = DocumentHelper.parseText(content.toString());
            Element root = document.getRootElement();
            String lyrics = root.elementIterator("Lyric").next().getText();
            this.lyrics = lyrics;

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
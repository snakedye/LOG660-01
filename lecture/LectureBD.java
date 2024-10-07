import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

import org.xmlpull.v1.*;

public class LectureBD {

    private Connection connection;

    // Inner class to represent roles in films
    public class Role {
        public Role(int i, String n, String p) {
            id = i;
            nom = n;
            personnage = p;
        }
        protected int id;
        protected String nom;
        protected String personnage;
    }

    public LectureBD() {
        connectionBD();  // Establish the database connection
    }

    // Connect to the database
    private void connectionBD() {
        // On se connecte a la BD
        String url =
                "jdbc:oracle:thin:@//bdlog660.ens.ad.etsmtl.ca:1521/ORCLPDB.ens.ad.etsmtl.ca";
        String user = "EQUIPE210";
        String password = "XFC5ioxE";

        try {
            // Charger le driver Oracle JDBC
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Établir la connexion
            connection = DriverManager.getConnection(url, user, password);
            System.out.println(
                    "Connexion à la base de données établie avec succès !"
            );
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC Oracle non trouvé !");
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            System.err.println(
                    "Erreur lors de la connexion à la base de données !"
            );
            e.printStackTrace();
        }
    }

    public void lecturePersonnes(String nomFichier) {
        try (InputStream is = new FileInputStream(nomFichier)) {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(is, null);

            int eventType = parser.getEventType();
            String tag = null, nom = null, anniversaire = null, lieu = null, photo = null, bio = null;
            int id = -1;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tag = parser.getName();
                    if (tag.equals("personne") && parser.getAttributeCount() == 1)
                        id = Integer.parseInt(parser.getAttributeValue(0));
                } else if (eventType == XmlPullParser.END_TAG) {
                    tag = null;
                    if (parser.getName().equals("personne") && id >= 0) {
                        // Insert the parsed Personne into the database
                        System.out.println(id + ": "+ anniversaire);
                        insertionPersonne(id, nom, anniversaire, lieu, photo, bio);
                        id = -1;
                        nom = null; anniversaire = null; lieu = null; photo = null; bio = null;
                    }
                } else if (eventType == XmlPullParser.TEXT && id >= 0) {
                    if (tag != null) {
                        switch (tag) {
                            case "nom": nom = parser.getText(); break;
                            case "anniversaire": anniversaire = parser.getText(); break;
                            case "lieu": lieu = parser.getText(); break;
                            case "photo": photo = parser.getText(); break;
                            case "bio": bio = parser.getText(); break;
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public void lectureFilms(String nomFichier) {
        try (InputStream is = new FileInputStream(nomFichier)) {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(is, null);

            int eventType = parser.getEventType();
            String tag = null, titre = null, langue = null, resume = null, poster = null;
            ArrayList<String> pays = new ArrayList<>();
            ArrayList<String> genres = new ArrayList<>();
            ArrayList<String> scenaristes = new ArrayList<>();
            ArrayList<Role> roles = new ArrayList<>();
            int id = -1, annee = -1, duree = -1, roleId = -1;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tag = parser.getName();
                    if (tag.equals("film") && parser.getAttributeCount() == 1)
                        id = Integer.parseInt(parser.getAttributeValue(0));
                    else if (tag.equals("realisateur") && parser.getAttributeCount() == 1)
                        roleId = Integer.parseInt(parser.getAttributeValue(0));
                } else if (eventType == XmlPullParser.END_TAG) {
                    tag = null;
                    if (parser.getName().equals("film") && id >= 0) {
                        // Insert the parsed Film into the database
                        insertionFilm(id, titre, annee, pays, langue, duree, resume, genres, roles, poster);
                        id = -1; annee = -1; duree = -1; titre = null; langue = null; resume = null; poster = null;
                        pays.clear(); genres.clear(); scenaristes.clear(); roles.clear();
                    }
                } else if (eventType == XmlPullParser.TEXT && id >= 0) {
                    if (tag != null) {
                        switch (tag) {
                            case "titre": titre = parser.getText(); break;
                            case "annee": annee = Integer.parseInt(parser.getText()); break;
                            case "pays": pays.add(parser.getText()); break;
                            case "langue": langue = parser.getText(); break;
                            case "duree": duree = Integer.parseInt(parser.getText()); break;
                            case "resume": resume = parser.getText(); break;
                            case "genre": genres.add(parser.getText()); break;
                            case "acteur": roles.add(new Role(roleId, parser.getText(), null)); break;
                        }
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }

    public void lectureClients(String nomFichier) {
        try (InputStream is = new FileInputStream(nomFichier)) {
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(is, null);

            int eventType = parser.getEventType();
            String tag = null, nomFamille = null, prenom = null, courriel = null, tel = null, anniv = null;
            int id = -1, expMois = -1, expAnnee = -1;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    tag = parser.getName();
                    if (tag.equals("client") && parser.getAttributeCount() == 1)
                        id = Integer.parseInt(parser.getAttributeValue(0));
                } else if (eventType == XmlPullParser.END_TAG) {
                    tag = null;
                    if (parser.getName().equals("client") && id >= 0) {
                        // Insert client into the database
                        System.out.println(id + ": "+ prenom + " " + nomFamille);
                        insertionClient(id, nomFamille, prenom, courriel, tel, anniv, expMois, expAnnee);

                        // Reset variables after insertion
                        id = -1;
                        nomFamille = null;
                        prenom = null;
                        courriel = null;
                        tel = null;
                        anniv = null;
                        expMois = -1;
                        expAnnee = -1;
                    }
                } else if (eventType == XmlPullParser.TEXT && id >= 0) {
                    if (tag != null) {
                        switch (tag) {
                            case "nom-famille": nomFamille = parser.getText(); break;
                            case "prenom": prenom = parser.getText(); break;
                            case "courriel": courriel = parser.getText(); break;
                            case "tel": tel = parser.getText(); break;
                            case "anniversaire": anniv = parser.getText(); break;
                            case "exp-mois": expMois = Integer.parseInt(parser.getText()); break;
                            case "exp-annee": expAnnee = Integer.parseInt(parser.getText()); break;
                        }
                    }
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
    }


    private void insertionPersonne(int id, String nom, String anniv, String lieu, String photo, String bio) {
        // Placeholder values for the missing fields
        String prenom = "N/A";                      // Sample first name
        String email = UUID.randomUUID().toString();        // Sample email
        String telephone = "N/A";            // Sample phone number
        String adresse_Num_Civique = "N/A";           // Sample address number
        String adresse_Ville = "N/A";         // Sample city
        String adresse_Province = "N/A";               // Sample province
        String adresse_Code_Postal = "N/A";         // Sample postal code
        String motDePasse = "password123";            // Sample password

        // SQL insertion statement that includes every non-null field from the `Personnes` table
        String insertSQL = "INSERT INTO Personnes (id_Personne, prenom, nom, email, telephone, adresse_Num_Civique, adresse_Rue, adresse_Ville, adresse_Province, adresse_Code_Postal, date_Naissance, mot_De_Passe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setInt(1, id);                         // Setting the primary key
            pstmt.setString(2, prenom);                  // Filling with gibberish value for prenom
            pstmt.setString(3, nom);                     // Actual parsed value for `nom`
            pstmt.setString(4, email);                   // Filling with a sample email address
            pstmt.setString(5, telephone);               // Filling with a sample telephone number
            pstmt.setString(6, adresse_Num_Civique);      // Filling with a sample address number
            pstmt.setString(7, lieu);             // Filling with a sample street name
            pstmt.setString(8, adresse_Ville);           // Filling with a sample city name
            pstmt.setString(9, adresse_Province);        // Filling with a sample province
            pstmt.setString(10, adresse_Code_Postal);    // Filling with a sample postal code
            pstmt.setDate(11, Date.valueOf(anniv));      // Convert parsed String anniversary to SQL Date
            pstmt.setString(12, motDePasse);             // Filling with a sample password

            // Execute the SQL insertion statement
            pstmt.executeUpdate();
            System.out.println("Inserted person: " + prenom + " " + nom);
        } catch (SQLException e) {
            System.out.println("Error inserting person: " + nom);
            e.printStackTrace();
        }
    }

    private void insertionFilm(int id, String titre, int annee, ArrayList<String> pays, String langue, int duree, String resume, ArrayList<String> genres, ArrayList<Role> roles, String poster) {
        // Prepare and execute SQL insertion using PreparedStatement
    }

    private void insertionClient(int id, String nomFamille, String prenom, String courriel, String tel, String anniv, int expMois, int expAnnee) {
        // Define a sample set of values for fields not provided in the method signature
        String typeCarte = "N/A";  // Credit card type (default value)
        String numeroCarte = "N/A"; // Simulated 16-digit credit card number
        String cvv = "N/A";  // Simulated CVV value
        int id_Personne = id; // Use the same id for `id_Personne` reference to simplify

        // Insert into `Clients` table
        String insertClientSQL = "INSERT INTO Clients (id_Client, id_Personne, id_Forfait, type_Carte_Credit, numero_Carte_Credit, mois_Expiration_Carte, annee_Expiration_Carte, cvv_Carte_Credit) VALUES (?, ?, NULL, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmtClient = connection.prepareStatement(insertClientSQL)) {
            // Set parameters for the `Clients` table
            pstmtClient.setInt(1, id);                   // Set the client ID
            pstmtClient.setInt(2, id_Personne);           // Use the same ID for `id_Personne`
            pstmtClient.setString(3, typeCarte);          // Default credit card type
            pstmtClient.setString(4, numeroCarte);        // Simulated credit card number
            pstmtClient.setInt(5, expMois);               // Expiration month from the XML
            pstmtClient.setInt(6, expAnnee);              // Expiration year from the XML
            pstmtClient.setString(7, cvv);                // Simulated CVV value

            // Execute the insertion
            pstmtClient.executeUpdate();
            System.out.println("Inserted into Clients: " + prenom + " " + nomFamille);
        } catch (SQLException e) {
            System.out.println("Error inserting into Clients: " + prenom + " " + nomFamille);
            e.printStackTrace();
        }    }

    public static void main(String[] args) {
        LectureBD lecture = new LectureBD();
        lecture.lecturePersonnes(args[0]);
        lecture.lectureFilms(args[1]);
        lecture.lectureClients(args[2]);
    }
}

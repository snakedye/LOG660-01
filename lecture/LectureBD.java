
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

import org.xmlpull.v1.*;

public class LectureBD {

    private Connection connection;


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
        connectionBD(); 
    }

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

    public void lectureFilms(String nomFichier){
        try {
           XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
           XmlPullParser parser = factory.newPullParser();
  
           InputStream is = new FileInputStream(nomFichier);
           parser.setInput(is, null);
  
           int eventType = parser.getEventType();
  
           String tag = null, 
                  titre = null,
                  langue = null,
                  poster = null,
                  roleNom = null,
                  rolePersonnage = null,
                  realisateurNom = null,
                  resume = null;
           
           ArrayList<String> pays = new ArrayList<String>();
           ArrayList<String> genres = new ArrayList<String>();
           ArrayList<String> scenaristes = new ArrayList<String>();
           ArrayList<Role> roles = new ArrayList<Role>();         
           ArrayList<String> annonces = new ArrayList<String>();
           
           int id = -1,
               annee = -1,
               duree = -1,
               roleId = -1,
               realisateurId = -1;
           
           while (eventType != XmlPullParser.END_DOCUMENT) 
           {
              if(eventType == XmlPullParser.START_TAG) 
              {
                 tag = parser.getName();
                 
                 if (tag.equals("film") && parser.getAttributeCount() == 1)
                    id = Integer.parseInt(parser.getAttributeValue(0));
                 else if (tag.equals("realisateur") && parser.getAttributeCount() == 1)
                    realisateurId = Integer.parseInt(parser.getAttributeValue(0));
                 else if (tag.equals("acteur") && parser.getAttributeCount() == 1)
                    roleId = Integer.parseInt(parser.getAttributeValue(0));
              } 
              else if (eventType == XmlPullParser.END_TAG) 
              {                              
                 tag = null;
                 
                 if (parser.getName().equals("film") && id >= 0)
                 {
                    insertionFilm(id,titre,annee,pays,langue,
                               duree,resume,genres,realisateurNom,
                               realisateurId, scenaristes,
                               roles,poster,annonces);
                                      
                    id = -1;
                    annee = -1;
                    duree = -1;
                    titre = null;                                 
                    langue = null;                  
                    poster = null;
                    resume = null;
                    realisateurNom = null;
                    roleNom = null;
                    rolePersonnage = null;
                    realisateurId = -1;
                    roleId = -1;
                    
                    genres.clear();
                    scenaristes.clear();
                    roles.clear();
                    annonces.clear();  
                    pays.clear();
                 }
                 if (parser.getName().equals("role") && roleId >= 0) 
                 {              
                    roles.add(new Role(roleId, roleNom, rolePersonnage));
                    roleId = -1;
                    roleNom = null;
                    rolePersonnage = null;
                 }
              }
              else if (eventType == XmlPullParser.TEXT && id >= 0) 
              {
                 if (tag != null)
                 {                                    
                    if (tag.equals("titre"))
                       titre = parser.getText();
                    else if (tag.equals("annee"))
                       annee = Integer.parseInt(parser.getText());
                    else if (tag.equals("pays"))
                       pays.add(parser.getText());
                    else if (tag.equals("langue"))
                       langue = parser.getText();
                    else if (tag.equals("duree"))                 
                       duree = Integer.parseInt(parser.getText());
                    else if (tag.equals("resume"))                 
                       resume = parser.getText();
                    else if (tag.equals("genre"))
                       genres.add(parser.getText());
                    else if (tag.equals("realisateur"))
                       realisateurNom = parser.getText();
                    else if (tag.equals("scenariste"))
                       scenaristes.add(parser.getText());
                    else if (tag.equals("acteur"))
                       roleNom = parser.getText();
                    else if (tag.equals("personnage"))
                       rolePersonnage = parser.getText();
                    else if (tag.equals("poster"))
                       poster = parser.getText();
                    else if (tag.equals("annonce"))
                       annonces.add(parser.getText());                  
                 }              
              }
              
              eventType = parser.next();            
           }
        }
        catch (XmlPullParserException e) {
            System.out.println(e);   
        }
        catch (IOException e) {
           System.out.println("IOException while parsing " + nomFichier); 
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
                        System.out.println(id + ": "+ prenom + " " + nomFamille + ", " + anniv);
                        insertionClient(id, nomFamille, prenom, courriel, tel, anniv, expMois, expAnnee);

                
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
        
        String prenom = "N/A";                      
        String email = UUID.randomUUID().toString();        
        String telephone = "N/A";            
        String adresse_Num_Civique = "N/A";           
        String adresse_Ville = "N/A";         
        String adresse_Province = "N/A";               
        String adresse_Code_Postal = "N/A";         
        String motDePasse = "password123";            
        Date date = (anniv != null) ? (Date.valueOf(anniv)) : null;


        String insertSQL = "INSERT INTO Personnes (id_Personne, prenom, nom, email, telephone, adresse_Num_Civique, adresse_Rue, adresse_Ville, adresse_Province, adresse_Code_Postal, date_Naissance, mot_De_Passe) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertSQL)) {
            pstmt.setInt(1, id);                    
            pstmt.setString(2, prenom);                  
            pstmt.setString(3, nom);          
            pstmt.setString(4, email);                  
            pstmt.setString(5, telephone);     
            pstmt.setString(6, adresse_Num_Civique);
            pstmt.setString(7, lieu);          
            pstmt.setString(8, adresse_Ville);         
            pstmt.setString(9, adresse_Province);     
            pstmt.setString(10, adresse_Code_Postal);    
            pstmt.setDate(11, date);      
            pstmt.setString(12, motDePasse);        

            // Execute the SQL insertion statement
            pstmt.executeUpdate();
            System.out.println("Inserted person: " + prenom + " " + nom);
        } catch (SQLException e) {
            System.out.println("Error inserting person: " + nom);
            e.printStackTrace();
        }
    }

    private void insertionFilm(int id, String titre, int annee, ArrayList<String> pays, String langue, int duree,
                           String resume, ArrayList<String> genres, String realisateurNom, int realisateurId,
                           ArrayList<String> scenaristes, ArrayList<Role> roles, String poster,
                           ArrayList<String> annonces) {

        String sqlFilm = "INSERT INTO FILMS (TITRE, ANNEE, RESUME, DUREE_MINUTE, ID_LANGUE) VALUES (?, ?, ?, ?, ?)";
        String sqlPays = "INSERT INTO FILMS_PAYS (ID_FILM, ID_PAYS) VALUES (?, ?)";
        String sqlGenre = "INSERT INTO FILMS_GENRES (ID_FILM, ID_GENRE) VALUES (?, ?)";
        String sqlRole = "INSERT INTO FILMS_ROLES (ID_FILM, ID_PROFESSIONNEL, PERSONNAGE) VALUES (?, ?, ?)";

        try {
            // Récupérer l'ID de la langue pour insertion
            int langueId = getLangueId(langue);
            if (langueId == -1) {
                System.err.println("Erreur : Langue inconnue " + langue);
                return;
            }

            // Insertion du film
            try (PreparedStatement pstmt = connection.prepareStatement(sqlFilm, new String[]{"ID_FILM"})) {
                pstmt.setString(1, titre);
                pstmt.setInt(2, annee);
                pstmt.setString(3, resume);
                pstmt.setInt(4, duree);
                pstmt.setInt(5, langueId);

                pstmt.executeUpdate();

                // Récupérer l'ID généré du film
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    id = generatedKeys.getInt(1);
                    System.out.println("Film inséré avec succès, ID généré : " + id);
                } else {
                    throw new SQLException("L'insertion du film a échoué, aucun ID généré.");
                }
            }

            // Insertion des pays
            for (String paysNom : pays) {
                int paysId = getPaysId(paysNom);
                if (paysId > 0) {
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlPays)) {
                        pstmt.setInt(1, id);
                        pstmt.setInt(2, paysId);
                        pstmt.executeUpdate();
                    }
                }
            }

            // Insertion des genres
            for (String genreNom : genres) {
                int genreId = getGenreId(genreNom);
                if (genreId > 0) {
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlGenre)) {
                        pstmt.setInt(1, id);
                        pstmt.setInt(2, genreId);
                        pstmt.executeUpdate();
                    }
                }
            }

            // Insertion des rôles
            for (Role role : roles) {
                int professionnelId = getProfessionnelId(role.nom);
                if (professionnelId == -1) {
                    System.err.println("Erreur : Professionnel inconnu " + role.nom);
                    continue;
                }

                try (PreparedStatement pstmt = connection.prepareStatement(sqlRole)) {
                    pstmt.setInt(1, id);
                    pstmt.setInt(2, professionnelId);
                    pstmt.setString(3, role.personnage);
                    pstmt.executeUpdate();
                }
            }

            System.out.println("Tous les détails du film ont été insérés avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'insertion du film dans la base de données");
            e.printStackTrace();
        }
    }

    private void insertionClient(int id, String nomFamille, String prenom, String courriel, String tel, String anniv, int expMois, int expAnnee) {

        String typeCarte = "N/A";  
        String numeroCarte = UUID.randomUUID().toString(); 
        String cvv = "N/A";  

        
        String insertClientSQL = "INSERT INTO Clients (id_Client, id_Forfait, type_Carte_Credit, numero_Carte_Credit, mois_Expiration_Carte, annee_Expiration_Carte, cvv_Carte_Credit) VALUES (?, NULL, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmtClient = connection.prepareStatement(insertClientSQL)) {
            
            pstmtClient.setInt(1, id);                   
            pstmtClient.setString(2, typeCarte);          
            pstmtClient.setString(3, numeroCarte);        
            pstmtClient.setInt(4, expMois);               
            pstmtClient.setInt(5, expAnnee);              
            pstmtClient.setString(6, cvv);

            
            pstmtClient.executeUpdate();
            System.out.println("Inserted into Clients: " + prenom + " " + nomFamille);
        } catch (SQLException e) {
            System.out.println("Error inserting into Clients: " + prenom + " " + nomFamille);
            e.printStackTrace();
        }
    }

    private int getPaysId(String nomPays) {
        String sqlSelect = "SELECT ID_PAYS FROM PAYS WHERE NOM = ?";
        String sqlInsert = "INSERT INTO PAYS (NOM) VALUES (?)";

        try (PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect)) {
            // Rechercher l'ID du pays
            pstmtSelect.setString(1, nomPays);
            ResultSet rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                // Le pays existe, retourner l'ID
                return rs.getInt("ID_PAYS");
            } else {
                // Le pays n'existe pas, on l'insère
                try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert, new String[]{"ID_PAYS"})) {
                    pstmtInsert.setString(1, nomPays);
                    pstmtInsert.executeUpdate();
                    
                    // Récupérer l'ID généré
                    ResultSet generatedKeys = pstmtInsert.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche ou de l'insertion du pays : " + nomPays);
            e.printStackTrace();
        }

        return -1;
    }


    private int getGenreId(String nomGenre) {
        String sqlSelect = "SELECT ID_GENRE FROM GENRES WHERE NOM = ?";
        String sqlInsert = "INSERT INTO GENRES (NOM) VALUES (?)";
    
        try (PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect)) {
            // Rechercher l'ID du genre
            pstmtSelect.setString(1, nomGenre);
            ResultSet rs = pstmtSelect.executeQuery();
    
            if (rs.next()) {
                // Le genre existe, retourner l'ID
                return rs.getInt("ID_GENRE");
            } else {
                // Le genre n'existe pas, on l'insère
                try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert, new String[]{"ID_GENRE"})) {
                    pstmtInsert.setString(1, nomGenre);
                    pstmtInsert.executeUpdate();
                    
                    // Récupérer l'ID généré
                    ResultSet generatedKeys = pstmtInsert.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche ou de l'insertion du genre : " + nomGenre);
            e.printStackTrace();
        }
    
        return -1; 
    }


    private int getLangueId(String nomLangue) {
        String sqlSelect = "SELECT ID_LANGUE FROM LANGUES WHERE NOM = ?";
        String sqlInsert = "INSERT INTO LANGUES (NOM) VALUES (?)";
    
        try (PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect)) {
            // Rechercher l'ID de la langue
            pstmtSelect.setString(1, nomLangue);
            ResultSet rs = pstmtSelect.executeQuery();
    
            if (rs.next()) {
                // La langue existe, retourner l'ID
                return rs.getInt("ID_LANGUE");
            } else {
                // La langue n'existe pas, on l'insère
                try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert, new String[]{"ID_LANGUE"})) {
                    pstmtInsert.setString(1, nomLangue);
                    pstmtInsert.executeUpdate();
                    
                    // Récupérer l'ID généré
                    ResultSet generatedKeys = pstmtInsert.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche ou de l'insertion de la langue : " + nomLangue);
            e.printStackTrace();
        }
    
        return -1; 
    }
    


    private int getProfessionnelId(String nomComplet) {
        String sqlSelect = "SELECT ID_PROFESSIONNEL FROM PROFESSIONNELS WHERE NOM = ? AND PRENOM = ?";
        String sqlInsert = "INSERT INTO PROFESSIONNELS (NOM, PRENOM) VALUES (?, ?)";
    
        String[] nomPrenom = nomComplet.split(" ", 2);
        String prenom = nomPrenom.length > 1 ? nomPrenom[0] : "";
        String nom = nomPrenom.length > 1 ? nomPrenom[1] : nomPrenom[0];
    
        try (PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect)) {
            // Rechercher l'ID professionnel
            pstmtSelect.setString(1, nom);
            pstmtSelect.setString(2, prenom);
            ResultSet rs = pstmtSelect.executeQuery();
    
            if (rs.next()) {
                // Si le professionnel existe, on retourne l'ID
                return rs.getInt("ID_PROFESSIONNEL");
            } else {
                //Si le professionnel n'existe pas, on l'insère
                try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert, new String[]{"ID_PROFESSIONNEL"})) {
                    pstmtInsert.setString(1, nom);
                    pstmtInsert.setString(2, prenom);
                    pstmtInsert.executeUpdate();
    
                    // Récupérer l'ID généré
                    ResultSet generatedKeys = pstmtInsert.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche ou de l'insertion du professionnel : " + nomComplet);
            e.printStackTrace();
        }
    
        return -1; 
    }


    public static void main(String[] args) {
        LectureBD lecture = new LectureBD();
        lecture.lecturePersonnes(args[0]);
        lecture.lectureFilms(args[1]);
        lecture.lectureClients(args[2]);
    }
}
